package Servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LifeCycleListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent event) {

    }

    public void contextDestroyed(ServletContextEvent event) {

    }
}
