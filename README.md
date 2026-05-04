# otp-verification

## Назначение

Сервис открывает verification-ticket для пользовательской операции, генерирует одноразовый код и подтверждает операцию по введенному коду. Проект основан на Spring MVC и состоит из следующих модулей: `account`, `admin`, `otp`, `delivery`, `security`, `logging`.

## Стек технологий

- Java 17
- Gradle
- Spring MVC / Spring Boot
- PostgreSQL 17
- Spring JdbcTemplate. Доступ к PostgreSQL реализован через Spring JdbcTemplate, который использует JDBC API.
- DTO на Java record
- JWT через Auth0 java-jwt
- SLF4J + Logback
- Spring `@Scheduled`
- Angus Mail
- JSMPP 3.0.1
- OkHttp для Telegram Bot API
- Ручная Swagger UI документация на `/docs`

## База Данных

Приложение ожидает PostgreSQL 17 и базу `otp_db`.

```sql
create database otp_db;
```

Подключение настраивается в [application.yml](src/main/resources/application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/otp_db
    username: postgres
    password: postgres
```

Схема создается из [schema.sql](src/main/resources/schema.sql). Используются таблицы:

- `accounts`
- `otp_policy`
- `verification_codes`

Статусы OTP: `ACTIVE`, `EXPIRED`, `USED`.

## Запуск

```bash
gradle bootRun
```

По умолчанию API доступно на `http://localhost:8080`.

## Swagger UI

Интерактивная документация доступна по адресу `http://localhost:8080/docs`.

Проверка через Swagger UI:

1. Откройте `http://localhost:8080/docs`.
2. Выполните `POST /auth/signup` для администратора.
3. Выполните `POST /auth/signup` для обычного пользователя.
4. Выполните `POST /auth/token` и скопируйте `accessToken` из ответа.
5. Нажмите `Authorize` и введите `Bearer <accessToken>`.
6. Для пользовательских сценариев используйте token пользователя: `POST /verifications`, затем `POST /verifications/confirm`.
7. Для admin-сценариев используйте token администратора: `PATCH /management/otp-policy`, `GET /management/accounts`, `DELETE /management/accounts/{accountId}`.

## Каналы Доставки

Настройки email, SMPP и Telegram находятся в [application.yml](src/main/resources/application.yml).

Email отправляется через SMTP и Angus Mail. SMS отправляется через SMPP emulator, например SMPPsim на `localhost:2775`. Telegram использует Bot API через OkHttp.

В `application.yml` указаны тестовые параметры, поэтому в целях безопасности (или работоспособности, как в случае с Telegram ботом), рекомендуется указать свои значения для в настройках приложения.

## Фоновая Обработка

`OtpCleanupJob` раз в заданный интервал (1 минута) помечает просроченные активные коды как `EXPIRED`.

```yaml
app:
  otp:
    cleanup-delay-ms: 60000
```

## Логирование

`RequestAuditFilter` пишет подробные логи каждого HTTP-запроса: method, URI, status, duration, account id, role и request id.

Примеры событий:

- `http.request.start`
- `http.request.finish`
- `verification.ticket.opened`
- `verification.confirmation.rejected`
- `otp.cleanup.finished`

## Проверка сборки

```bash
gradle bootJar
```
