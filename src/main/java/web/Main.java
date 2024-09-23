package web;

import com.fastcgi.FCGIInterface;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final String RESPONSE_TEMPLATE = "Content-Type: application/json\nContent-Length: %d\nStatus: %d\n\n%s";

    private static void sendJson(int statusCode, String jsonDump) {
        System.out.printf(RESPONSE_TEMPLATE, jsonDump.getBytes(StandardCharsets.UTF_8).length, statusCode, jsonDump);
    }

    public static void main(String[] args) throws IOException {
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            long startTime = System.nanoTime();
            try {
                if (fcgi.request != null && fcgi.request.inStream != null) {
                    String body = readRequestBody();
                    JSONObject jsonRequest = new JSONObject(body);

                    double x = jsonRequest.getDouble("x");
                    double y = jsonRequest.getDouble("y");
                    double r = jsonRequest.getDouble("r");

                    if (x < -5 || x > 3 || y < -5 || y > 5 || r < 0 || r > 5) {
                        sendJson(400, new JSONObject().put("error", "Invalid input data").toString());
                    } else {
                        boolean isInside = calculate(x, y, r);
                        long endTime = System.nanoTime();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                        String jsonResponse = new JSONObject()
                                .put("result", isInside)
                                .put("x", x)
                                .put("y", y)
                                .put("r", r)
                                .put("currentTime", LocalDateTime.now().format(dateTimeFormatter))
                                .put("executionTime", (endTime - startTime) + "ns")
                                .toString();

                        sendJson(200, jsonResponse);
                    }
                } else {
                    sendJson(400, new JSONObject().put("error", "Invalid FCGI request.").toString());
                }
            } catch (Exception e) {
                sendJson(500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }
    }

    private static boolean calculate(double x, double y, double r) {
        if (x <= 0 && y >= 0) {
            return (x >= -r && y <= r / 2);
        } else if (x >= 0 && y >= 0) {
            return (x * x + y * y <= r * r);
        } else if (x >= 0 && y <= 0) {
            return (y >= x - r);
        }
        return false;
    }


    private static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();
        int contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);
        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();
        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }
}
