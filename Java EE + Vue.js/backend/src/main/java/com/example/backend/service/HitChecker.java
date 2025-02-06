package com.example.backend.service;

public class HitChecker {
    public static boolean checkHit(double x, double y, double r) {
        if (x <= 0 && y >= 0) {
            return (x >= -r && y <= r / 2);
        } else if (x <= 0 && y <= 0) {
            return (x * x + y * y <= r * r);
        } else if (x >= 0 && y <= 0) {
            return (y >= x - r);
        }
        return false;
    }
}
