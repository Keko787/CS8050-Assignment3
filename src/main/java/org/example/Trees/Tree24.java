package org.example.Trees;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2-4 Tree (B-Tree of order 4) Implementation
 * Properties:
 * - Each node has 1-3 keys
 * - Each internal node has 2-4 children
 * - All leaves are at the same level
 * - Keys in each node are in sorted order
 */
public class Tree24<T extends Comparable<T>> implements Tree<T>, Serializable {
    private Node root;
    private int size;

    /**
     * Node class for 2-4 Tree (B-Tree of order 4)
     * Each node can have:
     * - 1 to 3 keys (values)
     * - 0 to 4 children
     * - Keys are kept in sorted order
     */
    private class Node implements Serializable {
        List<T> keys;           // Values stored in this node (1-3 keys)
        List<Node> children;    // Child pointers (0-4 children)
        boolean isLeaf;         // True if this is a leaf node

        Node() {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = true;
        }

        Node(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }

        // Getters and setters
        List<T> getKeys() {
            return keys;
        }

        void setKeys(List<T> keys) {
            this.keys = keys;
        }

        List<Node> getChildren() {
            return children;
        }

        void setChildren(List<Node> children) {
            this.children = children;
        }

        boolean isLeaf() {
            return isLeaf;
        }

        void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

        // Utility methods
        int getKeyCount() {
            return keys.size();
        }

        int getChildCount() {
            return children.size();
        }

        T getKey(int index) {
            if (index >= 0 && index < keys.size()) {
                return keys.get(index);
            }
            return null;
        }

        Node getChild(int index) {
            if (index >= 0 && index < children.size()) {
                return children.get(index);
            }
            return null;
        }

        void addKey(T key) {
            keys.add(key);
            // Keep keys sorted
            keys.sort(Comparable::compareTo);
        }

        void addChild(Node child) {
            children.add(child);
        }

        void insertKeyAt(int index, T key) {
            keys.add(index, key);
        }

        void insertChildAt(int index, Node child) {
            children.add(index, child);
        }

        T removeKey(int index) {
            if (index >= 0 && index < keys.size()) {
                return keys.remove(index);
            }
            return null;
        }

        Node removeChild(int index) {
            if (index >= 0 && index < children.size()) {
                return children.remove(index);
            }
            return null;
        }

        boolean isFull() {
            return keys.size() >= 3;
        }

        boolean hasMinimumKeys() {
            return keys.size() >= 1;
        }

        /**
         * Find the index where the key should be inserted or where to search
         */
        int findKeyIndex(T key) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
                i++;
            }
            return i;
        }

        @Override
        public String toString() {
            return "Node{keys=" + keys + ", isLeaf=" + isLeaf + "}";
        }
    }

    public Tree24() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null value");
        }

        if (root == null) {
            root = new Node();
            root.addKey(value);
            size++;
            return;
        }

        // If root is full, split it
        if (root.isFull()) {
            Node newRoot = new Node(false);
            newRoot.getChildren().add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }

        insertNonFull(root, value);
        size++;
    }

    private void insertNonFull(Node node, T value) {
        int i = node.getKeyCount() - 1;

        if (node.isLeaf()) {
            // Insert into leaf node
            node.addKey(value);
        } else {
            // Find child to insert into
            while (i >= 0 && value.compareTo(node.getKey(i)) < 0) {
                i--;
            }
            i++;

            Node child = node.getChild(i);

            // Split child if full
            if (child.isFull()) {
                splitChild(node, i);

                // After split, determine which child to insert into
                if (value.compareTo(node.getKey(i)) > 0) {
                    i++;
                }
            }

            insertNonFull(node.getChild(i), value);
        }
    }

    private void splitChild(Node parent, int index) {
        Node fullChild = parent.getChild(index);
        Node newChild = new Node(fullChild.isLeaf());

        // Move middle key up to parent
        T middleKey = fullChild.getKey(1);
        parent.insertKeyAt(index, middleKey);

        // Split keys: left child keeps key[0], right child gets key[2]
        newChild.addKey(fullChild.getKey(2));
        fullChild.getKeys().remove(2);
        fullChild.getKeys().remove(1);

        // Split children if not a leaf
        if (!fullChild.isLeaf()) {
            // Move last 2 children to new node
            newChild.addChild(fullChild.getChild(2));
            newChild.addChild(fullChild.getChild(3));
            fullChild.getChildren().remove(3);
            fullChild.getChildren().remove(2);
        }

        // Insert new child into parent
        parent.insertChildAt(index + 1, newChild);
    }

    @Override
    public boolean delete(T value) {
        if (root == null || value == null) {
            return false;
        }

        boolean deleted = deleteFromNode(root, value);

        // If root is empty after deletion, make its only child the new root
        if (root.getKeyCount() == 0) {
            if (!root.isLeaf() && root.getChildCount() > 0) {
                root = root.getChild(0);
            } else {
                root = null;
            }
        }

        if (deleted) {
            size--;
        }

        return deleted;
    }

    private boolean deleteFromNode(Node node, T value) {
        int i = node.findKeyIndex(value);

        if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
            // Key found in this node
            if (node.isLeaf()) {
                node.removeKey(i);
                return true;
            } else {
                return deleteFromInternalNode(node, i);
            }
        } else if (node.isLeaf()) {
            // Key not found
            return false;
        } else {
            // Key might be in subtree
            boolean isInLastChild = (i == node.getKeyCount());

            Node child = node.getChild(i);

            // Ensure child has at least 2 keys before descending
            if (child.getKeyCount() < 2) {
                fillChild(node, i);

                // After filling, the key position might have changed
                i = node.findKeyIndex(value);
                if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
                    child = node;
                    return deleteFromInternalNode(node, i);
                }

                if (isInLastChild && i > node.getKeyCount()) {
                    i = node.getKeyCount();
                }

                child = node.getChild(i);
            }

            return deleteFromNode(child, value);
        }
    }

    private boolean deleteFromInternalNode(Node node, int index) {
        T key = node.getKey(index);

        if (node.getChild(index).getKeyCount() >= 2) {
            T predecessor = getPredecessor(node, index);
            node.getKeys().set(index, predecessor);
            return deleteFromNode(node.getChild(index), predecessor);
        } else if (node.getChild(index + 1).getKeyCount() >= 2) {
            T successor = getSuccessor(node, index);
            node.getKeys().set(index, successor);
            return deleteFromNode(node.getChild(index + 1), successor);
        } else {
            merge(node, index);
            return deleteFromNode(node.getChild(index), key);
        }
    }

    private T getPredecessor(Node node, int index) {
        Node current = node.getChild(index);
        while (!current.isLeaf()) {
            current = current.getChild(current.getChildCount() - 1);
        }
        return current.getKey(current.getKeyCount() - 1);
    }

    private T getSuccessor(Node node, int index) {
        Node current = node.getChild(index + 1);
        while (!current.isLeaf()) {
            current = current.getChild(0);
        }
        return current.getKey(0);
    }

    private void fillChild(Node node, int index) {
        // Try to borrow from left sibling
        if (index != 0 && node.getChild(index - 1).getKeyCount() >= 2) {
            borrowFromLeft(node, index);
        }
        // Try to borrow from right sibling
        else if (index != node.getKeyCount() && node.getChild(index + 1).getKeyCount() >= 2) {
            borrowFromRight(node, index);
        }
        // Merge with sibling
        else {
            if (index != node.getKeyCount()) {
                merge(node, index);
            } else {
                merge(node, index - 1);
            }
        }
    }

    private void borrowFromLeft(Node node, int childIndex) {
        Node child = node.getChild(childIndex);
        Node sibling = node.getChild(childIndex - 1);

        // Move a key from parent to child
        child.getKeys().add(0, node.getKey(childIndex - 1));

        // Move a key from sibling to parent
        node.getKeys().set(childIndex - 1, sibling.getKey(sibling.getKeyCount() - 1));
        sibling.removeKey(sibling.getKeyCount() - 1);

        // Move child pointer if not leaf
        if (!child.isLeaf()) {
            child.getChildren().add(0, sibling.getChild(sibling.getChildCount() - 1));
            sibling.removeChild(sibling.getChildCount() - 1);
        }
    }

    private void borrowFromRight(Node node, int childIndex) {
        Node child = node.getChild(childIndex);
        Node sibling = node.getChild(childIndex + 1);

        // Move a key from parent to child
        child.addKey(node.getKey(childIndex));

        // Move a key from sibling to parent
        node.getKeys().set(childIndex, sibling.getKey(0));
        sibling.removeKey(0);

        // Move child pointer if not leaf
        if (!child.isLeaf()) {
            child.addChild(sibling.getChild(0));
            sibling.removeChild(0);
        }
    }

    private void merge(Node node, int index) {
        Node child = node.getChild(index);
        Node sibling = node.getChild(index + 1);

        // Pull key from this node and merge with right sibling
        child.addKey(node.getKey(index));

        // Copy keys from sibling to child
        for (T key : sibling.getKeys()) {
            child.addKey(key);
        }

        // Copy child pointers from sibling to child
        if (!child.isLeaf()) {
            for (Node grandChild : sibling.getChildren()) {
                child.addChild(grandChild);
            }
        }

        // Remove the key from this node
        node.removeKey(index);

        // Remove the sibling
        node.removeChild(index + 1);
    }

    @Override
    public boolean contains(T value) {
        return search(root, value);
    }

    private boolean search(Node node, T value) {
        if (node == null || value == null) {
            return false;
        }

        int i = 0;
        while (i < node.getKeyCount() && value.compareTo(node.getKey(i)) > 0) {
            i++;
        }

        if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
            return true;
        }

        if (node.isLeaf()) {
            return false;
        }

        return search(node.getChild(i), value);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(Node node, List<T> result) {
        if (node == null) {
            return;
        }

        int i;
        for (i = 0; i < node.getKeyCount(); i++) {
            // Visit child before key
            if (!node.isLeaf()) {
                inorderTraversal(node.getChild(i), result);
            }
            // Visit key
            result.add(node.getKey(i));
        }

        // Visit last child
        if (!node.isLeaf()) {
            inorderTraversal(node.getChild(i), result);
        }
    }

    @Override
    public String type() {
        return "2-4 Tree";
    }

    @Override
    public Color color() {
        return Color.BLUE;
    }

    @Override
    public TreeNode<T> getRoot() {
        // 2-4 tree doesn't use TreeNode interface directly
        // We'll create an adapter for visualization
        return null;
    }

    public Node get24Root() {
        return root;
    }
}
