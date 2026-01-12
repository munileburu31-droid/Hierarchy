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

        List<Integer> filteredIds = new ArrayList<>();
        List<Integer> filteredDepths = new ArrayList<>();

        boolean[] includeStack = new boolean[hierarchy.size()];

        for (int i = 0; i < hierarchy.size(); i++) {
            int nodeId = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            if (depth == 0) {
                includeStack[depth] = nodeIdPredicate.test(nodeId);
            } else {
                includeStack[depth] = nodeIdPredicate.test(nodeId) && includeStack[depth - 1];
            }

            if (includeStack[depth]) {
                filteredIds.add(nodeId);
                filteredDepths.add(depth);
            }
        }

        int[] idsArr = filteredIds.stream().mapToInt(Integer::intValue).toArray();
        int[] depthsArr = filteredDepths.stream().mapToInt(Integer::intValue).toArray();
        return new ArrayBasedHierarchy(idsArr, depthsArr);
    }
}
