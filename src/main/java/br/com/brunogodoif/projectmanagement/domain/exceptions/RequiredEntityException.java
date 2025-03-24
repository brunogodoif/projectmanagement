package br.com.brunogodoif.projectmanagement.domain.exceptions;

public class RequiredEntityException extends BusinessException {
    public RequiredEntityException(String message) {
        super(message);
    }

    public RequiredEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}