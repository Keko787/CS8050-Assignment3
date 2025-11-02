package org.example.Trees;

import javafx.scene.paint.Color;

/**
 * MaxHeap implementation where the largest element is at the root.
 * Parent node is always larger than its children.
 */
public class MaxHeap<T extends Comparable<T>> extends Heap<T> {

    //
    // Class Setup
    //

    @Override
    public String type() {
        return "MaxHeap";
    }

    @Override
    public Color color() {
        return Color.LIGHTCORAL;
    }

    //
    // Operator Methods
    //

    // Reheap for insertion
    @Override
    protected void heapifyUp(int index) {
        // Move the element up until heap property is satisfied
        while (index > 0) {
            int parentIndex = getParentIndex(index);

            // If current element is larger than parent, swap
            if (heap.get(index).compareTo(heap.get(parentIndex)) > 0) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break; // Heap property satisfied
            }
        }
    }

    // Reheap for deletion
    @Override
    protected void heapifyDown(int index) {
        // Move the element down until heap property is satisfied
        while (true) {
            int leftIndex = getLeftChildIndex(index);
            int rightIndex = getRightChildIndex(index);
            int largestIndex = index;

            // Find the largest among current, left child, and right child
            if (leftIndex < heap.size() &&
                heap.get(leftIndex).compareTo(heap.get(largestIndex)) > 0) {
                largestIndex = leftIndex;
            }

            if (rightIndex < heap.size() &&
                heap.get(rightIndex).compareTo(heap.get(largestIndex)) > 0) {
                largestIndex = rightIndex;
            }

            // If current is not the largest, swap with largest child
            if (largestIndex != index) {
                swap(index, largestIndex);
                index = largestIndex;
            } else {
                break; // Heap property satisfied
            }
        }
    }
}
