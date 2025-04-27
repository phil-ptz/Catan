module de.philx.catan {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.philx.catan to javafx.fxml;
    exports de.philx.catan;
}