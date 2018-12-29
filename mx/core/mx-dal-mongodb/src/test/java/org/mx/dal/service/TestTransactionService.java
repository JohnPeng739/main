package org.mx.dal.service;

public interface TestTransactionService {
    void testCommit();

    boolean commitSuccess();

    void testRollback();

    boolean rollbackSuccess();
}
