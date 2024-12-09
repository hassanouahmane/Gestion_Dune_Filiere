module Gestion_Dune_Filliere {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.sql;
	requires itextpdf;
 	
    opens Models to javafx.base;
	opens application to javafx.graphics, javafx.fxml;
    opens controllers to javafx.fxml,javafx.graphics,javafx.base;
    
    exports application;

    exports Models; 
    exports controllers;

}
