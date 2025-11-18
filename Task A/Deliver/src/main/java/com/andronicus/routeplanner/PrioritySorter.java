package com.andronicus.routeplanner;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<DeliveryPoint> points = List.of(
                new DeliveryPoint(0,0,"high"),
                new DeliveryPoint(2,3,"medium"),
                new DeliveryPoint(5,1,"high"),
                new DeliveryPoint(6,4,"low"),
                new DeliveryPoint(1,2,"medium")
        );

        List<DeliveryPoint> sorted = PrioritySorter.sortByPriority(new ArrayList<>(points));

        List<DeliveryPoint> finalRoute = new ArrayList<>();

        List<DeliveryPoint> high = new ArrayList<>();
        List<DeliveryPoint> med = new ArrayList<>();
        List<DeliveryPoint> low = new ArrayList<>();

        for (DeliveryPoint p : sorted) {
            switch (p.priority) {
                case "high" -> high.add(p);
                case "medium" -> med.add(p);
                case "low" -> low.add(p);
            }
        }

        finalRoute.addAll(NearestNeighbor.route(high));
        finalRoute.addAll(NearestNeighbor.route(med));
        finalRoute.addAll(NearestNeighbor.route(low));

        System.out.println("Optimized Route:");
        finalRoute.forEach(System.out::println);

        double totalDistance = 0;
        for (int i = 0; i < finalRoute.size() - 1; i++) {
            totalDistance += DistanceUtils.distance(finalRoute.get(i), finalRoute.get(i+1));
        }

        System.out.printf("\nTotal Distance: %.2f units\n", totalDistance);
    }
}
