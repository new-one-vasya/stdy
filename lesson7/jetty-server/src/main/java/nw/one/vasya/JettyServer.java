package nw.one.vasya;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);

    public static void main(String[] args) throws Exception {

        Server server = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        ServerConnector connector = new ServerConnector(server, http11);
        connector.setPort(8080);
        server.addConnector(connector);

        server.setHandler(new AbstractHandler() {

            @Override
            public void handle(String target, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

                if ("/service".equals(target)) {
                    LOGGER.info("Request {}", jettyRequest.getPathInfo());
                    response.getWriter().write("Hello " + request.getParameter("name"));
                }
                jettyRequest.setHandled(true);
            }
        });

        server.start();

    }
}