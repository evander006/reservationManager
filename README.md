# Reservation System
Pet-проект: REST API для управления бронированиями номеров на **Java 21** и **Spring Boot**.
Сервис позволяет создавать заявки на бронирование, обновлять их, подтверждать и отменять, а также проверять конфликты по занятым номерам.
## Стек
- Java 21
- Spring Boot 4
- Spring Web (REST)
- Spring Data JPA / Hibernate
- Bean Validation
- PostgreSQL
- Gradle
## Возможности
- CRUD-операции по бронированиям
- Статусы: `PENDING` → `APPROVED` / `CANCELLED`
- Валидация дат (`@FutureOrPresent`, `endDate` строго после `startDate`)
- Подтверждение бронирования с проверкой конфликтов по `roomId` и пересечению дат
- Отмена только для заявок в статусе `PENDING`
- Глобальная обработка ошибок (`@ControllerAdvice`) с единым форматом ответа
## Структура
```
src/main/java/evaanufr/dev/reservationsystem/
├── reservations/     # domain, API, service, JPA
└── web/              # exception handling, error DTO
```
## API
Базовый путь: `/reservation`
| Метод | Endpoint | Описание |
|--------|----------|----------|
| `GET` | `/reservation/{id}` | Получить бронирование по id |
| `GET` | `/reservation?roomId=&userId=&pageSize=&pageNumber=` | Поиск/список с фильтрами |
| `POST` | `/reservation` | Создать бронирование (`PENDING`) |
| `PUT` | `/reservation/{id}` | Обновить (только `PENDING`) |
| `POST` | `/reservation/{id}/approve` | Подтвердить (проверка конфликтов) |
| `DELETE` | `/reservation/{id}/cancel` | Отменить (только `PENDING`) |
### Пример создания
```http
POST /reservation
Content-Type: application/json
{
  "userId": 1,
  "roomId": 101,
  "startDate": "2026-08-10",
  "endDate": "2026-08-15"
}
```
`id` и `reservationStatus` при создании не передаются.
### Пример ответа об ошибке
```json
{
  "msg": "BAD_REQUEST",
  "timestamp": "2026-07-28T11:37:43.9495751",
  "errorMsg": "Start date must be one day earlier than end date"
}
```
## Запуск
### Требования
- JDK 21+
- PostgreSQL
### База данных
Создайте БД `reservation` и при необходимости поправьте настройки в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reservation
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.jpa.hibernate.ddl-auto=update
```
### Приложение
```bash
./gradlew bootRun
```
По умолчанию приложение стартует на `http://localhost:8080`.
## Дальнейшее развитие
- Реальная фильтрация и пагинация в `searchAllByFilter`
- Тесты сервиса и API
- Миграции схемы (Flyway / Liquibase)
