package toby.tutorial.user.service;

import toby.tutorial.user.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
