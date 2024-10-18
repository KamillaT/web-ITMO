package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.Result;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/check")
public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xText = request.getParameter("x");
        String yText = request.getParameter("y");
        String rText = request.getParameter("r");
        long startTime = System.nanoTime();

        if (isValidNumber(xText, yText, rText)) {
            try {
                double x = Double.parseDouble(request.getParameter("x"));
                double y = Double.parseDouble(request.getParameter("y"));
                double r = Double.parseDouble(request.getParameter("r"));

                if (r < 0 || r > 5) {
                    request.setAttribute("error", "Radius must be between 0 and 5.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }

                boolean hit = checkHit(x, y, r);

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String currentTime = LocalDateTime.now().format(dateTimeFormatter);

                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                HttpSession session = request.getSession();
                List<Result> results = (List<Result>) session.getAttribute("results");
                if (results == null) {
                    results = new ArrayList<>();
                }
                Result result = new Result(x, y, r, hit, currentTime, executionTime);
                results.add(result);
                session.setAttribute("results", results);

                request.setAttribute("x", x);
                request.setAttribute("y", y);
                request.setAttribute("r", r);
                request.setAttribute("hit", hit);
                request.setAttribute("currentTime", currentTime);
                request.setAttribute("executionTime", executionTime + " ns");

                request.getRequestDispatcher("/result.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid number format for x, y, or r.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Input values are not valid.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }

    }

    private boolean checkHit(double x, double y, double r) {
        if (x <= 0 && y >= 0) {
            return (x >= -r && y <= r / 2);
        } else if (x >= 0 && y >= 0) {
            return (x * x + y * y <= r * r);
        } else if (x >= 0 && y <= 0) {
            return (y >= x - r);
        }
        return false;
    }

    public static boolean isValidNumber(String xText, String yText, String rText) {
        String regex = "^-?\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcherX = pattern.matcher(xText);
        Matcher matcherY = pattern.matcher(yText);
        Matcher matcherR = pattern.matcher(rText);
        return matcherX.matches() && matcherY.matches() && matcherR.matches();
    }
}
