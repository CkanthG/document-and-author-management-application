package com.krieger.advice;

import java.util.Map;

/**
 * To capture all field level errors and send to UI.
 * @param fieldErrors used for capturing all field errors.
 */
public record GenericFieldErrorResponse(
        Map<String, String> fieldErrors
) {
}
