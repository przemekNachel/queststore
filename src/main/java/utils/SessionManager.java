package utils;

import com.sun.net.httpserver.HttpExchange;
import user.user.User;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private List<Session> sessions = new ArrayList<>();

    public SessionManager() {

    }

    public User getUserFromSession(HttpExchange exchange) {
        User user = null;
        String cookiesHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if(!CookiesUtil.doesCookieExist(exchange)) return user;
        String sessionID = CookiesUtil.getSessionID(cookiesHeader);
        System.out.println(sessionID);
        if (sessionID == null) {
            return user;
        }

        for (Session session : sessions) {
            if (session.getId().equalsIgnoreCase(sessionID)) {
                return session.getUser();
            }
        }
        return user;
    }

    public void createSessionAndAssignToCookie(HttpExchange exchange, User user) {
        Session session = new Session(user);
        CookiesUtil.addSessionCookie(exchange, session);
        sessions.add(session);
    }

    public void expireUserSession(HttpExchange exchange) {
        String sessionId = CookiesUtil.getSessionID(exchange.getRequestHeaders().getFirst("Cookie"));
        for (Session session : sessions) {
            if (session.getId().equals(sessionId)) {
                sessions.remove(session);
            }
        }
    }
}