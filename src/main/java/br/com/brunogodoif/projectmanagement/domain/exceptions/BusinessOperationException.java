package br.com.brunogodoif.projectmanagement.domain.exceptions;

public class BusinessOperationException extends BusinessException {
    public BusinessOperationException(String message) {
        super(message);
    }

    public BusinessOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}