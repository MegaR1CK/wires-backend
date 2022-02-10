package com.wires.api

import com.wires.api.users.GlobalUserTest
import kotlin.test.Test

class GlobalTest {

    @Test
    fun launchAllTests() {
        GlobalUserTest().launchAllUserTests()
    }
}
