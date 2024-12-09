package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

    private static Main instance; // Singleton pour accéder à Main
    private Stage primaryStage; // Stage principal

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Initialiser l'instance singleton
        this.primaryStage = primaryStage; // Initialiser le Stage principal
        showLoginWindow(); // Afficher l'interface de connexion
    }

 
    public void showWindow(String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

             Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

 
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.setResizable(resizable); 
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
    public void showLoginWindow() {
        showWindow("Connecter.fxml", "Se Connecter", false); // Désactiver le redimensionnement
    }

  
    public void showDashboard() {
        showWindow("Menu.fxml", "Menu Principal", true); // Activer le redimensionnement
    }
 
    public static Main getInstance() {
        return instance; // Retourne l'instance de Main
    }

    public static void main(String[] args) {
        launch(args);
    }
}
