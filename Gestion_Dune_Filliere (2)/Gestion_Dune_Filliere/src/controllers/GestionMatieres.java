package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Models.Matiere;
import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GestionMatieres {
    @FXML
    private TableColumn<Matiere, Integer> id;

    @FXML
    private TableView<Matiere> matiereTable;
    @FXML
    private TableColumn<Matiere, String> NomEnseignant;
    @FXML
    private TableColumn<Matiere, String> NomColumn;
    @FXML
    private TableColumn<Matiere, String> FiliereColumn;

    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;

    private ObservableList<Matiere> matieres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	id.setCellValueFactory(new PropertyValueFactory<>("id"));
        NomColumn.setCellValueFactory(new PropertyValueFactory<>("nom_matiere"));
        NomEnseignant.setCellValueFactory(new PropertyValueFactory<>("proffesseur"));
        FiliereColumn.setCellValueFactory(new PropertyValueFactory<>("filiere"));

        loadDataFromDatabase();
        
        matiereTable.setItems(matieres);
    }

    void loadDataFromDatabase() {
        String sql = "SELECT m.id,m.Nom_Module AS NomModule, CONCAT(e.nom, ' ', e.prenom) AS NomEnseignant, f.Nom_Filiere AS NomFilliere " +
                     "FROM module m " +
                     "JOIN enseignant e ON m.enseignant = e.id " +
                     "JOIN filiere f ON m.FILLIERE = f.id";
        try (Connection connection = ConnexionMysql.ConnexionDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            matieres.clear();
            while (resultSet.next()) {
                matieres.add(new Matiere(
                    resultSet.getInt("id"),
                    resultSet.getString("NomModule"),
                    resultSet.getString("NomEnseignant"),
                    resultSet.getString("NomFilliere")
                ));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    public void refreshTable() {
        matieres.clear();
        loadDataFromDatabase();
        matiereTable.refresh(); // Forcer le rafraîchissement
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/MatiereForm.fxml"));
            Parent root = loader.load();
            
            // Obtenir le contrôleur du formulaire
            MatiereFormController formController = loader.getController();
            
            // Transmettre la liste et la table
            formController.setMatiereList(matieres);
            formController.setParentController(this); // Passer le contrôleur parent

            Stage stage = new Stage();
            stage.setTitle("Ajouter Module");
            stage.setScene(new Scene(root, 472, 542));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        Matiere selectedMatiere = matiereTable.getSelectionModel().getSelectedItem();
        if (selectedMatiere != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/MatiereForm.fxml"));
                Parent root = loader.load();

                MatiereFormController formController = loader.getController();
                formController.setMatiere(selectedMatiere);
                formController.setMatiereList(matieres);
                formController.setParentController(this); // Passer le contrôleur parent

                Scene scene = new Scene(root, 472, 542);
                scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
                Stage stage = new Stage();
                stage.setTitle("Formulaire de modification");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un Module à modifier !");
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Matiere selectedMatiere = matiereTable.getSelectionModel().getSelectedItem();
        if (selectedMatiere != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Supprimer Module");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer le Module " + selectedMatiere.getNom_matiere() + " ?");
            if (confirmationAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
                String sql = "DELETE FROM module WHERE Nom_module = ?";
                try (Connection conn = ConnexionMysql.ConnexionDB();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, selectedMatiere.getNom_matiere());

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        matieres.remove(selectedMatiere);
                        matiereTable.refresh();
                        showAlert("Succès", "Module supprimé avec succès.");
                    } else {
                        showAlert("Erreur", "Aucun Module trouvé avec ce Nom.");
                    }
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression du module : " + e.getMessage());
                }
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un Module à supprimer !");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
