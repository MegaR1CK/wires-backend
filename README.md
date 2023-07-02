# Wires API

![image](https://res.cloudinary.com/hnp4q7akq/image/upload/v1655150453/Introduction_1_miqfw0.png)

![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Бэкенд Wires - социальной сети для представителей IT-профессий. Предоставляет REST API для социальной сети.

[Спецификация на Swagger](https://app.swaggerhub.com/apis/MegaR1CK/Wires-API/1.0)

# Стек
- 100% [Kotlin](https://kotlinlang.org/)
- [Ktor](https://ktor.io/) (бэкенд-фреймворк)
- [PostgreSQL](https://www.postgresql.org/) (СУБД)
- [Exposed](https://github.com/JetBrains/Exposed) (фреймворк взаимодействия с БД)
- [Logback](https://logback.qos.ch/) (логирование)
- [Koin](https://insert-koin.io/) (внедрение зависимостей)

# Внешние сервисы
- [Cloudinary](https://cloudinary.com/) (хранилище файлов)
- [Firebase Cloud Messaging](https://firebase.google.com/) (рассылка уведомлений)

# Архитектура
Бэкенд написан в соответствии с архитектурой MVC.

Слои:
- Слой контроллеров (Controller) - отвечает за прием HTTP-запросов от клиентов, извлечение параметров из запросов, передачу параметров в нужные методы сервисов и отправку ответов.
- Слой бизнес-логики (Service) - содержит всю логику работы определенных методов API, отвечает за проверку параметров, манипуляции с данными и выбрасывание исключений.
- Слой репозиториев (Repository) - слой, представленный репозиториями, специализированными классами, которые оперируют источниками данных и решают, к какому из них обратиться. Здесь содержится логика получения данных по запросу методов API и логика преобразования данных из одного типа в другой (mappers).
- Слой источников данных (DataSource) - слой, представленный классами, отвечающими за конкретные источники данных, такие как сетевые ресурсы или база данных. Они работают напрямую с источниками данных, отправляют и получают из них информацию, передаваемую в репозитории.

# Клиентская сторона
Представлена мобильным приложением Wires, расположенным в [wires-android](https://github.com/MegaR1CK/wires-android).
