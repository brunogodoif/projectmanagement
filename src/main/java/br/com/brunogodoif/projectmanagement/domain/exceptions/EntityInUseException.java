package br.com.brunogodoif.projectmanagement.domain.exceptions;

public class EntityInUseException extends BusinessException {
    public EntityInUseException(String message) {
        super(message);
    }
}