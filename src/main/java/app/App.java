package app;

import com.sun.net.httpserver.HttpServer;
import handlers.RootRequestHandler;
import handlers.StaticResourcesHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
//        new LoginController().start();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        httpServer.createContext("/", new RootRequestHandler());
        httpServer.createContext("/static", new StaticResourcesHandler());
        httpServer.start();
    }
}
