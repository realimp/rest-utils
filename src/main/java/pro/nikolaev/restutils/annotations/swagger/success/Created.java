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

package pro.nikolaev.restutils.annotations.swagger.success;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * A convenience annotation that is itself annotated with
 * {@link ApiResponse @ApiResponse}.
 *
 * <p>Methods that carry this annotation will be included in generated
 * openApi documentation with predefined HTTP status code 201.
 * And are assumed to have a {@link ResponseEntity} return type
 * with specified type reference (not a wildcard {@code <?>}).
 * {@link ResponseBody @ResponseBody} description and example will
 * be generated according to a type reference specified in {@link ResponseEntity}.
 *
 * <p><b>NOTE:</b> {@code @Created} is processed if annotated method
 * is part of properly configured {@link RestController} and
 * <a href="https://springdoc.org">{@code springdoc-openapi}</a> library.
 *
 * @author Ilya Nikolaev
 * @see ApiResponse
 * @see ResponseEntity
 * @see RestController
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ApiResponse(responseCode = "201", description = "Успешно создано", useReturnTypeSchema = true)
@Documented
public @interface Created {
}
