package org.example.Trees;
//Author: Abdelnasser Ouda
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Heap, A Complete Binary Tree.
 * Each Node contains no smaller/larger than objects of its descendants.
 */
public abstract class Heap<T extends Comparable<T>> implements Tree<T> , Serializable {
    //
    // Heap Class Setup and Constructor
    //

    protected ArrayList<T> heap;

    public Heap() {
        heap = new ArrayList<>();
    }

    //
    // Heap Class Operation Methods
    //

    @Override
    public void insert(T value) {
        heap.add(value); // add value to the Heap ArrayList

        // Reheap (up) the heap array
        heapifyUp(heap.size() - 1);
    }

    @Override
    public boolean delete(T value) {
        // finds index of value to delete
        int index = heap.indexOf(value);
        if (index == -1) return false;

        // swap the index to be deleted to the last Index, then delete the new last index
        int lastIndex = heap.size() - 1;
        swap(index, lastIndex);
        heap.remove(lastIndex);

        // reheap (down) the heap
        if (index < heap.size()) {
            heapifyDown(index);
        }

        return true;
    }

    @Override
    public boolean contains(T value) {
        return heap.contains(value);
    }

    @Override
    public void clear() {
        heap.clear();
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public List<T> inorderTraversal() {
        return new ArrayList<>(heap);
    }

    @Override
    public TreeNode<T> getRoot() {
        return heap.isEmpty() ? null : new HeapNode(0);
    }

    /**
    * These are the methods to use in MinHeap and MaxHeap, Reheap for Insertion (up) and Deletion (down)
    */
    protected abstract void heapifyUp(int index);
    protected abstract void heapifyDown(int index);

    protected void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    protected int getParentIndex(int i) {
        return (i - 1) / 2;
    }

    protected int getLeftChildIndex(int i) {
        return 2 * i + 1;
    }

    protected int getRightChildIndex(int i) {
        return 2 * i + 2;
    }

    /**
     * Heap Node Class
     */
    private class HeapNode implements TreeNode<T> {
        //
        // Heap Node Class Setup and Constructor
        //
        private int index;

        HeapNode(int index) {
            this.index = index;
        }

        //
        // Heap Node Class Helper Method
        //

        public String getColor() { return "null"; }

        //
        // Heap Node Class Operation Methods
        //

        @Override
        public T getValue() {
            return heap.get(index);
        }

        @Override
        public TreeNode<T> getLeft() {
            int leftIndex = getLeftChildIndex(index);
            return leftIndex < heap.size() ? new HeapNode(leftIndex) : null;
        }

        @Override
        public TreeNode<T> getRight() {
            int rightIndex = getRightChildIndex(index);
            return rightIndex < heap.size() ? new HeapNode(rightIndex) : null;
        }


    }
}