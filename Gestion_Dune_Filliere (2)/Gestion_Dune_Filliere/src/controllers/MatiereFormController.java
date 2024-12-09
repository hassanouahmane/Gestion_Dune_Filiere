package controllers;

import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Matiere;

public class MatiereFormController {

    @FXML
    private TextField moduletxtField;
    @FXML
    private ComboBox<String> filiereComboBox;
    @FXML
    private ComboBox<String> enseignantComboBox;

    @FXML
    private Button saveButton;
    
    private ObservableList<String> filieres = FXCollections.observableArrayList();
    private ObservableList<String> enseignants = FXCollections.observableArrayList();

    private ObservableList<Matiere> matieres;
    private TableView<Matiere> matiereTable;
    private Matiere matiere;
    
    private GestionMatieres parentController;

    public void setParentController(GestionMatieres parentController) {
        this.parentController = parentController;
    }


    @FXML
    public void initialize() {
        loadComboBoxData();
    }

    public void loadComboBoxData() {
        try (Connection connection = ConnexionMysql.ConnexionDB()) {
            // Charger les fili�res
            String filiereSql = "SELECT Nom_Filiere FROM filiere";
            ResultSet filiereResult = connection.createStatement().executeQuery(filiereSql);
            while (filiereResult.next()) {
                filieres.add(filiereResult.getString("Nom_Filiere"));
            }
            filiereComboBox.setItems(filieres);

            // Charger les enseignants
            String enseignantSql = "SELECT CONCAT(nom, ' ', prenom) AS NomEnseignant FROM enseignant";
            ResultSet enseignantResult = connection.createStatement().executeQuery(enseignantSql);
            while (enseignantResult.next()) {
                enseignants.add(enseignantResult.getString("NomEnseignant"));
            }
            enseignantComboBox.setItems(enseignants);

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des donn�es : " + e.getMessage());
        }
    }

    @FXML
    public void saveMatiere() {
        String nomModule = moduletxtField.getText();
        String filiere = filiereComboBox.getSelectionModel().getSelectedItem();
        String enseignant = enseignantComboBox.getSelectionModel().getSelectedItem();

        if (nomModule.isEmpty() || filiere == null || enseignant == null) {
            showAlert("Erreur", "Tous les champs doivent �tre remplis !");
            return;
        }

        try (Connection connection = ConnexionMysql.ConnexionDB()) {
            // R�cup�rer les IDs pour la fili�re et l'enseignant
            int filiereId = getIdFromName(connection, "filiere", "Nom_Filiere", filiere);
            int enseignantId = getIdFromName(connection, "enseignant", "CONCAT(nom, ' ', prenom)", enseignant);

            if (matiere == null) {
                // Ajouter un nouveau module
                String sql = "INSERT INTO module (Nom_Module, FILLIERE, enseignant) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, nomModule);
                    stmt.setInt(2, filiereId);
                    stmt.setInt(3, enseignantId);
                    stmt.executeUpdate();
                }
                showAlert("Succ�s", "Module ajout� avec succ�s !");
            } else {
                // Modifier un module existant
                String sql = "UPDATE module SET Nom_Module = ?, FILLIERE = ?, enseignant = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, nomModule);
                    stmt.setInt(2, filiereId);
                    stmt.setInt(3, enseignantId);
                    stmt.setInt(4, matiere.getId());
                    stmt.executeUpdate();
                }
                showAlert("Succ�s", "Module modifi� avec succ�s !");
            }

            parentController.refreshTable();


        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la sauvegarde du module : " + e.getMessage());
        }
    }

    private int getIdFromName(Connection connection, String table, String nameColumn, String name) throws SQLException {
        String sql = "SELECT id FROM " + table + " WHERE " + nameColumn + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Aucun ID trouv� pour " + name);
            }
        }
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
        moduletxtField.setText(matiere.getNom_matiere());
        filiereComboBox.setValue(matiere.getFiliere());
        enseignantComboBox.setValue(matiere.getProffesseur());
    }

    public void setMatiereList(ObservableList<Matiere> matieres) {
        this.matieres = matieres;
    }

    public void setMatiereTable(TableView<Matiere> matiereTable) {
        this.matiereTable = matiereTable;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
