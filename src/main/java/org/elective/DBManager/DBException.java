package org.elective.DBManager;

import java.sql.SQLException;

public class DBException extends SQLException {
    public DBException(String s, Exception e) {
        super(s, e);
    }
}
