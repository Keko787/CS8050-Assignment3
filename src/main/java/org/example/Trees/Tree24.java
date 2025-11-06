package org.example.Trees;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2-4 Tree Implementation
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
     * NODE CLASS for 2-4 Tree
     * Each node can have:
     * - 1 to 3 keys (values)
     * - 0 to 4 children ( x<s,  s<x<m, m<x<l, x>l)
     * - Keys are kept in sorted order
     */
    private class Node implements Serializable {
        //
        // Node Class Setup and Constructors
        //

        List<T> keys;           // Values stored in this node (1-3 keys)
        List<Node> children;    // Child pointers (0-4 children)
        boolean isLeaf;         // True if this is a leaf node

        Node() {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = true;
        }

        // separate constructor based on node being leaf node
        Node(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }

        //
        // Node Class Getters and setters
        //

        /**
        Keys
         */
        List<T> getKeys() {
            return keys;
        }

        void setKeys(List<T> keys) {
            this.keys = keys;
        }

        /**
         Children
         */
        List<Node> getChildren() {
            return children;
        }

        void setChildren(List<Node> children) {
            this.children = children;
        }

        /**
         Leaf
         */
        boolean isLeaf() {
            return isLeaf;
        }

        void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

        //
        // Node Class Utility methods
        //

        int getKeyCount() {
            return keys.size();
        }

        int getChildCount() {
            return children.size();
        }

        // get child on children array
        Node getChild(int index) {
            if (index >= 0 && index < children.size()) {
                return children.get(index);
            }
            return null;
        }

        // get keys on keys array
        T getKey(int index) {
            if (index >= 0 && index < keys.size()) {
                return keys.get(index);
            }
            return null;
        }

        //
        // Node Class Operators
        //

        /**
         * Add
         */
        void addKey(T key) {
            keys.add(key);
            // Keep keys sorted due to multiple keys in 1 node
            keys.sort(Comparable::compareTo);
        }

        void addChild(Node child) {
            children.add(child);
        }

        /**
         * Insert
         */

        void insertKeyAt(int index, T key) {
            keys.add(index, key);
        }

        void insertChildAt(int index, Node child) {
            children.add(index, child);
        }

        /**
         * Remove
         */
        // looks for index on key array to remove
        T removeKey(int index) {
            if (index >= 0 && index < keys.size()) {
                return keys.remove(index);
            }
            return null;
        }

        // looks for index on child array to remove
        Node removeChild(int index) {
            if (index >= 0 && index < children.size()) {
                return children.remove(index);
            }
            return null;
        }

        /**
         * Size Check and Navigation
         */

        boolean isFull() {  // node max size is 3
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

        //
        // Node Class Helper Method
        //

        @Override
        public String toString() {
            return "Node{keys=" + keys + ", isLeaf=" + isLeaf + "}";
        }
    }
    // End of Node Class

    //
    // 2-4 Tree Class Constructor
    //

    public Tree24() {
        this.root = null;
        this.size = 0;
    }

    //
    //  2-4 Tree Class Helper Methods
    //

    @Override
    public String type() {
        return "2-4 Tree";
    }

    @Override
    public Color color() {
        return Color.BLUE;
    }

    //
    //  2-4 Tree Class Operation Methods
    //

    /**
     * Insert
     */
    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null value");
        }

        // Base Case Handling
        // if no root, make a root
        if (root == null) {
            root = new Node();
            root.addKey(value);
            size++;
            return;
        }

        // Edge case: If root is full, split it
        if (root.isFull()) {
            Node newRoot = new Node(false); // make new node with leaf disabled
            newRoot.getChildren().add(root);  // make new root parent of root
            splitChild(newRoot, 0);  // split the root and replace with a new root
            root = newRoot;  // set the newRoot node as root
        }

        // Edge Case: insert into a non full node
        insertNonFull(root, value);
        size++;
    }

    // find the proper non full node to insert value at
    private void insertNonFull(Node node, T value) {
        // get index/pointer of rightmost/largest key in the current node
        int i = node.getKeyCount() - 1;

        // Case 1: Leaf Node - Direct Insertion
        if (node.isLeaf()) {
            // Insert into leaf node
            node.addKey(value);
        }
        // Case 2: Internal Node - Navigate Down
        else {
            // Find child to insert into
            while (i >= 0 && value.compareTo(node.getKey(i)) < 0) {
                i--;  // decrease pointer
            }
            i++;  // increase pointer by 1

            // Hand the full child: Split child if full
            // set the child node by getting the node's child at i
            Node child = node.getChild(i);
            if (child.isFull()) {
                splitChild(node, i);

                // Reorient After split, determine which child to insert into
                if (value.compareTo(node.getKey(i)) > 0) {
                    i++;  // Point to right child
                }
            }

            // Recursive Call with Target Child
            insertNonFull(node.getChild(i), value);
        }
    }

    // Insert Helper Function: Splits a full child node (with 3 keys) into two nodes.
    // Maintains the 2-4 tree property where each node has 1-3 keys
    private void splitChild(Node parent, int index) {

        // Init the full child with 3 keys and a new node that would be the right sibling
        Node fullChild = parent.getChild(index);
        Node newChild = new Node(fullChild.isLeaf());

        // Move middle key up to parent
        T middleKey = fullChild.getKey(1);  // middle index
        parent.insertKeyAt(index, middleKey);  // insert into parent

        // Split and Redistribute keys: left child keeps key[0], right child gets key[2]
        newChild.addKey(fullChild.getKey(2));  // add right key to new right sibling node
        fullChild.getKeys().remove(2);  // removes the key added to right sibling
        fullChild.getKeys().remove(1);  // remove middle node that is now in parent


        // Split and Redistribute children if not a leaf
        if (!fullChild.isLeaf()) {  // checks if the full node getting splitted has children
            // Move last 2 children to new node
            newChild.addChild(fullChild.getChild(2));
            newChild.addChild(fullChild.getChild(3));
            // Remove last 2 children from full child
            fullChild.getChildren().remove(3);
            fullChild.getChildren().remove(2);
        }

        // Insert new child into parent
        parent.insertChildAt(index + 1, newChild);
    }

    /**
     * Delete
     */
    @Override
    public boolean delete(T value) {

        // base case: if root or value doesnt exist return.
        if (root == null || value == null) {
            return false;
        }

        //
        boolean deleted = deleteFromNode(root, value);

        // If root is empty after deletion, make its only child the new root
        if (root.getKeyCount() == 0) {
            if (!root.isLeaf() && root.getChildCount() > 0) {
                root = root.getChild(0);
            } else {
                root = null;
            }
        }

        // if deletion is successful, reduce tree size
        if (deleted) {
            size--;
        }

        return deleted;
    }

    private boolean deleteFromNode(Node node, T value) {
        // get index/pointer of node based on key value
        int i = node.findKeyIndex(value);

        // Case 1: If Key is found in the node
        if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
            // Key found in this node
            if (node.isLeaf()) {  // if node is leaf, direct deletion
                node.removeKey(i);
                return true;
            }
            // if not, delete from internal node
            else {
                return deleteFromInternalNode(node, i);
            }
        }
        // Case 2: edge case - node is leaf, so its the end of the search
        else if (node.isLeaf()) {
            // Key not found
            return false;
        }
        // Case 3: Key might still be in tree - keep looking
        else {

            // records whether the last child is accessed
            boolean isInLastChild = (i == node.getKeyCount());

            // get the child at index i of node in question
            Node child = node.getChild(i);

            // Ensure child has at least 2 keys before descending
            if (child.getKeyCount() < 2) {
                fillChild(node, i);

                // After filling, research the key position based on the value
                i = node.findKeyIndex(value);

                // If Key is found in the node
                if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
                    child = node;  // update child ref to current node and return delete from internal node
                    return deleteFromInternalNode(node, i);
                }

                // edge case: handles if lastchild is the value but if keys shifted
                if (isInLastChild && i > node.getKeyCount()) {
                    i = node.getKeyCount();
                }

                // sets targeted child to descend into
                child = node.getChild(i);
            }

            // recursive call on child node
            return deleteFromNode(child, value);
        }
    }

    private boolean deleteFromInternalNode(Node node, int index) {
        // Saves key being deleted
        T key = node.getKey(index);

        // Case 1: If left child has >=2 keys - Replace with Predecessor
        if (node.getChild(index).getKeyCount() >= 2) {
            T predecessor = getPredecessor(node, index);  // get the number that is largest value smaller than the value in index
            node.getKeys().set(index, predecessor);  // replace with predecessor
            return deleteFromNode(node.getChild(index), predecessor);  // rec call. with predecessor
        }

        // Case 2: If right child has >=2 keys - Replace with Successor
        else if (node.getChild(index + 1).getKeyCount() >= 2) {
            T successor = getSuccessor(node, index);  // get smallest larger value than the value in index
            node.getKeys().set(index, successor);  // replace with successor
            return deleteFromNode(node.getChild(index + 1), successor); // rec call. with successor
        }

        // Case 3: If both children has <2 keys - Merge Children
        else {
            merge(node, index);  // merge both children of node
            return deleteFromNode(node.getChild(index), key);  // rec call. with merged child
        }
    }

    /**
     * Tree Sorting Operations
     */

    // Finds the predecessor - largest value smaller than a key at position index
    private T getPredecessor(Node node, int index) {
        Node current = node.getChild(index);  // the subtree left of the index
        while (!current.isLeaf()) {  // while the current node is not a leaf node
            current = current.getChild(current.getChildCount() - 1);  // traverse the tree to the right
        }
        return current.getKey(current.getKeyCount() - 1);  // Returns the nodes rightmost/largest key in leaf
    }

    //  Finds the successor - smallest value larger than a key at position index
    private T getSuccessor(Node node, int index) {
        Node current = node.getChild(index + 1);  // the subtree right of the index
        while (!current.isLeaf()) {  // while the current node is not a leaf node
            current = current.getChild(0);  // Moves to the leftmost child at each level
        }
        return current.getKey(0);  // Returns the nodes leftmost/smallest key in the leaf
    }

    // Ensures a child has at least 2 keys before deletion
    private void fillChild(Node node, int index) {
        // Try to borrow from left sibling
        if (index != 0 && node.getChild(index - 1).getKeyCount() >= 2) {
            borrowFromLeft(node, index);
        }
        // Try to borrow from right sibling
        else if (index != node.getKeyCount() && node.getChild(index + 1).getKeyCount() >= 2) {
            borrowFromRight(node, index);
        }
        // Try to Merge with sibling
        else {
            if (index != node.getKeyCount()) {  // if not rightmost child
                merge(node, index);  // merge with right sibling
            }
            else {  // if rightmost child
                merge(node, index - 1);  // merge with left sibling, not right
            }
        }
    }
    //  Borrows a key from left sibling through parent rotation.
    //  Child gains a key, left sibling loses a key, parent acts as intermediary
    private void borrowFromLeft(Node node, int childIndex) {
        // init the child that needs an extra key and the left sibling (one position to the left)
        Node child = node.getChild(childIndex);
        Node sibling = node.getChild(childIndex - 1);

        // Move a key from parent to child
        child.getKeys().add(0, node.getKey(childIndex - 1));

        // Move a key from sibling to parent
        node.getKeys().set(childIndex - 1, sibling.getKey(sibling.getKeyCount() - 1));
        sibling.removeKey(sibling.getKeyCount() - 1);

        // Move child pointer if not leaf
        if (!child.isLeaf()) {
            // Takes the rightmost child from sibling, Adds as child's leftmost child
            child.getChildren().add(0, sibling.getChild(sibling.getChildCount() - 1));
            sibling.removeChild(sibling.getChildCount() - 1);  // removes original child pointer
        }
    }

    // Borrows from right sibling through parent rotation.
    // Child gains a key, right sibling loses a key, parent acts as intermediary
    private void borrowFromRight(Node node, int childIndex) {
        // init the child that needs an extra key and the right sibling (one position to the right)
        Node child = node.getChild(childIndex);
        Node sibling = node.getChild(childIndex + 1);

        // Move a key from parent to child
        child.addKey(node.getKey(childIndex));

        // Move a key from sibling to parent
        node.getKeys().set(childIndex, sibling.getKey(0));
        sibling.removeKey(0);

        // Move child pointer if not leaf
        if (!child.isLeaf()) {
            // Takes sibling's leftmost child, Adds as child's rightmost child
            child.addChild(sibling.getChild(0));
            sibling.removeChild(0);
        }
    }

    // Merges child[index] with child[index + 1]
    // Parent's separator key joins the merge,
    // results in one combined child, parent loses a key and child
    private void merge(Node node, int index) {
        // init the left child (will receive all merged keys)
        // and the right child (will be merged into left child, then deleted)
        Node child = node.getChild(index);
        Node sibling = node.getChild(index + 1);

        // Pull key from parent, add it to the left child, and merge with right sibling
        child.addKey(node.getKey(index));

        // Copy keys from sibling to child
        for (T key : sibling.getKeys()) {  // loop through all keys in sibling
            child.addKey(key);
        }

        // Copy child pointers from sibling to child
        if (!child.isLeaf()) {  // internal nodes only
            for (Node grandChild : sibling.getChildren()) {  // iterates through sibling's children, transfering all child pointers
                child.addChild(grandChild);  // add each grandchild to left child
            }
        }

        // Remove the key from this node
        node.removeKey(index);

        // Remove the sibling
        node.removeChild(index + 1);
    }

    /**
    * Searches, Traversal and Size
    */

    @Override
    public boolean contains(T value) {
        return search(root, value);
    }

    private boolean search(Node node, T value) {

        // base case: ensures invalid values and nodes instantly return false
        if (node == null || value == null) {
            return false;
        }

        int i = 0;
        // Scans through keys from left to right to find where value belongs
        while (i < node.getKeyCount() && value.compareTo(node.getKey(i)) > 0) {
            i++;
        }

        // Compares search value with current key
        if (i < node.getKeyCount() && value.compareTo(node.getKey(i)) == 0) {
            return true;
        }

        // Base case: at leaf, no further to search
        if (node.isLeaf()) {
            return false;
        }

        // Rec call. Searches in the appropriate child subtree
        return search(node.getChild(i), value);
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
    public TreeNode<T> getRoot() {
        // 2-4 tree doesn't use TreeNode interface directly
        // We'll create an adapter for visualization
        return null;
    }

    public Node get24Root() {
        return root;
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
}
