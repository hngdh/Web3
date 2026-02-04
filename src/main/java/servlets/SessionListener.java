package servlets;

import data_services.PointDBServices;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Inject
    private PointDBServices pointDBServices;

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.getSession();
        String jSessionId = session.getId();
        pointDBServices.deleteAllBySession(jSessionId);
        System.out.println("Session " + jSessionId + " was destroyed!");
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        System.out.println("Session " + sessionEvent.getSession().getId() + " was created!");
    }
}
