package com.andronicus.routeplanner;

import java.util.*;

public class PrioritySorter {

    private static final Map<String, Integer> order = Map.of(
            "high", 1,
            "medium", 2,
            "low", 3
    );

    public static List<DeliveryPoint> sortByPriority(List<DeliveryPoint> points) {
        points.sort(Comparator.comparingInt(p -> order.getOrDefault(
                p.priority.toLowerCase(),   // FIX: convert input to lowercase
                Integer.MAX_VALUE           // FIX: handle unexpected priority safely
        )));
        return points;
    }
}
