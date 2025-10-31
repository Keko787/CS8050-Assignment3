package org.example.Trees;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Red-Black Tree Implementation
 * Properties:
 * 1. Every node is either red or black
 * 2. Root is always black
 * 3. All leaves (NIL) are black
 * 4. If a node is red, both children are black (no two red nodes in a row)
 * 5. All paths from root to leaves have the same number of black nodes
 */
public class RedBlackTree<T extends Comparable<T>> implements Tree<T>, Serializable {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root;
    private int size;

    /**
     * Node class for Red-Black Tree
     */
    private class Node implements TreeNode<T>, Serializable {
        T value;
        Node left, right, parent;
        boolean color; // RED = true, BLACK = false

        Node(T value) {
            this.value = value;
            this.color = RED; // New nodes are always red
            this.left = null;
            this.right = null;
            this.parent = null;
        }

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
            return color == RED ? "RED" : "BLACK";
        }

        public Node getParent() {
            return parent;
        }

        public boolean isRed() {
            return color == RED;
        }

        public boolean isBlack() {
            return color == BLACK;
        }
    }

    //
    // Constructor
    //

    public RedBlackTree() {
        this.root = null;
        this.size = 0;
    }

    //
    // Tree Interface Methods
    //

    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null value");
        }

        Node newNode = new Node(value);

        if (root == null) {
            root = newNode;
            root.color = BLACK; // Root must be black
            size++;
            return;
        }

        // Standard BST insert
        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;
            int cmp = value.compareTo(current.value);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Value already exists, don't insert
                return;
            }
        }

        newNode.parent = parent;
        int cmp = value.compareTo(parent.value);
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;

        // Fix Red-Black Tree properties
        fixInsert(newNode);
    }

    private void fixInsert(Node node) {
        while (node != root && node.parent.color == RED) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (parent == grandparent.left) {
                Node uncle = grandparent.right;

                // Case 1: Uncle is red - recolor
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    node = grandparent;
                } else {
                    // Case 2: Node is right child - left rotate
                    if (node == parent.right) {
                        node = parent;
                        rotateLeft(node);
                        parent = node.parent;
                    }
                    // Case 3: Node is left child - right rotate and recolor
                    parent.color = BLACK;
                    grandparent.color = RED;
                    rotateRight(grandparent);
                }
            } else {
                Node uncle = grandparent.left;

                // Case 1: Uncle is red - recolor
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    node = grandparent;
                } else {
                    // Case 2: Node is left child - right rotate
                    if (node == parent.left) {
                        node = parent;
                        rotateRight(node);
                        parent = node.parent;
                    }
                    // Case 3: Node is right child - left rotate and recolor
                    parent.color = BLACK;
                    grandparent.color = RED;
                    rotateLeft(grandparent);
                }
            }
        }
        root.color = BLACK; // Root must always be black
    }

    @Override
    public boolean delete(T value) {
        if (root == null || value == null) {
            return false;
        }

        Node nodeToDelete = findNode(root, value);
        if (nodeToDelete == null) {
            return false;
        }

        deleteNode(nodeToDelete);
        size--;
        return true;
    }

    private Node findNode(Node node, T value) {
        if (node == null) {
            return null;
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            return findNode(node.left, value);
        } else if (cmp > 0) {
            return findNode(node.right, value);
        } else {
            return node;
        }
    }

    private void deleteNode(Node node) {
        Node replacement;
        Node nodeToFix;

        // Node has two children
        if (node.left != null && node.right != null) {
            Node successor = minimum(node.right);
            node.value = successor.value;
            node = successor;
        }

        // Node has at most one child
        replacement = (node.left != null) ? node.left : node.right;

        if (replacement != null) {
            // Node has one child
            replacement.parent = node.parent;

            if (node.parent == null) {
                root = replacement;
            } else if (node == node.parent.left) {
                node.parent.left = replacement;
            } else {
                node.parent.right = replacement;
            }

            node.left = node.right = node.parent = null;

            // Fix if deleted node was black
            if (node.color == BLACK) {
                fixDelete(replacement);
            }
        } else if (node.parent == null) {
            // Deleting root with no children
            root = null;
        } else {
            // Node has no children
            if (node.color == BLACK) {
                fixDelete(node);
            }

            if (node.parent != null) {
                if (node == node.parent.left) {
                    node.parent.left = null;
                } else {
                    node.parent.right = null;
                }
                node.parent = null;
            }
        }
    }

    private void fixDelete(Node node) {
        while (node != root && (node == null || node.color == BLACK)) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;

                // Case 1: Sibling is red
                if (sibling != null && sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }

                // Case 2: Sibling is black with two black children
                if ((sibling == null) ||
                    ((sibling.left == null || sibling.left.color == BLACK) &&
                     (sibling.right == null || sibling.right.color == BLACK))) {
                    if (sibling != null) {
                        sibling.color = RED;
                    }
                    node = node.parent;
                } else {
                    // Case 3: Sibling is black with red left child and black right child
                    if (sibling.right == null || sibling.right.color == BLACK) {
                        if (sibling.left != null) {
                            sibling.left.color = BLACK;
                        }
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }

                    // Case 4: Sibling is black with red right child
                    if (sibling != null) {
                        sibling.color = node.parent.color;
                        node.parent.color = BLACK;
                        if (sibling.right != null) {
                            sibling.right.color = BLACK;
                        }
                        rotateLeft(node.parent);
                    }
                    node = root;
                }
            } else {
                Node sibling = node.parent.left;

                // Case 1: Sibling is red
                if (sibling != null && sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }

                // Case 2: Sibling is black with two black children
                if ((sibling == null) ||
                    ((sibling.left == null || sibling.left.color == BLACK) &&
                     (sibling.right == null || sibling.right.color == BLACK))) {
                    if (sibling != null) {
                        sibling.color = RED;
                    }
                    node = node.parent;
                } else {
                    // Case 3: Sibling is black with red right child and black left child
                    if (sibling.left == null || sibling.left.color == BLACK) {
                        if (sibling.right != null) {
                            sibling.right.color = BLACK;
                        }
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }

                    // Case 4: Sibling is black with red left child
                    if (sibling != null) {
                        sibling.color = node.parent.color;
                        node.parent.color = BLACK;
                        if (sibling.left != null) {
                            sibling.left.color = BLACK;
                        }
                        rotateRight(node.parent);
                    }
                    node = root;
                }
            }
        }

        if (node != null) {
            node.color = BLACK;
        }
    }

    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public boolean contains(T value) {
        return findNode(root, value) != null;
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
        if (node != null) {
            inorderTraversal(node.left, result);
            result.add(node.value);
            inorderTraversal(node.right, result);
        }
    }

    @Override
    public String type() {
        return "RBT";
    }

    @Override
    public Color color() {
        return Color.DARKRED;
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }

    //
    // Rotation Methods
    //

    private void rotateLeft(Node node) {
        Node rightChild = node.right;
        node.right = rightChild.left;

        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.parent = node.parent;

        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        rightChild.left = node;
        node.parent = rightChild;
    }

    private void rotateRight(Node node) {
        Node leftChild = node.left;
        node.left = leftChild.right;

        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.parent = node.parent;

        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.left) {
            node.parent.left = leftChild;
        } else {
            node.parent.right = leftChild;
        }

        leftChild.right = node;
        node.parent = leftChild;
    }
}
