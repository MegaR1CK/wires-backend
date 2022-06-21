package com.wires.api.service

import com.wires.api.authentication.JwtService
import com.wires.api.database.params.ImageInsertParams
import com.wires.api.database.params.PasswordUpdateParams
import com.wires.api.database.params.RefreshTokenUpdateParams
import com.wires.api.database.params.SessionDeleteParams
import com.wires.api.database.params.SessionInsertParams
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.mappers.PostsMapper
import com.wires.api.mappers.UserMapper
import com.wires.api.repository.DevicesRepository
import com.wires.api.repository.ImagesRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.SessionsRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.EmailExistsException
import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.RefreshTokenExpiredException
import com.wires.api.routing.StorageException
import com.wires.api.routing.UsernameTakenException
import com.wires.api.routing.WrongCredentialsException
import com.wires.api.routing.requestparams.PasswordChangeParams
import com.wires.api.routing.requestparams.TokenRefreshParams
import com.wires.api.routing.requestparams.UserEditParams
import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserLogoutParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.routing.respondmodels.PostResponse
import com.wires.api.routing.respondmodels.TokensResponse
import com.wires.api.routing.respondmodels.UserPreviewResponse
import com.wires.api.routing.respondmodels.UserResponse
import com.wires.api.utils.Cryptor
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class UserService : KoinComponent {

    private val userRepository: UserRepository by inject()
    private val sessionsRepository: SessionsRepository by inject()
    private val devicesRepository: DevicesRepository by inject()
    private val postsRepository: PostsRepository by inject()
    private val storageRepository: StorageRepository by inject()
    private val imagesRepository: ImagesRepository by inject()
    private val cryptor: Cryptor by inject()
    private val jwtService: JwtService by inject()
    private val userMapper: UserMapper by inject()
    private val postsMapper: PostsMapper by inject()

    suspend fun registerUser(params: UserRegisterParams) {
        when {
            userRepository.findUserByEmail(params.email) != null -> throw EmailExistsException()
            userRepository.findUserByUsername(params.username) != null -> throw UsernameTakenException()
            else -> {
                val salt = cryptor.generateSalt()
                cryptor.getBcryptHash(params.passwordHash, salt)?.let { hash ->
                    val newUser = UserInsertParams(
                        email = params.email,
                        username = params.username,
                        passwordHash = hash,
                        passwordSalt = salt
                    )
                    userRepository.registerUser(newUser)
                } ?: throw MissingArgumentsException()
            }
        }
    }

    suspend fun loginUser(params: UserLoginParams): TokensResponse {
        val currentUser = userRepository.findUserByEmail(params.email)
        if (
            currentUser == null ||
            !cryptor.checkBcryptHash(
                params.passwordHash,
                currentUser.passwordSalt,
                currentUser.passwordHash
            )
        ) throw WrongCredentialsException()
        val device = devicesRepository.findDeviceById(params.deviceId) ?: throw NotFoundException()
        val session = sessionsRepository.findSessionByIds(device.id, currentUser.id)
        if (session != null) sessionsRepository.deleteSession(SessionDeleteParams(session.deviceId, session.userId))
        val tokens = jwtService.generateTokenPair(currentUser.id)
        sessionsRepository.addSession(
            SessionInsertParams(
                deviceId = device.id,
                userId = currentUser.id,
                refreshToken = tokens.refreshToken,
                expiresAt = tokens.refreshTokenExpiresAt
            )
        )
        return TokensResponse(tokens.accessToken, tokens.refreshToken)
    }

    suspend fun refreshToken(params: TokenRefreshParams): TokensResponse {
        val currentSession = sessionsRepository.findSessionByToken(params.refreshToken) ?: throw NotFoundException()
        if (currentSession.expiresAt < System.currentTimeMillis()) throw RefreshTokenExpiredException()
        val newTokens = jwtService.generateTokenPair(currentSession.userId)
        sessionsRepository.updateRefreshToken(
            RefreshTokenUpdateParams(
                oldRefreshToken = params.refreshToken,
                newRefreshToken = newTokens.refreshToken,
                newExpiresAt = newTokens.refreshTokenExpiresAt
            )
        )
        return TokensResponse(newTokens.accessToken, newTokens.refreshToken)
    }

    suspend fun logoutUser(userId: Int, params: UserLogoutParams) {
        val currentSession = sessionsRepository.findSessionByIds(params.deviceId, userId) ?: throw NotFoundException()
        sessionsRepository.deleteSession(SessionDeleteParams(currentSession.deviceId, currentSession.userId))
    }

    suspend fun getUser(userId: Int): UserResponse {
        userRepository.findUserById(userId)?.let { user ->
            return userMapper.fromModelToResponse(user)
        } ?: throw NotFoundException()
    }

    suspend fun updateUser(userId: Int, userEditParams: UserEditParams, avatarBytes: ByteArray?) {
        val avatarUrl = avatarBytes?.let { bytes ->
            val image = storageRepository.uploadFile(bytes) ?: throw StorageException()
            imagesRepository.addImage(ImageInsertParams(image.url, image.size.width, image.size.height))
        }
        userEditParams.email?.let { mail ->
            userRepository.findUserByEmail(mail)?.let { if (it.id != userId) throw EmailExistsException() }
        }
        userEditParams.username?.let { username ->
            userRepository.findUserByUsername(username)?.let { if (it.id != userId) throw UsernameTakenException() }
        }
        userRepository.updateUser(
            UserUpdateParams(
                id = userId,
                username = userEditParams.username,
                email = userEditParams.email,
                avatarUrl = avatarUrl,
                interests = userEditParams.interests,
                firstName = userEditParams.firstName,
                lastName = userEditParams.lastName
            )
        )
        Unit
    }
    suspend fun changeUserPassword(userId: Int, params: PasswordChangeParams) {
        val currentUser = userRepository.findUserById(userId)
        if (currentUser != null &&
            cryptor.checkBcryptHash(params.oldPasswordHash, currentUser.passwordSalt, currentUser.passwordHash)
        ) {
            val newSalt = cryptor.generateSalt()
            cryptor.getBcryptHash(params.newPasswordHash, newSalt)?.let { passwordHash ->
                userRepository.changeUserPassword(
                    PasswordUpdateParams(userId, passwordHash, newSalt)
                )
            }
        } else {
            throw WrongCredentialsException()
        }
    }

    suspend fun getUserPosts(currentUserId: Int, userId: Int, limit: Int, offset: Long): List<PostResponse> {
        userRepository.findUserById(userId)?.let {
            return postsRepository.getUserPosts(userId, limit, offset)
                .map { postsMapper.fromModelToResponse(currentUserId, it) }
        } ?: throw NotFoundException()
    }

    suspend fun findUsers(query: String): List<UserPreviewResponse> {
        return userRepository.findUsersByNamePart(query).map(userMapper::fromModelToResponse)
    }
}
