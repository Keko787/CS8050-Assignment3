package org.example.Trees;

import javafx.scene.paint.Color;

/**
 * MinHeap implementation where the smallest element is at the root.
 * Parent node is always smaller than its children.
 */
public class MinHeap<T extends Comparable<T>> extends Heap<T> {

    //
    // Class Setup
    //

    @Override
    public String type() {
        return "MinHeap";
    }

    @Override
    public Color color() {
        return Color.LIGHTBLUE;
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

            // If current element is smaller than parent, swap
            if (heap.get(index).compareTo(heap.get(parentIndex)) < 0) {
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
            int smallestIndex = index;

            // Find the smallest among current, left child, and right child
            if (leftIndex < heap.size() &&
                heap.get(leftIndex).compareTo(heap.get(smallestIndex)) < 0) {
                smallestIndex = leftIndex;
            }

            if (rightIndex < heap.size() &&
                heap.get(rightIndex).compareTo(heap.get(smallestIndex)) < 0) {
                smallestIndex = rightIndex;
            }

            // If current is not the smallest, swap with smallest child
            if (smallestIndex != index) {
                swap(index, smallestIndex);
                index = smallestIndex;
            } else {
                break; // Heap property satisfied
            }
        }
    }
}
