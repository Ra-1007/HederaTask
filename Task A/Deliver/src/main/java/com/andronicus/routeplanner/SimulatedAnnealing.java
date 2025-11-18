package com.andronicus.routeplanner;

import java.util.*;

public class SimulatedAnnealing {

    private static double routeDistance(List<DeliveryPoint> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += DistanceUtils.distance(route.get(i), route.get(i + 1));
        }
        return total;
    }

    private static void twoOptSwap(List<DeliveryPoint> route, int i, int k) {
        while (i < k) {
            Collections.swap(route, i, k);
            i++;
            k--;
        }
    }

    public static List<DeliveryPoint> optimize(List<DeliveryPoint> initialRoute) {

        // FIX 1: Do not optimize groups with <2 points
        if (initialRoute.size() < 2) {
            return new ArrayList<>(initialRoute);
        }

        List<DeliveryPoint> current = new ArrayList<>(initialRoute);
        List<DeliveryPoint> best = new ArrayList<>(current);

        double currentDist = routeDistance(current);
        double bestDist = currentDist;

        double temp = 5000.0;         // high temp for small datasets
        double coolingRate = 0.9995;  // slow cooling
        int iterations = 50000;

        Random rand = new Random();

        for (int iter = 0; iter < iterations; iter++) {

            int size = current.size();

            // FIX 2: random bounds safe
            int i = rand.nextInt(size - 1);
            int k = i + 1 + rand.nextInt(size - i - 1);

            List<DeliveryPoint> newRoute = new ArrayList<>(current);
            twoOptSwap(newRoute, i, k);

            double newDist = routeDistance(newRoute);

            if (newDist < currentDist ||
                    Math.exp((currentDist - newDist) / temp) > rand.nextDouble()) {

                current = newRoute;
                currentDist = newDist;

                if (newDist < bestDist) {
                    bestDist = newDist;
                    best = new ArrayList<>(newRoute);
                }
            }

            temp *= coolingRate;
        }

        return best;
    }
}
