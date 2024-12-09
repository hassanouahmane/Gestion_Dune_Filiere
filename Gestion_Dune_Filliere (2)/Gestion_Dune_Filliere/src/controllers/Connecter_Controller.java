package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.ConnexionMysql;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Connecter_Controller {

    @FXML
    private TextField emailField;  
    @FXML
    private TextField passwordField;  
    @FXML
    private Button connect;  

    // Variable statique pour stocker le nom d'utilisateur
    public static String connectedUsername = null;

    @FXML
    public void initialize() {
        setupListeners();
    }

    private void setupListeners() {
        connect.setOnAction(event -> connecter());
    }

    @FXML
    void connecter() {
        if (!validateInputs()) {
            return;
        }

        if (authenticateUser()) {
            redirectToDashboard();
        }
    }

    private boolean validateInputs() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean authenticateUser() {
        String email = emailField.getText();
        String password = passwordField.getText();

        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection == null) {
            showAlert("Erreur", "Connexion à la base de données échouée.", AlertType.ERROR);
            return false;
        }

        try {
            String query = "SELECT * FROM utilisateur WHERE EMAIL = ? AND PASSWORD = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Connexion réussie, récupère le nom d'utilisateur
                String nom = resultSet.getString("Nom");
                String prenom = resultSet.getString("PRENOM");

                connectedUsername = prenom + " " + nom;  

                return true;
            } else {
                showAlert("Erreur", "Email ou mot de passe incorrect.", AlertType.ERROR);
                return false;
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la connexion : " + e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    private void redirectToDashboard() {
        Main.getInstance().showDashboard();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
