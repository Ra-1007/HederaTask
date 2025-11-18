package com.andronicus.routeplanner;

public class DistanceUtils {
    public static double distance(DeliveryPoint a, DeliveryPoint b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}
