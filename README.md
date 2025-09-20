# Fliplet

---

Сервис для изучения новых слов и определений с помощью карточек. Пользователь может создавать карточки вручную или генерировать их из PDF-файлов с помощью ИИ.

---
## Стек
`Kotlin`, `Spring Boot`, `Redis`, `PostgreSQL`, `Yandex Cloud S3`, `Yandex Cloud GPT API`, `Docker`, `JWT`, `SMTP`

## API

### Auth

| Метод | URL                                  | Описание                                                   |
|-------|--------------------------------------|------------------------------------------------------------|
| POST  | `/api/auth/register`                 | Создание пользователя и отправка кода регистрации на email |
| POST  | `/api/auth/resend-verification-code` | Отправка кода регистрации на email                         |
| POST  | `/api/auth/verify-email`             | Подтверждение электронной почты                            |
| POST  | `/api/auth/login`                    | Авторизация                                                |
| POST  | `/api/auth/refresh`                  | Обновление `access` и `refresh` токенов                    |
| POST  | `/api/auth/logout`                   | Выход                                                      |
<br>

**`POST`** `/api/auth/register`
```json
{
    "email": "email@example.com",
    "password": "abcd1234"
}
```
<br>

**`POST`** `/api/auth/resend-verification-code`
```json
{
    "email": "email@example.com"
}
```
<br>

**`POST`** `/api/auth/verify-email`
```json
{
    "email": "email@example.com",
    "code": "123456"
}
```
<br>

**`POST`** `/api/auth/login`
```json
{
    "email": "email@example.com",
    "password": "abcd1234"
}
```
`Response`
```json
{
    "accessToken": "eyJ...wVA",
    "refreshToken": "eyJ...2Wg"
}
```
<br>

**`POST`** `/api/auth/refresh`
```json
{
    "refreshToken": "eyJ...2Wg"
}
```
`Response`
```json
{
    "accessToken": "eyJ...RBQ",
    "refreshToken": "eyJ...2yA"
}
```
<br>

**`POST`** `/api/auth/logout`
```json
{
    "refreshToken": "eyJ...2yA"
}
```
<br>

---
### Users

| Метод | URL                               | Описание                                                               |
|-------|-----------------------------------|------------------------------------------------------------------------|
| GET   | `/api/users/me`                   | Получение информации о текущем пользователе                            |
| GET   | `/api/users/{username}`           | Получение информации о пользователе по username                        |
| PATCH | `/api/users/me/email`             | Обновление электронной почты (необходимо подтверждение с помощью кода) |
| PATCH | `/api/users/me/name`              | Обновление имени                                                       |
| PATCH | `/api/users/me/username`          | Обновление username                                                    |
| PATCH | `/api/users/me/avatar/upload-url` | Получение ссылки для обновления изображения профиля                    |
| POST  | `/api/users/me/avatar/confirm`    | Обновление ссылки на изображение профиля в бд                          |
<br>

**`GET`** `/api/users/me`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "id": "08ec64c4-7568-4729-a84e-47c8e73e9323",
    "username": "user",
    "name": null,
    "email": "user@example.com",
    "avatarUrl": "https://storage.yandexcloud.net/fliplet/avatars/default.png"
}
```
<br>

**`GET`** `/api/users/{username}`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "username": "user",
    "name": null,
    "avatarUrl": "https://storage.yandexcloud.net/fliplet/avatars/default.png"
}
```
<br>

**`PATCH`** `/api/users/me/email`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "email": "newemail@example.com"
}
```
<br>

**`PATCH`** `/api/users/me/name`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "name": "Иван"
}
```
`Response`
```json
{
    "id": "08ec64c4-7568-4729-a84e-47c8e73e9323",
    "username": "user",
    "name": "Иван",
    "email": "newemail@example.com",
    "avatarUrl": "https://storage.yandexcloud.net/fliplet/avatars/default.png"
}
```
<br>

**`PATCH`** `/api/users/me/username`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "username": "ivan"
}
```
`Response`
```json
{
    "id": "08ec64c4-7568-4729-a84e-47c8e73e9323",
    "username": "ivan",
    "name": "Иван",
    "email": "newemail@example.com",
    "avatarUrl": "https://storage.yandexcloud.net/fliplet/avatars/default.png"
}
```
<br>

**`PATCH`** `/api/users/me/avatar`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "fileName": "avatar.jpg",
    "contentType": "image/jpg"
}
```
`Response`
```json
{
    "uploadUrl": "https://fliplet.storage.yandexcloud.net/avatars/08ec64c4-7568-4729-a84e-47c8e73e9323/avatar.jpg",
    "headers": {
        "host": "fliplet.storage.yandexcloud.net",
        "content-type": "image/jpg"
    },
    "key": "avatars/08ec64c4-7568-4729-a84e-47c8e73e9323/avatar.jpg",
    "method": "PUT"
}
```
<br>

**`PATCH`** `/api/users/me/avatar/confirm`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "key":"avatars/08ec64c4-7568-4729-a84e-47c8e73e9323/avatar.jpg"
}
```
`Response`
```json
{
    "id": "08ec64c4-7568-4729-a84e-47c8e73e9323",
    "username": "ivan",
    "name": "Иван",
    "email": "newemail@example.com",
    "avatarUrl": "https://storage.yandexcloud.net/fliplet/avatars/08ec64c4-7568-4729-a84e-47c8e73e9323/avatar.jpg"
}
```
<br>

---
### Collections

| Метод  | URL                               | Описание                                         |
|--------|-----------------------------------|--------------------------------------------------|
| POST   | `/api/collections`                | Создание коллекции                               |
| GET    | `/api/collections`                | Получение списка коллекций текущего пользователя |
| GET    | `/api/collections/{username}`     | Получение списка коллекций по username           |
| PATCH  | `/api/collections/{collectionId}` | Обновление коллекции                             |
| DELETE | `/api/collections/{collectionId}` | Удаление коллекции                               |
<br>

**`POST`** `/api/collections`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "name": "Коллекция",
    "description": "Моя первая коллекция",
    "public": false
}
```
`Response`
```json
{
    "id": "13f4911f-ad12-45f4-907b-ef3da7495128",
    "name": "Коллекция",
    "description": "Моя первая коллекция",
    "public": false
}
```
<br>

**`GET`** `/api/collections`
<br>


**Авторизация:** `Bearer <token>`

**Параметры запроса:**

| Key  | Default Value    |
|------|------------------|
| page | `0`              |
| size | `10`             |
| sort | `createdAt,desc` |

```json
{
    "content": [
    {
        "id": "13f4911f-ad12-45f4-907b-ef3da7495128",
        "name": "Коллекция",
        "description": "Моя первая коллекция",
        "public": false
    },
    {
        "id": "3680843e-46bb-42a9-a979-f01dd0ed3a15",
        "name": "Коллекция 2",
        "description": "Моя вторая коллекция",
        "public": true
    }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1,
    "totalElements": 2,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
}
```
<br>

**`GET`** `/api/collections/{username}`
<br>

**Авторизация:** `Bearer <token>`

**Параметры запроса:**

| Key  | Default Value    |
|------|------------------|
| page | `0`              |
| size | `10`             |
| sort | `createdAt,desc` |

При запросе чужих коллекций возвращает только публичные
```json
{
    "content": [
    {
        "id": "c901a200-b2d8-40a9-8f64-585193a7dcaf",
        "name": "Коллекция",
        "description": "Коллекция пользователя test",
        "public": true
    }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
}
```
<br>

**`PATCH`** `/api/collections/{collectionId}`
<br>

**Авторизация:** `Bearer <token>`

```json
{
    "name": "Обновленное название",
    "description": "Обновленное описание",
    "public": true
}
```
`Response`
```json
{
    "id": "13f4911f-ad12-45f4-907b-ef3da7495128",
    "name": "Обновленное название",
    "description": "Обновленное описание",
    "public": true
}
```
<br>

---
### Cards

| Метод  | URL                         | Описание                                        |
|--------|-----------------------------|-------------------------------------------------|
| POST   | `/api/cards`                | Создание карточки                               |
| POST   | `/api/cards/bulk`           | Создание сразу нескольких карточек              |
| POST   | `/api/cards/generate`       | Генерация списка карточек из файла с помощью AI |
| GET    | `/api/cards`                | Получение списка карточек текущего пользователя |
| GET    | `/api/cards/{collectionId}` | Получение списка карточек из коллекции          |
| GET    | `/api/cards/{cardId}`       | Получение конкретной карточки                   |
| PATCH  | `/api/cards/{cardId}`       | Обновление карточки                             |
| DELETE | `/api/cards/{cardId}`       | Удаление карточки                               |

<br>

**`POST`** `/api/cards`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "cardCollectionId": "13f4911f-ad12-45f4-907b-ef3da7495128",
    "front": "Вопрос",
    "back": "Ответ"
}
```
`Response`
```json
{
    "id": "959e8809-5e06-42d8-a8e2-04e5d7993438",
    "front": "Вопрос",
    "back": "Ответ"
}
```
<br>

**`POST`** `/api/cards/bulk`
<br>

**Авторизация:** `Bearer <token>`
```json
{
    "cards": [
        {
            "cardCollectionId": "13f4911f-ad12-45f4-907b-ef3da7495128",
            "front": "Вопрос 1",
            "back": "Ответ 1"
        },
        {
            "cardCollectionId": "13f4911f-ad12-45f4-907b-ef3da7495128",
            "front": "Вопрос 2",
            "back": "Ответ 2"
        }
    ]
}
```
`Response`
```json
[
    {
        "id": "6a2848ab-5527-4971-b234-61f2784a94f8",
        "front": "Вопрос 1",
        "back": "Ответ 1"
    },
    {
        "id": "70c24a99-d082-4912-ab67-fedcc783bcb3",
        "front": "Вопрос 2",
        "back": "Ответ 2"
    }
]
```
<br>

**`POST`** `/api/cards/generate`
<br>

**Авторизация:** `Bearer <token>`

**Content-Type:** `multipart/form-data`

**Параметры формы:**

| Параметр | Тип  | Обязательный | Описание               |
|----------|------|--------------|------------------------|
| `file`   | file | Да           | PDF-файла для загрузки |

`Response`
```json
[
    {
        "front": "Вопрос 1",
        "back": "Ответ 1"
    },
    {
        "front": "Вопрос 2",
        "back": "Ответ 2"
    }
]
```
<br>

**`GET`** `/api/cards`
<br>

**Авторизация:** `Bearer <token>`

**Параметры запроса:**

| Key  | Default Value    |
|------|------------------|
| page | `0`              |
| size | `10`             |
| sort | `createdAt,desc` |

`Response`
```json
{
    "content": [
        {
            "id": "6a2848ab-5527-4971-b234-61f2784a94f8",
            "front": "Вопрос 1",
            "back": "Ответ 1"
        },
        {
            "id": "70c24a99-d082-4912-ab67-fedcc783bcb3",
            "front": "Вопрос 2",
            "back": "Ответ 2"
        }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1,
    "totalElements": 2,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
}
```
<br>

**`GET`** `/api/cards/{collectionId}`
<br>

**Авторизация:** `Bearer <token>`

**Параметры запроса:**

| Key  | Default Value    |
|------|------------------|
| page | `0`              |
| size | `10`             |
| sort | `createdAt,desc` |

Чужие карточки можно просматривать только если они находятся в публичной коллекции

`Response`
```json
{
    "content": [
        {
            "id": "6a2848ab-5527-4971-b234-61f2784a94f8",
            "front": "Вопрос 1",
            "back": "Ответ 1"
        },
        {
            "id": "70c24a99-d082-4912-ab67-fedcc783bcb3",
            "front": "Вопрос 2",
            "back": "Ответ 2"
        }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1,
    "totalElements": 2,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
}
```
<br>

**`GET`** `/api/cards/{cardId}`
<br>

**Авторизация:** `Bearer <token>`

Чужие карточки можно просматривать только если они находятся в публичной коллекции

`Response`
```json
{
    "id": "6a2848ab-5527-4971-b234-61f2784a94f8",
    "front": "Вопрос 1",
    "back": "Ответ 1"
}
```
<br>

**`PATCH`** `/api/cards/{cardId}`
<br>

**Авторизация:** `Bearer <token>`

```json
{
    "front": "Обновленный вопрос",
    "back": "Обновленный ответ"
}
```

`Response`
```json
{
    "id": "6a2848ab-5527-4971-b234-61f2784a94f8",
    "front": "Обновленный вопрос",
    "back": "Обновленный ответ"
}
```
<br>

---
### Study

| Метод | URL                      | Описание                                    |
|-------|--------------------------|---------------------------------------------|
| GET   | `/api/study/next`        | Получение следующей карточки для повторения |
| POST  | `/api/study/review`      | Фиксация результат повторения карточки      |
<br>

**`GET`** `/api/study/next`

**Авторизация:** `Bearer <token>`

```json
{
    "id": "959e8809-5e06-42d8-a8e2-04e5d7993438",
    "front": "Вопрос",
    "back": "Ответ"
}
```
<br>

**`POST`** `/api/study/review`

**Авторизация:** `Bearer <token>`

| Quality   | Интерпретация     |
|-----------|-------------------|
| `0` - `2` | Карточка забыта   |
| `3`       | Вспомнил с трудом |
| `4` - `5` | Вспомнил уверенно |

```json
{
    "id": "959e8809-5e06-42d8-a8e2-04e5d7993438",
    "quality": 5
}
```

`Response`
```json
{
    "id": "959e8809-5e06-42d8-a8e2-04e5d7993438",
    "quality": 5,
    "nextReviewAt": "2025-09-21T04:12:11.951055852Z"
}
```
