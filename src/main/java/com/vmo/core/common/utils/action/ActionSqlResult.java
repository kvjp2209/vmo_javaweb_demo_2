package com.vmo.core.common.utils.action;

import java.sql.SQLException;

@FunctionalInterface
public interface ActionSqlResult<T> {
    T execute() throws SQLException;
}
