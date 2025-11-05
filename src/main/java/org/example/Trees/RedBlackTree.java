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
    //
    // Red Black Tree Class Setup
    //

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
    // Red Black Tree Constructor
    //

    public RedBlackTree() {
        this.root = null;
        this.size = 0;
    }

    //
    // Red Black Tree Helper Methods
    //

    @Override
    public String type() {
        return "RBT";
    }

    @Override
    public Color color() {
        return Color.DARKRED;
    }

    //
    // Red Black Tree Operation Methods
    //

    /**
     * Insert
     */
    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null value");
        }

        Node newNode = new Node(value); // make the new node block

        // if no root, make new root
        if (root == null) {
            root = newNode;
            root.color = BLACK; // Root must be black
            size++;
            return;
        }

        // if root exist, insert the value using BST properties
        /*
        * Standard BST insert to insert the new value
        */

        // init current as root and parent as null
        Node current = root;
        Node parent = null;

        // Edge case: while the current node is not null, navigate the element based on the input value
        while (current != null) {
            // set parrent to current value, at start - root
            parent = current;
            // make an integer comparison metric based on the input value and current element's value
            int cmp = value.compareTo(current.value);

            // if comparison metric less than 0, set current element to the left child
            if (cmp < 0) {
                current = current.left;
            // If comparison metric greater than 0, set current element to the right child
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Value already exists, don't insert
                return;
            }
        }

        // check the parent which is the parent of current element
        newNode.parent = parent;
        int cmp = value.compareTo(parent.value);
        // if the current element is less than the input value, put it in left child
        if (cmp < 0) {
            parent.left = newNode;
        } else {  // if the current element is less than the input value, put it in left child
            parent.right = newNode;
        }

        size++;
        // eof BST Insert

        // Fix Red-Black Tree color properties
        fixInsert(newNode);
    }

    // Ensures the Red Black Tree Color is correct
    private void fixInsert(Node node) {
        // loop conditions: loop until node is root and parent of node is red
        while (node != root && node.parent.color == RED) {
            // init input node's parent and grandparent
            Node parent = node.parent;
            Node grandparent = parent.parent;

            // if parent is less than grandparent
            if (parent == grandparent.left) {
                // uncle is right child
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
                // if parent is right child, then uncle is left child
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

    /**
     * Delete
     */
    @Override
    public boolean delete(T value) {
        // if value or root doesnt exit dont delete
        if (root == null || value == null) {
            return false;
        }

        // if cant find node dont delete
        Node nodeToDelete = findNode(root, value);
        if (nodeToDelete == null) {
            return false;
        }

        // delete node if found and decrease size
        deleteNode(nodeToDelete);
        size--;
        return true;
    }

    private Node findNode(Node node, T value) {
        // base case: if node is null return no match
        if (node == null) {
            return null;
        }

        // compare inputted value to inputted node
        int cmp = value.compareTo(node.value);
        // if comparison is value is less than node, recursively call using left child
        if (cmp < 0) {
            return findNode(node.left, value);
        }
        else if (cmp > 0) {
            // if comparison is value is greater than node, recursively call using right child
            return findNode(node.right, value);
        }
        // if comparison is the same, the node is found
        else {
            return node;
        }
    }

    // delete the node and rearrange the nodes with the red black tree scheme in mind
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

    /**
     * Inorder Traversal
     */
    @Override
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();  // creats the list of nodes
        inorderTraversal(root, result);  // performs the traversal
        return result;
    }

    private void inorderTraversal(Node node, List<T> result) {
        if (node != null) { // return condition edge case
            inorderTraversal(node.left, result);  // rec call on left sibling
            result.add(node.value);  // add it to the list
            inorderTraversal(node.right, result);  // rec call on right sibling
        }
    }

    // find the left most node
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
    public TreeNode<T> getRoot() {
        return root;
    }

    /**
    * Rotation Methods
    */

    private void rotateLeft(Node node) {
        // Init right child of the node being rotated
        Node rightChild = node.right;
        // take the left subtree of right child and make it the right subtree of the node being rotated
        node.right = rightChild.left;

        // if transfered subtree exists, update the parent to pointer to the node being transfered
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        // set the right child parent pointer to node's original parent, to move the right child up
        rightChild.parent = node.parent;

        // if node is root, right child becomes root
        if (node.parent == null) {
            root = rightChild;
        }
        // if node was a left child, update the node's parent left child pointer to right child
         else if (node == node.parent.left) {
            node.parent.left = rightChild;
        }
        // if node was a right child, update the node's parent right child pointer to the node's right child
        else {
            node.parent.right = rightChild;
        }

        // The node is rotated as right child's left child
        rightChild.left = node;
        node.parent = rightChild;
    }

    private void rotateRight(Node node) {
        // Init left child of the node being rotated
        Node leftChild = node.left;
        // take the right subtree of left child and make it the left subtree of the node being rotated
        node.left = leftChild.right;

        // if transfered subtree exists, update the parent to pointer to the node being transfered
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        // set the left child parent pointer to node's original parent, to move the left child up
        leftChild.parent = node.parent;

        // if node is root, right child becomes root
        if (node.parent == null) {
            root = leftChild;
        }
        // if node was a left child, update the node's parent left child pointer to node's left child
        else if (node == node.parent.left) {
            node.parent.left = leftChild;
        }
        // if node was a right child, update the node's parent right child pointer to the node's left child
        else {
            node.parent.right = leftChild;
        }

        // The node is rotated as left child's right child
        leftChild.right = node;
        node.parent = leftChild;
    }
}
