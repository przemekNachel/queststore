package utils;

import com.sun.net.httpserver.HttpExchange;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CookiesUtil {

    public static void addCookie(HttpExchange httpExchange, Session session) {
        HttpCookie cookie = new HttpCookie("sessionId", session.getId());
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    public static boolean doesCookieExist(HttpExchange httpExchange) {
        return httpExchange.getRequestHeaders().getFirst("Cookie") != null;
    }

    public static List<HttpCookie> getAllCookies(String cookiesHeader) {
        List<HttpCookie> cookiesList = new ArrayList<>();
        String[] singleCookie = cookiesHeader.split(";");
        for (String c : singleCookie) {
            LinkedList<HttpCookie> l = (LinkedList<HttpCookie>) HttpCookie.parse(c);
            cookiesList.add(l.getFirst());
        }
        return cookiesList;
    }

}
