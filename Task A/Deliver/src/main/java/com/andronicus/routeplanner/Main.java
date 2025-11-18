package com.andronicus.routeplanner;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of delivery points: ");
        int n = sc.nextInt();

        List<DeliveryPoint> points = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for point " + (i + 1));

            System.out.print("x: ");
            int x = sc.nextInt();

            System.out.print("y: ");
            int y = sc.nextInt();

            System.out.print("priority (high/medium/low): ");
            String priority = sc.next();

            points.add(new DeliveryPoint(x, y, priority));
        }

        // Sort by priority
        List<DeliveryPoint> sorted = PrioritySorter.sortByPriority(points);

        List<DeliveryPoint> high = new ArrayList<>();
        List<DeliveryPoint> med = new ArrayList<>();
        List<DeliveryPoint> low = new ArrayList<>();

        for (DeliveryPoint p : sorted) {
            switch (p.priority.toLowerCase()) {
                case "high" -> high.add(p);
                case "medium" -> med.add(p);
                case "low" -> low.add(p);
                default -> System.out.println("Invalid priority: " + p.priority);
            }
        }

        List<DeliveryPoint> finalRoute = new ArrayList<>();
        if (!high.isEmpty()) finalRoute.addAll(NearestNeighbor.route(high));
        if (!med.isEmpty())  finalRoute.addAll(NearestNeighbor.route(med));
        if (!low.isEmpty())  finalRoute.addAll(NearestNeighbor.route(low));

        System.out.println("\nOptimized Route:");
        finalRoute.forEach(System.out::println);

        double total = 0;
        for (int i = 0; i < finalRoute.size() - 1; i++) {
            total += DistanceUtils.distance(finalRoute.get(i), finalRoute.get(i+1));
        }

        System.out.printf("\nTotal Distance: %.2f units\n", total);
    }
}
