package com.example.backend.rest;

import com.example.backend.entity.Point;
import com.example.backend.entity.User;
import com.example.backend.jwt.JwtUtil;
import com.example.backend.service.UserService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.example.backend.service.PasswordHash.checkPassword;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String userJsonStr) {
        try {
            User user = new User();
            JsonObject jsonUser = JsonParser.parseString(userJsonStr).getAsJsonObject();
            String email = jsonUser.get("email").toString();

//            if (!isValidEmail(email)) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("{\"error\": \"Invalid email format. Please, use this format: email@example.com\"}")
//                        .build();
//            }

            user.setUsername(jsonUser.get("username").toString());
            user.setEmail(email);
            user.setPassword(jsonUser.get("password").toString());

            if (userService.findByEmail(user.getEmail()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email is already in use\"}")
                        .build();
            }

            if (userService.findByUsername(user.getUsername()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Username is already in use\"}")
                        .build();
            }

            userService.createUser(user);

            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"User registered successfully!\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to register user\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(String userDataJsonStr) {
        try {
            JsonObject jsonUser = JsonParser.parseString(userDataJsonStr).getAsJsonObject();
            String email = jsonUser.get("email").toString();
            String password = jsonUser.get("password").toString();

            User curUser = userService.findByEmail(email);
            if (curUser == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email wasn't found\"}")
                        .build();
            }
            String hashedPassword = curUser.getPassword();
            if (!checkPassword(password, hashedPassword)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Wrong password\"}")
                        .build();
            }

            String token = JwtUtil.generateToken(curUser.getUsername());
            if (token == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid email or password\"}")
                        .build();
            }

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("token", token);
            return Response.ok(responseJson.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to log in\"}")
                    .build();
        }
    }

    @GET
    @Path("/personal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonalPage(@HeaderParam("Authorization") String authHeader) {
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

            return Response.ok(user).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid token\"}")
                    .build();
        }
    }
}
