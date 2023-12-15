/*
 * Copyright (c) 2023 the original author or authors.
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

package pro.nikolaev.restutils.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * {@link Exception} intended to use to expose an HTTP status and reason
 * to {@link ExceptionHandler @ExceptionHandler}.
 *
 * @author Ilya Nikolaev
 * @see ExceptionHandler
 * @since 1.0
 */
public class ApiException extends Exception {
    private final HttpStatusCode status;
    private final String reason;

    /**
     * Constructor with a response status and a reason.
     *
     * @param status the HTTP status
     * @param reason the associated reason
     * @since 1.0
     */
    public ApiException(HttpStatusCode status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    /**
     * Constructor with a response status, reason and message as explanation.
     *
     * @param status  the HTTP status
     * @param reason  the associated reason
     * @param message the explanation of an error or a hint on how to avoid it
     * @since 1.0
     */
    public ApiException(HttpStatusCode status, String reason, String message) {
        super(message);
        this.status = status;
        this.reason = reason;
    }

    /**
     * Constructor with a response status, reason and message as explanation,
     * as well as a nested exception.
     *
     * @param status  the HTTP status
     * @param reason  the associated reason
     * @param message the explanation of an error or a hint on how to avoid it
     * @param cause   a nested exception (optional)
     * @since 1.0
     */
    public ApiException(HttpStatusCode status, String reason, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.reason = reason;
    }

    /**
     * Constructor with a response status and reason as well as a nested exception.
     *
     * @param status the HTTP status
     * @param reason the associated reason
     * @param cause  a nested exception (optional)
     * @since 1.0
     */
    public ApiException(HttpStatusCode status, String reason, Throwable cause) {
        super(cause);
        this.status = status;
        this.reason = reason;
    }

    /**
     * Constructor with a response status, reason and message as explanation,
     * as well as a nested exception, suppression enabled or disabled,
     * and writable stack trace enabled or disabled.
     *
     * @param status             the HTTP status
     * @param reason             the associated reason
     * @param message            the explanation of an error or a hint on how to avoid it
     * @param cause              a nested exception (optional)
     * @param enableSuppression  whether suppression is enabled or disable
     * @param writableStackTrace whether the stack trace should be writable
     * @since 1.0
     */
    public ApiException(HttpStatusCode status, String reason, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
        this.reason = reason;
    }

    /**
     * Return the HTTP status code to use for the response.
     */
    public HttpStatusCode getStatus() {
        return status;
    }

    /**
     * The reason explaining the exception (potentially {@code null} or empty).
     */
    public String getReason() {
        return reason;
    }
}
