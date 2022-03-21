module org.mth.jfxemptyfolderscleaner {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.logging;

    opens org.mth.jfxemptyfolderscleaner to javafx.fxml;
    exports org.mth.jfxemptyfolderscleaner;
}