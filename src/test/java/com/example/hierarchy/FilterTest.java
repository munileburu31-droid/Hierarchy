package com.example.hierarchy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTest {

    @Test
    void testFilter() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );

        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);

        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 5, 8, 10, 11},
                new int[]{0, 1, 1, 0, 1, 2}
        );

        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testFilter_RootFails() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1,2,3},
                new int[]{0,1,2}
        );

        Hierarchy filtered = HierarchyFilter.filter(hierarchy, node -> node != 1);
        assertEquals("[]", filtered.formatString());
    }

    @Test
    void testFilter_LeafFails() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1,2,3},
                new int[]{0,1,2}
        );

        Hierarchy filtered = HierarchyFilter.filter(hierarchy, node -> node != 3);
        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{1,2},
                new int[]{0,1}
        );

        assertEquals(expected.formatString(), filtered.formatString());
    }

    @Test
    void testFilter_IntermediateFails() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1,2,3,4},
                new int[]{0,1,2,3}
        );

        // node 2 fails, nodes 3 and 4 under it are removed
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, node -> node != 2);
        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{1},
                new int[]{0}
        );

        assertEquals(expected.formatString(), filtered.formatString());
    }

    @Test
    void testFilter_MultipleRoots() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1,2,3,6,7,8},
                new int[]{0,1,2,0,1,0}
        );

        // root 6 fails → subtree under 6 removed
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, node -> node != 6);
        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{1,2,3,8},
                new int[]{0,1,2,0}
        );

        assertEquals(expected.formatString(), filtered.formatString());
    }
    @Test
    void testFilter_SiblingNodesFail() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5},
                new int[]{0, 1, 2, 1, 1}
        );
        // node 2 fails → its subtree (3) removed, siblings 4 & 5 remain
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, node -> node != 2);
        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{1, 4, 5},
                new int[]{0, 1, 1}
        );
        assertEquals(expected.formatString(), filtered.formatString());
    }
}
