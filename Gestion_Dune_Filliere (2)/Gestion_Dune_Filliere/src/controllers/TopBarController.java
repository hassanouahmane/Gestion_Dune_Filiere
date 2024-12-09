package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TopBarController {

    @FXML
    private Button usernameButton;

    /**
     * Initialisation du contrôleur.
     * Récupère et affiche le nom d'utilisateur connecté.
     */
    @FXML
    public void initialize() {
        // Récupère le nom d'utilisateur stocké dans la variable statique de Connecter_Controller
        String username = Connecter_Controller.connectedUsername;
        
        if (username != null) {
            usernameButton.setText(username); // Affiche le nom de l'utilisateur
        } else {
            usernameButton.setText("Utilisateur inconnu");
        }
    }
}
