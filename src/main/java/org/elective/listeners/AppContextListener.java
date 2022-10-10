package org.elective.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.dao.DAOFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.debug("Application started");
        DAOFactory.setDAOFactoryFCN("org.elective.DBManager.dao.mysql.MysqlDAOFactory");

    }
}
