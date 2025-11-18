package com.andronicus.routeplanner;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<DeliveryPoint> points = new ArrayList<>();

        // ------------------------------
        // SAMPLE INPUT OPTION
        // ------------------------------
        System.out.print("Use sample data? (y/n): ");
        String useSample = sc.next();

        if (useSample.equalsIgnoreCase("y")) {

            points.add(new DeliveryPoint(0, 0, "high"));
            points.add(new DeliveryPoint(2, 3, "medium"));
            points.add(new DeliveryPoint(5, 1, "high"));
            points.add(new DeliveryPoint(6, 4, "low"));
            points.add(new DeliveryPoint(1, 2, "medium"));

            System.out.println("\nUsing sample data:");
            points.forEach(System.out::println);

        } else {

            // ------------------------------
            // MANUAL USER INPUT
            // ------------------------------
            System.out.print("Enter number of delivery points: ");
            int n = sc.nextInt();

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
        }

        // ------------------------------
        // OPTIMAL PRIORITY TSP
        // ------------------------------
        OptimalPriorityTSP solver = new OptimalPriorityTSP(points);
        List<DeliveryPoint> finalRoute = solver.solve();

        // ------------------------------
        // DISPLAY ROUTE
        // ------------------------------
        System.out.println("\nOptimized Route (Optimal DP TSP):");
        finalRoute.forEach(System.out::println);

        // ------------------------------
        // CALCULATE DISTANCE
        // ------------------------------
        double total = 0;
        for (int i = 0; i < finalRoute.size() - 1; i++) {
            total += DistanceUtils.distance(finalRoute.get(i), finalRoute.get(i + 1));
        }

        System.out.printf("\nTotal Distance: %.2f units\n", total);
    }
}
