package application;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.servlet.DispatcherServlet;

public class ServiceBus {

    private static Log LOGGER = LogFactory.getLog(ServiceBus.class);

    public static void main(String[] args) throws Exception {

        if(args.length != 1) {
            LOGGER.fatal("You must pass in a port number.");
            System.exit(255);
        }
        int port = Integer.parseInt(args[0]);

        LOGGER.info("Starting service bus under port:  " + port);
        Server server = new Server(port);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        server.setHandler(handler);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:spring-config/*.xml");

        handler.addServlet(new ServletHolder(dispatcherServlet), "/*");

        server.start();
        server.join();


    }




}
