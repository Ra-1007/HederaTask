package com.andronicus.routeplanner;

import java.util.*;

public class PrioritySorter {
    public static List<DeliveryPoint> sortByPriority(List<DeliveryPoint> points) {
        Map<String, Integer> priorityOrder = Map.of(
                "high", 1,
                "medium", 2,
                "low", 3
        );

        points.sort(Comparator.comparingInt(p -> priorityOrder.get(p.priority)));
        return points;
    }
}
