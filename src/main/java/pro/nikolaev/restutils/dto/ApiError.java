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

package pro.nikolaev.restutils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.nikolaev.restutils.components.ExceptionHandlingAdvice;

/**
 * DTO type that is used to populate {@link ResponseBody @ResponseBody}
 * of {@link ExceptionHandler @ExceptionHandler} methods in {@link ExceptionHandlingAdvice}.
 *
 * @param message a brief error description
 * @param details details which may describe either reason for an error,
 *                ways of resolution to an error or any other additional info.
 *                Will be omitted in final {@code JSON} if {@code null}
 * @author Ilya Nikolaev
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema
public record ApiError(
        @Schema(description = "Сообщение об ошибке", example = "Некорректный запрос")
        String message,

        @Schema(description = "Детали ошибки, если присутствуют", example = "Поле < id > не может быть пустым")
        String details) {
}
