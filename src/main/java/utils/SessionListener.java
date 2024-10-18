package utils;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.time.LocalDateTime;
import java.time.Duration;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LocalDateTime startTime = LocalDateTime.now();
        event.getSession().setAttribute("startTime", startTime);
        System.out.println("Session started at: " + startTime);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        LocalDateTime startTime = (LocalDateTime) event.getSession().getAttribute("startTime");
        LocalDateTime endTime = LocalDateTime.now();

        if (startTime != null) {
            Duration sessionDuration = Duration.between(startTime, endTime);
            System.out.println("Session ended at: " + endTime);
            System.out.println("Session duration: " + sessionDuration.toMinutes() + " minutes");
        }
    }
}
