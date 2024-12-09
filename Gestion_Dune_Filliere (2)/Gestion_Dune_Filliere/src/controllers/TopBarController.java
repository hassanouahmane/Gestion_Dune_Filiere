package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TopBarController {

    @FXML
    private Button usernameButton;

    /**
     * Initialisation du contr�leur.
     * R�cup�re et affiche le nom d'utilisateur connect�.
     */
    @FXML
    public void initialize() {
        // R�cup�re le nom d'utilisateur stock� dans la variable statique de Connecter_Controller
        String username = Connecter_Controller.connectedUsername;
        
        if (username != null) {
            usernameButton.setText(username); // Affiche le nom de l'utilisateur
        } else {
            usernameButton.setText("Utilisateur inconnu");
        }
    }
}
