package com.oneglobal.service.exception;

import java.util.List;

public record ValidationError(Long timeStamp, Integer status, String message, String path, List<FieldError> errors) {
    public record FieldError(String field, String message) {}
}
