module event.manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires atlantafx.base;
    
    // Add these two lines to fix the "not visible" errors:
    requires java.sql;
    requires java.logging;

    opens ui to javafx.fxml;
    exports ui;
    exports model;
    exports service;
    // exports controller;
    
    // If your FXML files need to access your controllers in other packages, 
    // you might need to 'opens' those too, but start with this.
}