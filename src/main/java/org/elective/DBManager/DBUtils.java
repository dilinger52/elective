package org.elective.DBManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DBUtils {

    private static DBUtils instance;

    public static synchronized DBUtils getInstance() {
        if (instance == null) {
            instance = new DBUtils();
        }
        return instance;
    }

    private DataSource ds;

    private DBUtils() {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            ds = (DataSource)envContext.lookup("jdbc/elective");
        } catch (NamingException ex) {
            throw new IllegalStateException("Cannot obtain a data source", ex);
        }
    }

    public Connection getConnection() {
        Connection con;
        try {
            con = ds.getConnection();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot obtain a connection", ex);
        }
        return con;
    }

    public static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
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
}
