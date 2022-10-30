package org.elective.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUtils {

    private static DBUtils instance;
    private final DataSource ds;

    private DBUtils() {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/elective");
        } catch (NamingException ex) {
            throw new IllegalStateException("CannotObtainADataSource", ex);
        }
    }

    public static synchronized DBUtils getInstance() {
        if (instance == null) {
            instance = new DBUtils();
        }
        return instance;
    }

    public static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() {
        Connection con;
        try {
            con = ds.getConnection();
        } catch (SQLException ex) {
            throw new IllegalStateException("CannotObtainAConnection", ex);
        }
        return con;
    }
}
