package com.example.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class HierarchyFilter {

    public static Hierarchy filter(Hierarchy hierarchy, IntPredicate nodeIdPredicate) {

        // Nodes are processed from top to bottom as they appear.
        // A node is kept only if it passes the condition and all its parents pass.
        // If a node fails the condition, it and all its children are removed.
        // Children are not checked if their parent is already removed.
        // Sibling nodes are checked independently of each other.
        // Multiple root nodes are allowed and checked separately.
        // The original order of nodes is preserved.

        // List to hold filtered node IDs and depths
        List<Integer> filteredIds = new ArrayList<>();
        List<Integer> filteredDepths = new ArrayList<>();

        // Tracks whether ancestors passed the filter at each depth
        boolean[] includeStack = new boolean[hierarchy.size()];

        for (int i = 0; i < hierarchy.size(); i++) {
            int nodeId = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            // Keep node if it passes the predicate and all its parents passed
            includeStack[depth] = nodeIdPredicate.test(nodeId) && (depth == 0 || includeStack[depth - 1]);

            // Add node to filtered list if it passes
            if (includeStack[depth]) {
                filteredIds.add(nodeId);
                filteredDepths.add(depth);
            }
        }

        // Convert lists to arrays and return a new hierarchy
        int[] idsArr = filteredIds.stream().mapToInt(Integer::intValue).toArray();
        int[] depthsArr = filteredDepths.stream().mapToInt(Integer::intValue).toArray();
        return new ArrayBasedHierarchy(idsArr, depthsArr);
    }
}