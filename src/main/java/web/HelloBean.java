package web;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import com.google.gson.Gson;

@Named
@ApplicationScoped
public class HelloBean implements Serializable {

    private double x = 0.0;
    private double y = 0.0;
    private double r = 1.0;
    private String resultMessage;
    private long executionTime;
    private String currentTime;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PointPU");

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getR() { return r; }
    public void setR(double r) { this.r = r; }

    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public String getCurrentTime() { return currentTime; }
    public void setCurrentTime(String currentTime) { this.currentTime = currentTime; }

    public String checkPoint() {
        long startTime = System.nanoTime();

        if (!isValidNumber(Double.toString(x), Double.toString(y), Double.toString(r))
                || !isValidInput(x, y, r)) {
            resultMessage = "Invalid input values.";
            return null;
        }

        if (checkHit(x, y, r)) {
            resultMessage = "Hit!";
        } else {
            resultMessage = "Miss :(";
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setResultMessage(resultMessage);
        point.setExecutionTime(executionTime);
        point.setCurrentTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(point);
        em.getTransaction().commit();
        em.close();

//        return null;
        return "index.xhtml?faces-redirect=true";
    }

    public void clearPoints() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createNativeQuery("DROP TABLE IF EXISTS Point").executeUpdate();

            em.getTransaction().commit();
            System.out.println("Table 'Point' has been dropped from the database.");
        } catch (Exception e) {
            System.err.println("Error dropping the table: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }


    public List<Point> getPoints() {
        EntityManager em = emf.createEntityManager();
        List<Point> points = new ArrayList<>();
        try {
            points = em.createQuery("SELECT p FROM Point p", Point.class).getResultList();
        } catch (Exception e) {
            createTableIfNotExists();
        } finally {
            em.close();
        }
        return points;
    }

    public String getPointsJson() {
        Gson gson = new Gson();
        return gson.toJson(getPoints());
    }

    public static boolean isValidNumber(String xText, String yText, String rText) {
        String regex = "^-?\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcherX = pattern.matcher(xText);
        Matcher matcherY = pattern.matcher(yText);
        Matcher matcherR = pattern.matcher(rText);
        return matcherX.matches() && matcherY.matches() && matcherR.matches();
    }

    private boolean checkHit(double x, double y, double r) {
        if (x <= 0 && y >= 0) {
            return (x >= -r && y <= r / 2);
        } else if (x <= 0 && y <= 0) {
            return (x * x + y * y <= r * r);
        } else if (x >= 0 && y <= 0) {
            return (y >= x - r);
        }
        return false;
    }

    private boolean isValidInput(double x, double y, double r) {
        return x >= -7 && x <= 5 && y >= -5 && y <= 5 && r >= 1 && r <= 6;
    }

    public void createTableIfNotExists() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery(
                    "CREATE TABLE IF NOT EXISTS Point (" +
                            "id BIGSERIAL PRIMARY KEY, " +
                            "x DOUBLE PRECISION NOT NULL, " +
                            "y DOUBLE PRECISION NOT NULL, " +
                            "r DOUBLE PRECISION NOT NULL, " +
                            "resultMessage VARCHAR(255), " +
                            "currentTime VARCHAR(255), " +
                            "executionTime BIGINT)").executeUpdate();
            em.getTransaction().commit();
            System.out.println("Table 'Point' has been created or already exists.");
        } catch (Exception e) {
            System.err.println("Error creating table 'Point': " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
