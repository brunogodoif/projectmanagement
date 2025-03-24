package br.com.brunogodoif.projectmanagement.domain.exceptions;

public class EntityDuplicateException extends BusinessException {
    public EntityDuplicateException(String message) {
        super(message);
    }

    public EntityDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}