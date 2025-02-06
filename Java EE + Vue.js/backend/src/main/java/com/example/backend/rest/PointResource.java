package com.example.backend.rest;

import com.example.backend.entity.Point;
import com.example.backend.entity.User;
import com.example.backend.jwt.JwtUtil;
import com.example.backend.service.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.backend.service.Validator.*;
import static com.example.backend.service.HitChecker.*;

@Path("/points")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PointResource {
    @Inject
    private PointService pointService;

    @Inject
    private UserService userService;

    @GET
    public Response getPoints(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Missing or invalid Authorization header\"}")
                    .build();
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            String username = JwtUtil.validateTokenAndRetrieveSubject(token);
            User user = userService.findByUsername(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found\"}")
                        .build();
            }

            List<Point> points = pointService.findPointsByUserId(user.getId());
            return Response.ok(points).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid token\"}" + " " + token)
                    .build();
        }
    }

    @POST
    @Path("/checkpoint")
    public Response submitPoint(@HeaderParam("Authorization") String authHeader, String pointJson) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Missing or invalid Authorization header\"}")
                    .build();
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            String username = JwtUtil.validateTokenAndRetrieveSubject(token);
            User user = userService.findByUsername(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found\"}")
                        .build();
            }

            Point point = processPointJson(pointJson, user);
            if(point != null) {
                pointService.savePoint(point);
                return Response.status(Response.Status.CREATED).entity(point).build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid input\"}")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid token\"}" + " " + token)
                    .build();
        }
    }

    private Point processPointJson(String pointJson, User user) {
        JsonObject jsonPoint = JsonParser.parseString(pointJson).getAsJsonObject();
        String xStr = jsonPoint.get("x").toString();
        String yStr = jsonPoint.get("y").toString();
        String rStr = jsonPoint.get("r").toString();

        if (isValidNumber(xStr, yStr, rStr)) {

            double x = Double.parseDouble(xStr);
            double y = Double.parseDouble(yStr);
            double r = Double.parseDouble(rStr);

            if (isValidInput(x, y, r)) {
                Point point = new Point();
                point.setX(x);
                point.setY(y);
                point.setR(r);
                point.setInside(checkHit(x, y, r));
                point.setUser(user);
                point.setCreatedAt(LocalDateTime.now());
                point.setExecutionTime(System.nanoTime());

                return point;
            }
            return null;
        }
        return null;
    }

    @DELETE
    @Path("/clear")
    public Response deletePoints(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Missing or invalid Authorization header\"}")
                    .build();
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            String username = JwtUtil.validateTokenAndRetrieveSubject(token);
            User user = userService.findByUsername(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found\"}")
                        .build();
            }

            pointService.deletePoints(user.getId());
            return Response.noContent().build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid token\"}" + " " + token)
                    .build();
        }
    }
}