package org.elective.listeners;

import org.elective.database.dao.DAOFactory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.servlet.ServletContextEvent;

import static org.mockito.Mockito.mock;

public class AppContextListenerTest {

    @Test
    public void testContextInitialized() throws Exception {
        AppContextListener appContextListener = new AppContextListener();

        ServletContextEvent sce = mock(ServletContextEvent.class);

        appContextListener.contextInitialized(sce);

        Assertions.assertEquals("class org.elective.database.dao.mysql.MysqlDAOFactory", DAOFactory.getInstance().getClass().toString());

    }


} 
