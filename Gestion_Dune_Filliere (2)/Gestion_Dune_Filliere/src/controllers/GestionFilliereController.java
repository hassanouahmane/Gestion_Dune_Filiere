package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Filiere;
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

public class GestionFilliereController {

    @FXML
    private TableView<Filiere> Filieretable;

    @FXML
    private TableColumn<Filiere, Integer> IdColumn;

    @FXML
    private TableColumn<Filiere, Integer> NbrEtudiantColumn;

    @FXML
    private TableColumn<Filiere, String> NomFiliere;

    @FXML
    private TableColumn<Filiere, String> SemestreColumn;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    private ObservableList<Filiere> FiliereList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        NbrEtudiantColumn.setCellValueFactory(new PropertyValueFactory<>("nombreEtudiants"));
        NomFiliere.setCellValueFactory(new PropertyValueFactory<>("nomFiliere"));
        SemestreColumn.setCellValueFactory(new PropertyValueFactory<>("id_semestre"));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        String sql = "SELECT * FROM filiere";
        try (Connection conn = ConnexionMysql.ConnexionDB();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            FiliereList.clear();
            while (rs.next()) {
                FiliereList.add(new Filiere(
                    rs.getInt("id"),
                    rs.getString("Nom_Filiere"),
                    rs.getInt("Nbr_etudiants"),
                    rs.getInt("Semestre")
                ));
            }
            Filieretable.setItems(FiliereList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FormFilliere.fxml"));
            Parent root = loader.load();

            FiliereFormController formController = loader.getController();
            formController.setFiliereList(FiliereList);
            formController.setFiliereTable(Filieretable);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Filière");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        Filiere selectedFiliere = Filieretable.getSelectionModel().getSelectedItem();

        if (selectedFiliere != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FormFilliere.fxml"));
                Parent root = loader.load();

                FiliereFormController formController = loader.getController();
                
                formController.setFiliereList(FiliereList);
                formController.setFiliereTable(Filieretable);
                formController.setCurrentFiliere(selectedFiliere);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Filière");
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une filière à modifier !");
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Filiere selectedFiliere = Filieretable.getSelectionModel().getSelectedItem();

        if (selectedFiliere != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Supprimer Filière");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer la filière " + selectedFiliere.getNomFiliere() + " ?");

            if (confirmationAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
                String sql = "DELETE FROM filiere WHERE id = ?";
                try (Connection conn = ConnexionMysql.ConnexionDB();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, selectedFiliere.getId());

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        FiliereList.remove(selectedFiliere);
                        Filieretable.refresh();
                        showAlert("Succès", "Filière supprimée avec succès.");
                    } else {
                        showAlert("Erreur", "Aucune filière trouvée avec cet ID.");
                    }

                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression de la filière : " + e.getMessage());
                }
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une filière à supprimer !");
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
