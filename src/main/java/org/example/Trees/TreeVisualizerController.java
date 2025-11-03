package org.example.Trees;
//Author: Abdelnasser Ouda
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.List;

public class TreeVisualizerController {
    private VBox view;
    private ComboBox<String> treeTypeComboBox;
    private TextField inputField;
    private Button insertButton, deleteButton, searchButton, clearButton, inorderButton;
    private Canvas treeCanvas;
    private TextArea outputArea;
    private List<Integer> keys = new ArrayList<>();

    private Tree<Integer> currentTree;
    private Map<String, Tree<Integer>> trees;

    private Stage stage;  // need to set this when creating the controller

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TreeVisualizerController() {
        initializeTrees();
        initializeView();
        setupEventHandlers();
    }

    private void initializeTrees() {
        trees = new HashMap<>();
        trees.put("Binary Search Tree", new BinarySearchTree<>());
        trees.put("AVL Tree", new AVLTree<>());
        trees.put("Red-Black Tree", new RedBlackTree<>());
        trees.put("Min Heap", new MinHeap<>());
        trees.put("Max Heap", new MaxHeap<>());
        trees.put("2-4 Tree", new Tree24<>());
        currentTree = trees.get("Binary Search Tree");
    }

    private void initializeView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        treeTypeComboBox = new ComboBox<>();
        treeTypeComboBox.getItems().addAll(trees.keySet());
        treeTypeComboBox.setValue("Binary Search Tree");

        inputField = new TextField();
        insertButton = new Button("Insert");
        deleteButton = new Button("Delete");
        searchButton = new Button("Search");
        clearButton = new Button("Clear");
        inorderButton = new Button("In-Order Traversal");

        HBox buttonBox = new HBox(10, insertButton, deleteButton, searchButton, clearButton, inorderButton);

        treeCanvas = new Canvas(1000, 675);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setMinHeight(100);
        outputArea.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");


        VBox root = new VBox(10);

        view.getChildren().addAll(
                new HBox(10, new Label("Tree Type:"), treeTypeComboBox),
                new HBox(10, new Label("Value:"), inputField),
                buttonBox,
                treeCanvas,
                outputArea
        );
    }

    private void setupEventHandlers() {
        insertButton.setOnAction(e -> handleInsert());
        deleteButton.setOnAction(e -> handleDelete());
        searchButton.setOnAction(e -> handleSearch());
        clearButton.setOnAction(e -> handleClear());
        inorderButton.setOnAction(e -> handleInorderTraversal());
        treeTypeComboBox.setOnAction(e -> handleTreeTypeChange());
    }

    private void handleInsert() {
        try {
            int value = Integer.parseInt(inputField.getText());
            if (!currentTree.contains(value)) {
                System.out.println("inside handleInsert and going to call currentTree.insert("+value+")");
                currentTree.insert(value);
                updateTreeVisualization();
                outputArea.appendText("Inserted: " + value + "\n");
            } else
                outputArea.appendText("The value " + value + " already in the tree.\n");

        } catch (NumberFormatException ex) {
            outputArea.appendText("Invalid input. Please enter an integer.\n");
        }
    }

    private void handleDelete() {
        try {
            int value = Integer.parseInt(inputField.getText());
            boolean deleted = currentTree.delete(value);
            updateTreeVisualization();
            outputArea.appendText(deleted ? "Deleted: " + value + "\n" : "Value not found: " + value + "\n");
        } catch (NumberFormatException ex) {
            outputArea.appendText("Invalid input. Please enter an integer.\n");
        }
    }

    private void handleSearch() {
        try {
            int value = Integer.parseInt(inputField.getText());
            boolean found = currentTree.contains(value);
            outputArea.appendText(found ? "Found: " + value + "\n" : "Not found: " + value + "\n");
        } catch (NumberFormatException ex) {
            outputArea.appendText("Invalid input. Please enter an integer.\n");
        }
    }

    private void handleClear() {
        currentTree.clear();
        updateTreeVisualization();
        outputArea.appendText("Tree cleared.\n");
    }

    private void handleInorderTraversal() {
        List<Integer> traversal = currentTree.inorderTraversal();
        if (traversal.isEmpty()) {
            outputArea.appendText("Tree is empty.\n");
        } else {
            outputArea.appendText("In-order traversal: " + traversal + "\n");
        }
    }

    private void handleTreeTypeChange() {
        String selectedType = treeTypeComboBox.getValue();
        currentTree = trees.get(selectedType);
        updateTreeVisualization();
        outputArea.appendText("Switched to " + selectedType + "\n");
    }

    private void updateTreeVisualization() {
        GraphicsContext gc = treeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, treeCanvas.getWidth(), treeCanvas.getHeight());

        // Check if this is a 2-4 tree
        if (currentTree instanceof Tree24) {
            Tree24<Integer> tree24 = (Tree24<Integer>) currentTree;
            if (tree24.get24Root() != null) {
                int depth = get24TreeDepth(tree24.get24Root());
                double verticalSpacing = (treeCanvas.getHeight() - 80) / Math.max(depth, 1);
                draw24Tree(gc, tree24.get24Root(), treeCanvas.getWidth() / 2, 40, treeCanvas.getWidth() / 2, verticalSpacing);
            } else {
                outputArea.appendText("Tree is empty.\n");
            }
        } else if (currentTree.getRoot() != null) {
            int depth = getTreeDepth(currentTree.getRoot());
            int width = getTreeWidth(currentTree.getRoot());

            double verticalSpacing = (treeCanvas.getHeight()) / (depth + currentTree.size() / 3);
            double horizontalSpacing = treeCanvas.getWidth() / (width + currentTree.size() / 2);

            drawTree(gc, currentTree.getRoot(), treeCanvas.getWidth() / 2, 40, horizontalSpacing, verticalSpacing, width);
        } else {
            outputArea.appendText("Tree is empty or null.\n");
        }
    }

    private void drawTree(GraphicsContext gc, TreeNode<Integer> node, double x, double y, double hSpacing, double vSpacing, int width) {
        if (node == null) return;

       // if you implement 24Tree, you need to create draw24Tree and all other needed methods
       drawNormalTree(gc, currentTree.getRoot(), treeCanvas.getWidth() / 2, 40, hSpacing, vSpacing, width);

    }

    private void drawNormalTree(GraphicsContext gc, TreeNode<Integer> node, double x, double y, double hSpacing, double vSpacing, int width) {
        if ("RBT".equals(currentTree.type())) {
            if ("RED".equals(node.getColor())) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.BLACK);
            }
        } else {
            gc.setFill(currentTree.color());
        }

        gc.fillOval(x - 15, y - 15, 40, 40);
        gc.setFill(Color.GHOSTWHITE);
        gc.fillText(node.getValue().toString(), x - 10, y + 10);

        // Draw left subtree
        if (node.getLeft() != null) {
            int leftWidth = getTreeWidth(node.getLeft());
            double newX = x - (width - leftWidth / 3) * hSpacing / 3;
            double newY = y + vSpacing;
            gc.strokeLine(x + 5, y + 24, newX, newY);
            drawNormalTree(gc, node.getLeft(), newX, newY, hSpacing, vSpacing, leftWidth);
        }

        // Draw right subtree
        if (node.getRight() != null) {
            int rightWidth = getTreeWidth(node.getRight());
            double newX = x + (width - rightWidth / 3) * hSpacing / 3;
            double newY = y + vSpacing;
            gc.strokeLine(x + 5, y + 24, newX, newY);
            drawNormalTree(gc, node.getRight(), newX, newY, hSpacing, vSpacing, rightWidth);
        }
    }

    private List<TreeNode<Integer>> getNodeChildren(TreeNode<Integer> node) {
        List<TreeNode<Integer>> children = new ArrayList<>();
        if (node.getLeft() != null) children.add(node.getLeft());
        if (node.getRight() != null) children.add(node.getRight());
        // Add logic here to get additional children if they exist
        return children;
    }

    private int getTreeDepth(TreeNode<Integer> node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeDepth(node.getLeft()), getTreeDepth(node.getRight()));
    }

    private int getTreeWidth(TreeNode<Integer> node) {
        if (node == null) return 0;
        if (node.getLeft() == null && node.getRight() == null) return 1;
        return getTreeWidth(node.getLeft()) + getTreeWidth(node.getRight());
    }

    public VBox getView() {
        return view;
    }

    //
    // Save and Load Methods
    //

    public void saveTree() {
        // This is a placeholder implementation. In a real application, you would
        // implement serialization of the tree structure to a file.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Tree");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tree Files", "*.tree")
        );
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                outputArea.appendText("Tree before saving" + currentTree.inorderTraversal() + ".\n");
                out.writeObject(currentTree);
                outputArea.appendText("Tree saved successfully.\n");
            } catch (IOException e) {
                outputArea.appendText("Error saving tree: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    public void loadTree() {
        // This is a placeholder implementation. In a real application, you would
        // implement deserialization of the tree structure from a file.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Tree");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tree Files", "*.tree")
        );
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Object loadedObject = in.readObject();

                if (!(loadedObject instanceof Tree)) {
                    outputArea.appendText("Error: Loaded object is not a valid Tree.\n");
                    return;
                }

                @SuppressWarnings("unchecked")
                Tree<Integer> loadedTree = (Tree<Integer>) loadedObject;

                // Determine the type of the loaded tree and update the UI
                String treeType = determineTreeType(loadedTree);
                if (treeType == null) {
                    outputArea.appendText("Error: Unknown tree type.\n");
                    return;
                }

                treeTypeComboBox.setValue(treeType);
                currentTree = loadedTree;

                // Debug information
                outputArea.appendText("Tree loaded successfully.\n");
                outputArea.appendText("Tree type: " + currentTree.type() + "\n");
                outputArea.appendText("Tree size: " + currentTree.size() + "\n");
                outputArea.appendText("Tree contents: " + currentTree.inorderTraversal() + "\n");

                updateTreeVisualization();
            } catch (IOException | ClassNotFoundException e) {
                outputArea.appendText("Error loading tree: " + e.getMessage() + "\n");
                e.printStackTrace(); // Print stack trace for debugging
            }
        }
    }

    private String determineTreeType(Tree<?> tree) {
        return currentTree.type();
    }

    //
    // 2-4 Tree visualization methods
    //

    private void draw24Tree(GraphicsContext gc, Object node, double x, double y, double hSpacing, double vSpacing) {
        if (node == null) return;

        // Use reflection to access the inner Node class methods
        try {
            java.lang.reflect.Method getKeyCountMethod = node.getClass().getDeclaredMethod("getKeyCount");
            getKeyCountMethod.setAccessible(true);
            int keyCount = (int) getKeyCountMethod.invoke(node);

            double nodeWidth = 40 + (keyCount - 1) * 30;
            double nodeHeight = 30;

            // Draw node rectangle
            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            gc.fillRoundRect(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight, 10, 10);
            gc.strokeRoundRect(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight, 10, 10);

            // Draw keys
            gc.setFill(Color.BLACK);
            gc.setFont(new javafx.scene.text.Font(14));
            java.lang.reflect.Method getKeyMethod = node.getClass().getDeclaredMethod("getKey", int.class);
            getKeyMethod.setAccessible(true);

            for (int i = 0; i < keyCount; i++) {
                Integer key = (Integer) getKeyMethod.invoke(node, i);
                String text = key.toString();
                double textX = x - nodeWidth / 2 + 15 + i * 30;
                double textY = y + 5;
                gc.fillText(text, textX, textY);

                // Draw separators between keys
                if (i < keyCount - 1) {
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(1);
                    double sepX = x - nodeWidth / 2 + 30 + i * 30;
                    gc.strokeLine(sepX, y - nodeHeight / 2, sepX, y + nodeHeight / 2);
                }
            }

            // Draw children
            java.lang.reflect.Method isLeafMethod = node.getClass().getDeclaredMethod("isLeaf");
            isLeafMethod.setAccessible(true);
            boolean isLeaf = (boolean) isLeafMethod.invoke(node);

            if (!isLeaf) {
                java.lang.reflect.Method getChildCountMethod = node.getClass().getDeclaredMethod("getChildCount");
                getChildCountMethod.setAccessible(true);
                int childCount = (int) getChildCountMethod.invoke(node);

                java.lang.reflect.Method getChildMethod = node.getClass().getDeclaredMethod("getChild", int.class);
                getChildMethod.setAccessible(true);

                double childSpacing = hSpacing / (childCount + 1);

                for (int i = 0; i < childCount; i++) {
                    Object child = getChildMethod.invoke(node, i);
                    if (child != null) {
                        // Calculate child position
                        double childX = x - hSpacing / 2 + (i + 1) * childSpacing;
                        double childY = y + vSpacing;

                        // Draw line to child
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(1);
                        gc.strokeLine(x, y + nodeHeight / 2, childX, childY - 15);

                        // Recursively draw child
                        draw24Tree(gc, child, childX, childY, hSpacing / 2, vSpacing);
                    }
                }
            }
        } catch (Exception e) {
            outputArea.appendText("Error drawing 2-4 tree: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private int get24TreeDepth(Object node) {
        if (node == null) return 0;

        try {
            java.lang.reflect.Method isLeafMethod = node.getClass().getDeclaredMethod("isLeaf");
            isLeafMethod.setAccessible(true);
            boolean isLeaf = (boolean) isLeafMethod.invoke(node);
            if (isLeaf) return 1;

            int maxChildDepth = 0;
            java.lang.reflect.Method getChildCountMethod = node.getClass().getDeclaredMethod("getChildCount");
            getChildCountMethod.setAccessible(true);
            int childCount = (int) getChildCountMethod.invoke(node);

            java.lang.reflect.Method getChildMethod = node.getClass().getDeclaredMethod("getChild", int.class);
            getChildMethod.setAccessible(true);

            for (int i = 0; i < childCount; i++) {
                Object child = getChildMethod.invoke(node, i);
                maxChildDepth = Math.max(maxChildDepth, get24TreeDepth(child));
            }
            return 1 + maxChildDepth;
        } catch (Exception e) {
            return 0;
        }
    }
}
