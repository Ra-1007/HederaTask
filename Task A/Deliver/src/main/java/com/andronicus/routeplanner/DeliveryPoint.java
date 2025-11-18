package com.andronicus.routeplanner;

public class DeliveryPoint {
    public int x;
    public int y;
    public String priority;

    public DeliveryPoint(int x, int y, String priority) {
        this.x = x;
        this.y = y;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ") - " + priority;
    }
}
