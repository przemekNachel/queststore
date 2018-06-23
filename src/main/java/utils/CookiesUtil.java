package utils;

import com.sun.net.httpserver.HttpExchange;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class CookiesUtil {

    public static void addSessionCookie(HttpExchange httpExchange, Session session) {
        HttpCookie cookie = new HttpCookie("session", session.getId());
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    public static String getSessionID(String cookiesHeader) {
        String sessionID = null;
        for (HttpCookie httpCookie : getAllCookies(cookiesHeader)) {
            if (httpCookie.getName().equalsIgnoreCase("session")) {
                sessionID = httpCookie.getValue();
            }
        }

        return sessionID;
    }

    public static boolean doesCookieExist(HttpExchange httpExchange) {
        return httpExchange.getRequestHeaders().getFirst("Cookie") != null;
    }

    public static List<HttpCookie> getAllCookies(String cookiesHeader) {
        List<HttpCookie> cookiesList = new ArrayList<>();
        String[] singleCookie = cookiesHeader.split(";");
        for (String c : singleCookie) {
            List<HttpCookie> l = HttpCookie.parse(c);
            cookiesList.add(l.get(0));
        }
        return cookiesList;
    }

}
