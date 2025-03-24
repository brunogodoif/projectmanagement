package br.com.brunogodoif.projectmanagement.domain.exceptions;

public class BusinessValidationException extends BusinessException {
    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}