package br.com.brunogodoif.projectmanagement.domain.usecases;


public interface DeleteEntityInterface<ID> {
    void execute(ID id);
}