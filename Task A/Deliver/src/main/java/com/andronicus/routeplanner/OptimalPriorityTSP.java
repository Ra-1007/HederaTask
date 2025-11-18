package com.andronicus.routeplanner;

import java.util.*;

public class OptimalPriorityTSP {

    private int n;
    private List<DeliveryPoint> points;
    private double[][] dist;
    private int[] priority; // 0=high, 1=medium, 2=low
    private Map<Long, Double> memo;

    public OptimalPriorityTSP(List<DeliveryPoint> pts) {
        this.points = pts;
        this.n = pts.size();
        this.memo = new HashMap<>();

        this.priority = new int[n];
        for (int i = 0; i < n; i++) {
            switch (pts.get(i).priority.toLowerCase()) {
                case "high" -> priority[i] = 0;
                case "medium" -> priority[i] = 1;
                case "low" -> priority[i] = 2;
                default -> throw new IllegalArgumentException("Invalid priority");
            }
        }

        this.dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = DistanceUtils.distance(pts.get(i), pts.get(j));
            }
        }
    }

    private boolean violatesPriority(int mask, int next) {

        int highVisited = 0, mediumVisited = 0;
        int highTotal = 0, mediumTotal = 0;

        for (int i = 0; i < n; i++) {
            if (priority[i] == 0) highTotal++;
            if (priority[i] == 1) mediumTotal++;
        }

        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) != 0) {
                if (priority[i] == 0) highVisited++;
                if (priority[i] == 1) mediumVisited++;
            }
        }

        // If next is medium but not all highs visited
        if (priority[next] == 1 && highVisited < highTotal)
            return true;

        // If next is low but highs or mediums not visited completely
        if (priority[next] == 2 &&
                (highVisited < highTotal || mediumVisited < mediumTotal))
            return true;

        return false;
    }

    private double tsp(int mask, int last) {

        if (mask == (1 << n) - 1)
            return 0;

        long key = (((long) mask) << 5) | last;

        if (memo.containsKey(key))
            return memo.get(key);

        double best = 1e18;

        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0)
                continue;

            if (violatesPriority(mask, next))
                continue;

            double cost = dist[last][next] +
                    tsp(mask | (1 << next), next);

            if (cost < best)
                best = cost;
        }

        memo.put(key, best);
        return best;
    }

    public List<DeliveryPoint> solve() {
        double bestCost = 1e18;
        int bestStart = -1;

        // Only High priority points can be valid starting points
        for (int i = 0; i < n; i++) {
            if (priority[i] == 0) {
                memo.clear();
                double cost = tsp(1 << i, i);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestStart = i;
                }
            }
        }

        // reconstruct the optimal path
        return reconstruct(bestStart);
    }

    private List<DeliveryPoint> reconstruct(int start) {

        List<DeliveryPoint> path = new ArrayList<>();
        int mask = 1 << start;
        int last = start;
        path.add(points.get(last));

        while (mask != (1 << n) - 1) {
            double bestNextCost = 1e18;
            int bestNext = -1;

            for (int next = 0; next < n; next++) {

                if ((mask & (1 << next)) != 0)
                    continue;

                if (violatesPriority(mask, next))
                    continue;

                double cost = dist[last][next] +
                        tsp(mask | (1 << next), next);

                if (cost < bestNextCost) {
                    bestNextCost = cost;
                    bestNext = next;
                }
            }

            path.add(points.get(bestNext));
            mask |= 1 << bestNext;
            last = bestNext;
        }

        return path;
    }
}
