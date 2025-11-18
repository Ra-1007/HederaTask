package com.andronicus.routeplanner;

import java.util.*;

public class NearestNeighbor {

    public static List<DeliveryPoint> route(List<DeliveryPoint> points) {

        if (points.isEmpty()) return points;

        List<DeliveryPoint> route = new ArrayList<>();
        DeliveryPoint current = points.get(0);
        route.add(current);

        List<DeliveryPoint> unvisited = new ArrayList<>(points.subList(1, points.size()));

        while (!unvisited.isEmpty()) {

            // Make a final reference for lambda
            final DeliveryPoint currentFinal = current;

            DeliveryPoint next = Collections.min(
                    unvisited,
                    Comparator.comparingDouble(p -> DistanceUtils.distance(currentFinal, p))
            );

            route.add(next);
            unvisited.remove(next);
            current = next;
        }

        return route;
    }
}
