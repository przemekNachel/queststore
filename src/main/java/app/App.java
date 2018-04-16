package app;

import com.sun.net.httpserver.HttpServer;
import handlers.LoginRequestHandler;
import handlers.LogoutRequestHandler;
import handlers.RootRequestHandler;
import handlers.StaticResourcesHandler;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
//        new LoginController().start();
        SessionManager sessionManager = new SessionManager();

        RequestRedirector redirector = new RequestRedirector(sessionManager);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        httpServer.createContext("/", new RootRequestHandler(sessionManager, redirector));
        httpServer.createContext("/static", new StaticResourcesHandler());
        httpServer.createContext("/login", new LoginRequestHandler(sessionManager, redirector));
        httpServer.createContext("/logout", new LogoutRequestHandler(sessionManager, redirector));
        httpServer.start();
    }
}
