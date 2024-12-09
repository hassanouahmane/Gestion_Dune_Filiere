package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.ConnexionMysql;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ProfileController {

    @FXML
    private Text textCne;

    @FXML
    private Text textNom;

    @FXML
    private Text textPrenom;

    @FXML
    private Text textEmail;

    @FXML
    private Text textTelephone;

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldPrenom;

    @FXML
    private TextField fieldEmail;

    @FXML
    private TextField fieldTelephone;

    @FXML
    private TextField fieldPassword; // Champ pour mot de passe existant

    @FXML
    private TextField fieldConfirmPassword; // Champ pour la confirmation du nouveau mot de passe

    @FXML
    private Button btnEnregistrer;

    private String existingPassword; // Stocke le mot de passe actuel de la base de données

    /**
     * Méthode appelée à l'initialisation du contrôleur.
     */
    @FXML
    public void initialize() {
        // Charger les données utilisateur au démarrage
        loadUserData();

        // Configurer l'action pour le bouton "Enregistrer"
        btnEnregistrer.setOnAction(event -> saveUserData());
    }

    /**
     * Charge les données utilisateur depuis la base de données.
     */
    private void loadUserData() {
        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection != null) {
            try {
                String query = "SELECT cne, Nom, PRENOM, EMAIL, telephone, password FROM utilisateur WHERE cne = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, "D1234567"); // Remplacez par une méthode pour récupérer le CNE utilisateur actuel
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Charger les données dans les labels
                    textCne.setText(resultSet.getString("cne"));
                    textNom.setText(resultSet.getString("Nom"));
                    textPrenom.setText(resultSet.getString("PRENOM"));
                    textEmail.setText(resultSet.getString("EMAIL"));
                    textTelephone.setText(resultSet.getString("telephone"));

                    // Pré-remplir les champs de texte pour modification
                    fieldNom.setText(resultSet.getString("Nom"));
                    fieldPrenom.setText(resultSet.getString("PRENOM"));
                    fieldEmail.setText(resultSet.getString("EMAIL"));
                    fieldTelephone.setText(resultSet.getString("telephone"));

                    // Stocker le mot de passe existant
                    existingPassword = resultSet.getString("password");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données utilisateur : " + e.getMessage());
            }
        } else {
            System.err.println("Connexion à la base de données échouée.");
        }
    }

    /**
     * Sauvegarde les données modifiées dans la base de données.
     */
    private void saveUserData() {
        String currentPassword = fieldPassword.getText(); // Mot de passe saisi par l'utilisateur
        String newPassword = fieldConfirmPassword.getText(); // Nouveau mot de passe saisi (facultatif)

        if (fieldPassword.getText() == null || fieldPassword.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir le mot de passe existant pour effectuer les modifications.", AlertType.WARNING);
            return;
        }
        // Vérification du mot de passe existant
        if (currentPassword == null || !currentPassword.equals(existingPassword)) {
            showAlert("Erreur", "Le mot de passe existant est incorrect.", AlertType.ERROR);
            return;
        }

        // Vérifier si un nouveau mot de passe est saisi
        if ( !newPassword.equals(currentPassword)) {
            // Nouveau mot de passe doit être confirmé correctement
            if (!newPassword.equals(fieldConfirmPassword.getText())) {
                showAlert("Erreur", "Les mots de passe ne correspondent pas.", AlertType.ERROR);
                return;
            }
        } else {
            // Si pas de nouveau mot de passe saisi, conserver l'ancien
            newPassword = existingPassword;
        }

        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection != null) {
            try {
                String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, password = ? WHERE cne = ?";
                PreparedStatement statement = connection.prepareStatement(query);

                // Récupérer les valeurs des champs de texte
                statement.setString(1, fieldNom.getText());
                statement.setString(2, fieldPrenom.getText());
                statement.setString(3, fieldEmail.getText());
                statement.setString(4, fieldTelephone.getText());
                statement.setString(5, newPassword); // Mot de passe (modifié ou inchangé)
                statement.setString(6, textCne.getText()); // Utiliser le CNE comme identifiant unique

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert("Succès", "Les informations de l'utilisateur ont été mises à jour avec succès.", AlertType.INFORMATION);

                    // Recharger les données pour actualiser les labels
                    loadUserData();
                } else {
                    showAlert("Erreur", "Aucune ligne n'a été mise à jour.", AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la mise à jour des données utilisateur : " + e.getMessage(), AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Connexion à la base de données échouée.", AlertType.ERROR);
        }
    }

    /**
     * Affiche une alerte.
     *
     * @param title   Titre de l'alerte
     * @param message Message de l'alerte
     * @param type    Type d'alerte
     */
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
