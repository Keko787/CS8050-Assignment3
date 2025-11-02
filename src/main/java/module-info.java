module org.example.Trees {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;


    opens org.example.Trees to javafx.fxml;
    exports org.example.Trees;
}