module de.philx.catan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens de.philx.catan to javafx.fxml;
    exports de.philx.catan;
    exports de.philx.catan.GamePieces;
    opens de.philx.catan.GamePieces to javafx.fxml;
    exports de.philx.catan.Cards.Developments;
    exports de.philx.catan.Cards.Resources;
    exports de.philx.catan.Players;
    opens de.philx.catan.Players to javafx.fxml;
    opens de.philx.catan.Cards.Resources to javafx.fxml;
    exports de.philx.catan.GameField;
    opens de.philx.catan.GameField to javafx.fxml;
    exports de.philx.catan.Screens;
    opens de.philx.catan.Screens to javafx.fxml;
}