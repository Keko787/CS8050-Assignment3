package org.example.Trees;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AVL Tree Implementation
 * A self-balancing binary search tree where the heights of the two child
 * subtrees of any node differ by at most one.
 *
 * Properties:
 * 1. Binary Search Tree property (left < parent < right)
 * 2. Balance factor of each node is -1, 0, or 1
 * 3. Balance factor = height(left subtree) - height(right subtree)
 * 4. Automatically rebalances after insertions and deletions
 */
public class AVLTree<T extends Comparable<T>> implements Tree<T>, Serializable {
    private Node root;
    private int size;

    /**
     * Node class for AVL Tree
     */
    private class Node implements TreeNode<T>, Serializable {
        //
        // Node Class Setup and Constructor
        //
        T value;
        Node left, right;
        int height; // Height of the subtree rooted at this node

        Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1; // New node is initially at height 1
        }

        //
        // Node Class Operator Methods
        //

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public TreeNode<T> getLeft() {
            return left;
        }

        @Override
        public TreeNode<T> getRight() {
            return right;
        }

        @Override
        public String getColor() {
            // Return balance factor for visualization
            int balance = getBalance(this);
            if (balance > 1 || balance < -1) {
                return "UNBALANCED"; // Should never happen in valid AVL tree
            }
            return "BALANCED";
        }
    }

    //
    // AVL Tree Constructor
    //

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    //
    // AVL Tree Helper Methods
    //

    @Override
    public String type() {
        return "AVL Tree";
    }

    @Override
    public Color color() {
        return Color.GREEN;
    }

    //
    // AVL Tree Operation Methods
    //

    /**
     * Insert
     */
    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null value");
        }
        root = insert(root, value);
    }

    private Node insert(Node node, T value) {
        // 1. Perform standard BST insertion
        if (node == null) {
            size++;
            return new Node(value);
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        } else {
            // Duplicate value, don't insert
            return node;
        }

        // 2. Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // 3. Get the balance factor
        int balance = getBalance(node);

        // 4. If node is unbalanced, then there are 4 cases

        // Left-Left Case (Right Rotation)
        if (balance > 1 && value.compareTo(node.left.value) < 0) {
            return rotateRight(node);
        }

        // Right-Right Case (Left Rotation)
        if (balance < -1 && value.compareTo(node.right.value) > 0) {
            return rotateLeft(node);
        }

        // Left-Right Case (Left rotation on left child, then Right rotation)
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-Left Case (Right rotation on right child, then Left rotation)
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // Return the (unchanged) node pointer
        return node;
    }

    /**
     * Delete
     */
    @Override
    public boolean delete(T value) {
        if (root == null || value == null) {
            return false;
        }

        int initialSize = size;
        root = delete(root, value);
        return size < initialSize;
    }

    private Node delete(Node node, T value) {
        // 1. Perform standard BST deletion
        if (node == null) {
            return null;
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = delete(node.left, value);
        } else if (cmp > 0) {
            node.right = delete(node.right, value);
        } else {
            // Node to be deleted found
            size--;

            // Node with only one child or no child
            if (node.left == null || node.right == null) {
                Node temp = (node.left != null) ? node.left : node.right;

                // No child case
                if (temp == null) {
                    return null;
                } else {
                    // One child case
                    return temp;
                }
            } else {
                // Node with two children: get inorder successor (smallest in right subtree)
                Node successor = getMin(node.right);
                node.value = successor.value;
                node.right = delete(node.right, successor.value);
                size++; // Compensate for the decrement that will happen when we delete successor
            }
        }

        // 2. Update height of current node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // 3. Get the balance factor
        int balance = getBalance(node);

        // 4. If node is unbalanced, then there are 4 cases

        // Left-Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Left-Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Right-Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    @Override
    public boolean contains(T value) {
        return search(root, value);
    }

    private boolean search(Node node, T value) {
        if (node == null || value == null) {
            return false;
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            return search(node.left, value);
        } else if (cmp > 0) {
            return search(node.right, value);
        } else {
            return true;
        }
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

    /**
     * Inorder Search
     */
    @Override
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(Node node, List<T> result) {
        if (node != null) {
            inorderTraversal(node.left, result);
            result.add(node.value);
            inorderTraversal(node.right, result);
        }
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }

    //
    // AVL Tree Specific Methods
    //

    /**
     * Get the height of a node
     */
    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    /**
     * Get the balance factor of a node
     * Balance factor = height(left subtree) - height(right subtree)
     * Valid values: -1, 0, 1 (for balanced AVL tree)
     */
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    /**
     * Get the node with minimum value (leftmost node)
     */
    private Node getMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Get the node with maximum value (rightmost node)
     */
    private Node getMax(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    //
    // Helper methods for debugging
    //

    /**
     * Check if the tree is balanced (for debugging and testing on runtime)
     */
    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(Node node) {
        if (node == null) {
            return true;
        }

        int balance = getBalance(node);
        if (Math.abs(balance) > 1) {
            return false;
        }

        return isBalanced(node.left) && isBalanced(node.right);
    }

    /**
     * Get the height of the tree (to test height and other height checking methods)
     */
    public int getHeight() {
        return height(root);
    }
}
