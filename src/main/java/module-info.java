module com.example.deepspaceimager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.deepspaceimager to javafx.fxml;
    exports com.example.deepspaceimager;
}