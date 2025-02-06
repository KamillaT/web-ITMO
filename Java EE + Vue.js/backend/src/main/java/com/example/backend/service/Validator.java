package com.example.backend.service;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValidNumber(String xText, String yText, String rText) {
        String regex = "^-?\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcherX = pattern.matcher(xText);
        Matcher matcherY = pattern.matcher(yText);
        Matcher matcherR = pattern.matcher(rText);
        return matcherX.matches() && matcherY.matches() && matcherR.matches();
    }

    public static boolean isValidInput(double x, double y, double r) {
        return x >= -7 && x <= 5 && y >= -5 && y <= 5 && r >= 1 && r <= 6;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
