package utils;

public class Result {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String currentTime;
    private long executionTime;

    public Result(double x, double y, double r, boolean hit, String currentTime, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public boolean isHit() {
        return hit;
    }

    public String getDate() {
        return currentTime;
    }

    public String getExecutionTime() {
        return executionTime + " ns";
    }
}
