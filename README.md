# Небольшой набор вспомогательных классов для Spring Boot 3.x.x Web приложений

Набор включает в себя:

1. [RestControllerAdvice Bean для обработки часто встречающихся исключений](#restcontrolleradvice)
2. [Исключение для формрование ответов API с определенныи статусом](#apiexception)
3. [Набор аннотаций для Swagger | OpenAPI, описывающие основные варианты HTTP ответов](#аннотации-для-swagger--openapi)

## RestControllerAdvice

Обработка исключений с использованием RestControllerAdvice включается добавлением аннотации 
`@EnableRestExceptionHandler` к любому классу конфигурации или основному классу приложения:

```java
@EnableRestExceptionHandler
@SpringBootApplication
public class RestUtilsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestUtilsApplication.class, args);
    }
}
```

Стандартное тело ответа с сообщением об ошибке будет иметь вид:

```json
{
  "message": "Сообщение об ошибке",
  "details": "Детали ошибки, если присутствуют"
}
```

### Обрабатываемые исключения

| Исключение                                                                          | Возвращаемый HTTP статус | Комментарий                                                                                       |
|-------------------------------------------------------------------------------------|--------------------------|---------------------------------------------------------------------------------------------------|
| `ApiException`<br/>pro.nikolaev.restutils.exceptions                                | Статус из исключения     | Все параметры ответа берутся из исключения: статус / message / details                            |
| `BadCredentialsException`<br/>org.springframework.security.authentication           | 401                      | Все поля тела ответа не изменияемые                                                               |
| `UsernameNotFoundException`<br/>org.springframework.security.core.userdetails       | 401                      | Все поля тела ответа не изменияемые                                                               |
| `AccountExpiredException`<br/>org.springframework.security.authentication           | 423                      | message - постоянное, details - из сообщения в исключении                                         |
| `DisabledException`<br/>org.springframework.security.authentication                 | 423                      | message - постоянное, details - из сообщения в исключении                                         |
| `LockedException`<br/>org.springframework.security.authentication                   | 423                      | message - постоянное, details - из сообщения в исключении                                         |
| `AuthenticationException`<br/>org.springframework.security.core                     | 401                      | message - постоянное, details - из сообщения в исключении                                         |
| `AccessDeniedException`<br/>org.springframework.security.access                     | 403                      | message - постоянное, details - из сообщения в исключении                                         |
| `HttpRequestMethodNotSupportedException`<br/>org.springframework.web                | 405                      | message - постоянное, details - отсутствует                                                       |
| `MethodArgumentNotValidException`<br/>org.springframework.web.bind _*_              | 400                      | message - постоянное, details - сообщение с информацией по параметру, который не прошел валидацию |
| `HttpMessageNotReadableException`<br/>org.springframework.http.converter            | 400                      | message - постоянное, details - из сообщения в исключении                                         |
| `MethodArgumentTypeMismatchException`<br/>org.springframework.web.method.annotation | 400                      | message - постоянное, details - сообщение с информацией по параметру не верного типа              |
| `HttpMediaTypeNotAcceptableException`<br/>org.springframework.web                   | 406                      | message - постоянное, details - из сообщения в исключении                                         |
| `HttpMediaTypeNotSupportedException`<br/>org.springframework.web                    | 415                      | message - постоянное, details - из сообщения в исключении                                         |
| `MaxUploadSizeExceededException`<br/>org.springframework.web.multipart              | 413                      | message - постоянное, details - информация о допустимом размере файла / запроса                   |
| `ResponseStatusException`<br/>org.springframework.web.server                        | Статус из исключения     | Все параметры ответа берутся из исключения: статус / message / details                            |
| `NoResourceFoundException`<br/>org.springframework.web.servlet.resource             | 404                      | message - постоянное, details - запрошенный путь                                                  |
| `Exception`<br/>java.lang<br/>(любое другое исключение)                             | 500                      | message - постоянное, details - из сообщения в исключении                                         |

 _*_ Валидация параметров работает толькоесли в проект также добавлен `spring-boot-starter-validation`,
а валидируемые параметры отмечены аннотацией `@Valid`.

## ApiException

Исключение которое при выбрасывании в слое бизнес логики / контроллере будет интерпретировано как ответ API
с указанным в исключении HTTP статусом, причиной и деталями ошибки.

Пример:

```java
throw new ApiException(HttpStatus.NOT_FOUND, "Не найдено", "Объект с id 56 не найден");
```

Будет интерпретирован в ответ API вида:

```http request
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Не найдено",
  "details": "Объект с id 56 не найден"
}
```

## Аннотации для Swagger | OpenApi

Аннотации для упрощения генерации спецификации OpenAPI или для использования со Swagger UI.
Аннотации можно комбинировать, для того чтобы получить описания всех необходимыйх вариантов ответов.

Пример:

```java
@Operation(summary = "created object")
@Created
@BadRequest
@PostMapping(value = "/api/create", consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
public Object create(@RequestBody @Valid Object dto) {
    return new Object();
}
```

### Аннотации для 2xx HTTP статус кодов

 * [`@Ok`](#ok)
 * [`@OkWithResource`](#okwithresource)
 * [`@Created`](#created)
 * [`@Accepted`](#accepted)
 * [`@NoContent`](#nocontent)

#### `@Ok`

Соответствует HTTP статусу [200](https://www.rfc-editor.org/rfc/rfc9110#status.200), с телом ответа в формате JSON.

```http request
HTTP/1.1 200 OK
Content-Type: application/json
 
{"message":"ok"}
```

При этом схема и пример тела ответа будут генерироваться на основе типа объекта возвращаемого методом с аннотацией.
Поэтому метод контроллера должен возвращать объект явно указанного класса `T` или `ResponseEntity<T>` с явным указанием типа, 
но не `ResponseEntity<?>`.

#### `@OkWithResource`

Соответствует HTTP статусу [200](https://www.rfc-editor.org/rfc/rfc9110#status.200), с телом ответа вида `application/octet-stream`.
При этом ожидается что в ответе будет передан заголовок `Content-Disposition`.

```http request
HTTP/1.1 200 OK
Content-Disposition: attachment; filename="test.txt"
Accept-Ranges: bytes
Content-Type: text/plain
```

#### `@Created`

Соответствует HTTP статусу [201](https://www.rfc-editor.org/rfc/rfc9110#status.201), с телом ответа в формате JSON.

```http request
HTTP/1.1 201 Created
Content-Type: application/json
 
{"message":"ok"}
```

Описание схемы и примера ответа генерируется на основе типа объекта возвращаемого методом.
Поэтому метод должен возвращать `ResponseEntity<T>` с явным указанием типа объекта:

```java
@Created
@PostMapping(value = "/api/create", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ApiResponse> create(@RequestBody @Valid CreateDtoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(request));
}
```

#### `@Accepted`

Соответствует HTTP статусу [202](https://www.rfc-editor.org/rfc/rfc9110#status.202), с телом ответа в формате JSON.

```http request
HTTP/1.1 202 Accepted
Content-Type: application/json
 
{"message":"ok"}
```

Описание схемы и примера ответа генерируется на основе типа объекта возвращаемого методом.
Поэтому метод должен возвращать `ResponseEntity<T>` с явным указанием типа объекта:

```java
@Accepted
@PostMapping(value = "/api/create", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ApiResponse> create(@RequestBody @Valid CreateDtoRequest request) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(request));
}
```

#### `@NoContent`

Соответствует HTTP статусу [204](https://www.rfc-editor.org/rfc/rfc9110#status.204), тело ответа не предусмотрено.

```http request
HTTP/1.1 204 No Content
```

```java
@NoContent
@DeleteMapping(value = "/api/delete/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    // Do something
    return ResponseEntity.noContent().build();
}
```

### Аннотации для 4xx HTTP статус кодов

 * [`@BadRequest`](#badrequest)
 * [`@Unauthorized`](#unauthirized)
 * [`@Forbidden`](#forbidden)
 * [`@NotFound`](#notfound)
 * [`@Conflict`](#conflict)
 * [`@Gone`](#gone)
 * [`@PayloadTooLarge`](#payloadtoolarge)
 * [`@UnprocessableContent`](#unprocessablecontent)
 * [`@Locked`](#locked)
 * [`@TooManyRequests`](#toomanyrequests)

#### `@BadRequest`

Соответствует HTTP статусу [400](https://www.rfc-editor.org/rfc/rfc9110#status.400), с телом ответа в формате JSON.

```http request
HTTP/1.1 400 Bad Request
Content-Type: application/json
Connection: close

{
  "message": "Некорректный запрос",
  "details": "password must not be blank"
}
```

#### `@Unauthirized`

Соответствует HTTP статусу [401](https://www.rfc-editor.org/rfc/rfc9110#status.401), с телом ответа в формате JSON.

```http request
HTTP/1.1 401 Unauthorized
Content-Type: application/json
Connection: close

{
  "message": "Не авторизован",
  "details": "Не верный логин или пароль"
}
```

#### `@Forbidden`

Соответствует HTTP статусу [403](https://www.rfc-editor.org/rfc/rfc9110#status.403), с телом ответа в формате JSON.

```http request
HTTP/1.1 403 Forbidden
Content-Type: application/json
Connection: close

{
  "message": "Доступ запрещен",
  "details": "Не достаточно прав для совершения операции"
}
```

#### `@NotFound`

Соответствует HTTP статусу [404](https://www.rfc-editor.org/rfc/rfc9110#status.404), с телом ответа в формате JSON.

```http request
HTTP/1.1 404 Not Found
Content-Type: application/json
Connection: close

{
  "message": "Не найдено",
  "details": "Сущность с id < 123 > не найдена"
}
```

#### `@Conflict`

Соответствует HTTP статусу [409](https://www.rfc-editor.org/rfc/rfc9110#status.409), с телом ответа в формате JSON.

```http request
HTTP/1.1 409 Conflict
Content-Type: application/json
Connection: close

{
  "message": "Конфликт",
  "details": "Другой такой же процесс уже запущен"
}
```

#### `@Gone`

Соответствует HTTP статусу [410](https://www.rfc-editor.org/rfc/rfc9110#status.410), с телом ответа в формате JSON.

```http request
HTTP/1.1 410 Gone
Content-Type: application/json
Connection: close

{
  "message": "Более не доступно",
  "details": "Ресурс был удален"
}
```

#### `@PayloadTooLarge`

Соответствует HTTP статусу [413](https://www.rfc-editor.org/rfc/rfc9110#status.413), с телом ответа в формате JSON.

```http request
HTTP/1.1 413 Request Entity Too Large
Content-Type: application/json
Connection: close

{
  "message": "Превышен максимальный размер запроса",
  "details": "Максимальный размер загружаемого файла: 1 Mb"
}
```

#### `@UnprocessableContent`

Соответствует HTTP статусу [422](https://www.rfc-editor.org/rfc/rfc9110#status.422), с телом ответа в формате JSON.

```http request
HTTP/1.1 422 Unprocessable Entity (WebDAV) (RFC 4918)
Content-Type: application/json
Connection: close

{
  "message": "Невозможно выполнить",
  "details": "Ресурс в данный момент редактируется другим пользователем"
}
```

#### `@Locked`

Соответствует HTTP статусу [422](https://www.rfc-editor.org/rfc/rfc9110#status.422), с телом ответа в формате JSON.

```http request
HTTP/1.1 423 Locked (WebDAV) (RFC 4918)
Content-Type: application/json
Connection: close

{
  "message": "Невозможно выполнить",
  "details": "Ресурс в данный момент редактируется другим пользователем"
}
```

#### `@TooManyRequests`

Соответствует HTTP статусу [429](https://www.rfc-editor.org/rfc/rfc9110#status.429), с телом ответа в формате JSON.

```http request
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
Connection: close

{
  "message": "Невозможно выполнить",
  "details": "Ресурс в данный момент редактируется другим пользователем"
}
```

### Аннотации для 5xx HTTP статус кодов

 * [`@InternalServerError`](#internalservererror)
 * [`@NotImplemented`](#notimplemented)

#### `@InternalServerError`

Соответствует HTTP статусу [500](https://www.rfc-editor.org/rfc/rfc9110#status.500), с телом ответа в формате JSON.

```http request
HTTP/1.1 500 Internal Server Error
Content-Type: application/json
Connection: close

{
  "message": "Внутренняя ошибка приложения",
  "details": "Детали ошибки"
}
```

#### `@NotImplemented`

Соответствует HTTP статусу [501](https://www.rfc-editor.org/rfc/rfc9110#status.501), с телом ответа в формате JSON.

```http request
HTTP/1.1 501 Not Implemented
Content-Type: application/json
Connection: close

{
  "message": "Не реализовано"
}
```
