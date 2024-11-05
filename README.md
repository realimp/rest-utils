# Небольшой набор вспомогательных классов для Spring Boot 3.x.x Web приложений

Набор включает в себя:

1. [RestControllerAdvice Bean для обработки часто встречающихся исключений](https://gitverse.ru/realimp/rest-utils/wiki/WIKIRSTTLS-1)
2. [Исключение для формирования ответов API с определенным статусом](https://gitverse.ru/realimp/rest-utils/wiki/WIKIRSTTLS-2)
3. [Набор аннотаций для Swagger | OpenAPI, описывающие основные варианты HTTP ответов](https://gitverse.ru/realimp/rest-utils/wiki/WIKIRSTTLS-3)

## Quick start

Добавить в проект зависимость:

```xml
<dependency>
    <groupId>pro.nikolaev</groupId>
    <artifactId>rest-utils</artifactId>
    <version>1.0.4</version>
</dependency>
```

После чего обработка исключений с использованием `RestControllerAdvice` включается добавлением аннотации 
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
