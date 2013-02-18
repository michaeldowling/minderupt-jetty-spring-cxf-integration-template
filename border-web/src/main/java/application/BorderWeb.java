package application;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class BorderWeb {

    private static Log LOGGER = LogFactory.getLog(BorderWeb.class);


    public static void main(String[] args) {

        if(args.length != 1) {
            LOGGER.fatal("You must pass in a port number.");
            System.exit(255);
        }
        int port = Integer.parseInt(args[0]);

        Server server = new Server(port);

        if(LOGGER.isInfoEnabled()) LOGGER.info("Configuring Apache CXF");
        ServletContextHandler cxfServletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ServletHolder cxfServletHolder = new ServletHolder(new CXFServlet());
        cxfServletHolder.setInitParameter("config-location", "classpath:spring-config/*.xml");
        cxfServletHandler.addServlet(cxfServletHolder, "/api/*");

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.addHandler(cxfServletHandler);

        server.setHandler(handlers);

        try {
            server.start();
        } catch(Exception e) {
            LOGGER.fatal("Unable to start Jetty.", e);
            System.exit(255);
        }

        if(LOGGER.isInfoEnabled()) LOGGER.info("Jetty started, awaiting requests...");
        try {
            server.join();
        } catch(Exception e) {
            LOGGER.fatal("While serving requests, Jetty encountered a fatal error.", e);
            System.exit(255);
        }

    }
}
