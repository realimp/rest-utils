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

package pro.nikolaev.restutils.annotations;

import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pro.nikolaev.restutils.components.PerControllerExceptionHandlingAdvice;
import pro.nikolaev.restutils.dto.ApiError;

import java.lang.annotation.*;

/**
 * A convenience annotation that creates a bean annotated with
 * {@link RestControllerAdvice @RestControllerAdvice} that
 * will only handle exceptions in controllers annotated with this annotation.
 *
 * <p>Types that carry this annotation are treated as controller advice where
 * {@link ExceptionHandler @ExceptionHandler} methods use
 * {@link ResponseBody @ResponseBody} of type {@link ApiError}.
 *
 * @author Ilya Nikolaev
 * @see RestControllerAdvice
 * @see ApiError
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(PerControllerExceptionHandlingAdvice.class)
@Documented
public @interface RestExceptionHandler {
}
