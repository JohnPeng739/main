package org.mx.test.service;

public interface TransactionService {
    void commit();
    boolean commitSuccess();
    void rollback();
    boolean rollbackSuccess();
}
