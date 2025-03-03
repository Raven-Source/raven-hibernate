package org.raven.hibernate.jpa.test.config;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class NoOpTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() {
        // No-op
        return null;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // No-op
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        // No-op
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        // No-op
    }
}
