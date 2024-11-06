/*
 * Copyright (c) 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.nikolaev.restutils.components;

import jakarta.servlet.MultipartConfigElement;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pro.nikolaev.restutils.dto.ApiError;
import pro.nikolaev.restutils.exceptions.ApiException;

import java.text.MessageFormat;

/**
 * Class representing {@link RestControllerAdvice} bean for handling MVC exception.
 *
 * @author Ilya Nikolaev
 * @version 1.0
 */
@RestControllerAdvice
public class ExceptionHandlingAdvice {
    private static final String BAD_REQUEST = "Некорректный запрос";
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAdvice.class);
    private final long maxFileSize;
    private final long maxRequestSize;

    public ExceptionHandlingAdvice(MultipartConfigElement multipartConfigElement) {
        this.maxFileSize = DataSize.ofBytes(multipartConfigElement.getMaxFileSize()).toMegabytes();
        this.maxRequestSize = DataSize.ofBytes(multipartConfigElement.getMaxRequestSize()).toMegabytes();
    }

    /**
     * {@link ExceptionHandler} to handle {@link ApiException}.
     *
     * @param e {@link ApiException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status from exception,
     * exception reason as {@code message},
     * exception message in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see ApiException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus()).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError(e.getReason(), e.getMessage()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link HttpRequestMethodNotSupportedException}.
     *
     * @param ignored {@link HttpRequestMethodNotSupportedException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 405,
     * {@literal "Метод не поддерживается"} message
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see HttpRequestMethodNotSupportedException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handle405(HttpRequestMethodNotSupportedException ignored) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Метод не поддерживается", null));
    }

    /**
     * {@link ExceptionHandler} to handle {@link MethodArgumentNotValidException}.
     *
     * <p><b>NOTE:</b> only works if
     * <a href="https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html">
     * {@code Jakarta Bean Validation}</a> is properly configured.
     *
     * @param e {@link MethodArgumentNotValidException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 400,
     * {@literal "Некорректный запрос"} message,
     * failed parameter info in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see MethodArgumentNotValidException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handle400(MethodArgumentNotValidException e) {
        String details = null;
        FieldError fieldError = e.getFieldError();
        ObjectError globalError = e.getGlobalError();
        if (fieldError != null) {
            details = MessageFormat
                    .format("{0} {1}", fieldError.getField(), fieldError.getDefaultMessage());
        } else if (globalError != null) {
            details = MessageFormat
                    .format("{0} {1}", globalError.getObjectName(), globalError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError(BAD_REQUEST, details));
    }

    /**
     * {@link ExceptionHandler} to handle {@link HttpMessageNotReadableException}.
     *
     * @param e {@link HttpMessageNotReadableException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 400,
     * {@literal "Некорректный запрос"} message,
     * exception message in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see HttpMessageNotReadableException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handle400(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError(BAD_REQUEST, e.getMessage()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link MethodArgumentTypeMismatchException}.
     *
     * @param e {@link MethodArgumentTypeMismatchException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 400,
     * {@literal "Некорректный запрос"} message,
     * information about invalid parameter in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see MethodArgumentTypeMismatchException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handle400(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError(BAD_REQUEST,
                        MessageFormat.format("Некорректное значение параметра < {0} >. {1}",
                                e.getParameter().getParameterName(), e.getMessage())));
    }

    /**
     * {@link ExceptionHandler} to handle {@link HttpMediaTypeNotAcceptableException}.
     *
     * @param e {@link HttpMediaTypeNotAcceptableException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 406,
     * {@literal "Тип данных не поддерживается"} message,
     * exception message in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see HttpMediaTypeNotAcceptableException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiError> handle406(HttpMediaTypeNotAcceptableException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Тип данных не поддерживается", e.getMessage()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link HttpMediaTypeNotSupportedException}.
     *
     * @param e {@link HttpMediaTypeNotSupportedException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 415,
     * {@literal "Не поддерживаемый тип данных"} message,
     * exception message in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see HttpMediaTypeNotSupportedException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handle415(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Не поддерживаемый тип данных", e.getMessage()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link MaxUploadSizeExceededException}.
     *
     * @param e {@link MaxUploadSizeExceededException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 413,
     * {@literal "Превышен максимальный размер запроса"} message,
     * either max request size, max file size or both in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see MaxUploadSizeExceededException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handle413(MaxUploadSizeExceededException e) {
        Throwable cause = e.getCause();
        String detail = null;
        if (cause != null) {
            if (cause.getCause() instanceof SizeLimitExceededException) {
                detail = MessageFormat.format("Максимальный размер тела запроса: {0} Mb", maxRequestSize);
            } else if (cause.getCause() instanceof FileSizeLimitExceededException) {
                detail = MessageFormat.format("Максимальный размер загружаемого файла: {0} Mb", maxFileSize);
            } else {
                detail = MessageFormat.format(
                        "Максимальный размер тела запроса: {0} Mb. Максимальный размер одного файла: {1} Mb",
                        maxFileSize, maxRequestSize);
            }
        }
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Превышен максимальный размер запроса", detail));
    }

    /**
     * {@link ExceptionHandler} to handle {@link ResponseStatusException}.
     *
     * @param e {@link ResponseStatusException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status from exception,
     * exception reason as {@code message},
     * exception detailed message code in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see ResponseStatusException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError(e.getReason(), e.getDetailMessageCode()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link NoResourceFoundException}.
     *
     * @param e {@link NoResourceFoundException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 404,
     * {@literal "Не найдено"} message,
     * resource path in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see NoResourceFoundException
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handle404(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON).body(new ApiError("Не найдено", e.getResourcePath()));
    }

    /**
     * {@link ExceptionHandler} to handle any exception not specified in other handlers of
     * {@link ExceptionHandlingAdvice} thrown while executing a {@link RestController} method.
     *
     * <p>As this method is intended to handle any unexpected or otherwise handled exceptions
     * it also logs them at {@code ERROR} level for easier debugging.</p>
     *
     * @param e {@link Exception} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 500,
     * {@literal "Внутренняя ошибка приложения"} message,
     * exception message in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see ResponseEntity
     * @since 1.0
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception e) {
        logger.error("Unexpected error:", e);
        return ResponseEntity.internalServerError().header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Внутренняя ошибка приложения", e.getMessage()));
    }

    /**
     * {@link ExceptionHandler} to handle {@link AccessDeniedException}.
     *
     * @param e {@link AccessDeniedException} to be processed by {@link ExceptionHandler}
     * @return {@link ResponseEntity ResponseEntity} with HTTP status 403,
     * {@literal "Доступ запрещен"} message,
     * resource path in {@code details} part of the body
     * and {@code Connection: Close} header
     * @see ExceptionHandler
     * @see AccessDeniedException
     * @see ResponseEntity
     * @since 1.0.5
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handle403(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header(HttpHeaders.CONNECTION, "Close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError("Доступ запрещен", e.getMessage()));
    }
}
