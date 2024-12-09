package controllers;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Ensignant;
import Models.Etudiant;
import Models.Matiere;
import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class EtudiantFormController {
	 
    @FXML
    private Text fname2;

    @FXML
    private TextField nomField;

    @FXML
    private Text lname2;

    @FXML
    private Text phone2;

    @FXML
    private TextField cneField;

    @FXML
    private Text email2;
    
    private ObservableList<String> filieres = FXCollections.observableArrayList();


    @FXML
    private ComboBox<String> filiereCombox;
 
    @FXML
    private Button savebtn;

    @FXML
    private TextField prenomField;
    @FXML
    private TableView<Etudiant> etudiantTable;

    private ObservableList<Etudiant> etudiantList;
    private Etudiant currentEtudiant;

    private GestionetudiantController parentController;

    public void setParentController(GestionetudiantController parentController) {
        this.parentController = parentController;
    }
    
    @FXML
    public void initialize() {
        loadComboBoxData();
    }
    
    public void loadComboBoxData() {
        try (Connection connection = ConnexionMysql.ConnexionDB()) {
            // Charger les filières
            String filiereSql = "SELECT Nom_Filiere FROM filiere";
            ResultSet filiereResult = connection.createStatement().executeQuery(filiereSql);
            while (filiereResult.next()) {
                filieres.add(filiereResult.getString("Nom_Filiere"));
            }
            filiereCombox.setItems(filieres);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

     @FXML
    void save(ActionEvent event) {
         String nom = nomField.getText();
         String prenom = prenomField.getText();
         String cne = cneField.getText();
         String filiere = filiereCombox.getSelectionModel().getSelectedItem();

         
         if (nom.isEmpty() || prenom.isEmpty() || cne.isEmpty()|| filiere == null) {
             showAlert("Erreur", "Tous les champs doivent être remplis !");
             return;
         }
         
         try (Connection connection = ConnexionMysql.ConnexionDB()) {
             // Récupérer les IDs pour la filière et l'enseignant
             int filiereId = getIdFromName(connection, "filiere", "Nom_Filiere", filiere);
 
             if (currentEtudiant == null) {
                 // Ajouter un nouveau module
                 String sql = "INSERT INTO etudiant (cne, nom, prenom,FILLIERE) VALUES (?, ?, ?, ?)";
                 try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                     stmt.setString(1, cne);
                     stmt.setString(2, nom);
                     stmt.setString(3, prenom);
                     stmt.setInt(4, filiereId);
                     stmt.executeUpdate();
                 }
                 showAlert("Succès", "Etudiant ajouté avec succès !");
             } else {
                 // Modifier un module existant
                 String sql = "UPDATE etudiant SET cne = ?, nom = ?, FILLIERE = ? , prenom = ?  WHERE id = ?";
                 try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                     stmt.setString(1, cne);
                	 stmt.setString(2, nom);
                     stmt.setInt(3, filiereId);
                     stmt.setString(4, prenom);
                      stmt.setInt(5, currentEtudiant.getId());
                     stmt.executeUpdate();
                 }
                 showAlert("Succès", "Etudiant modifié avec succès !");
             }

             parentController.refreshTable();


         } catch (SQLException e) {
             showAlert("Erreur", "Erreur lors de la sauvegarde du module : " + e.getMessage());
         }
     
        
    }

     public void setEtudiant(Etudiant etudiant) {
         this.currentEtudiant = etudiant;
         nomField.setText(etudiant.getNom());
         prenomField.setText(etudiant.getPrenom());
         filiereCombox.setValue(etudiant.getFiliere());
         cneField.setText(etudiant.getCne());
     }

     private int getIdFromName(Connection connection, String table, String nameColumn, String name) throws SQLException {
         String sql = "SELECT id FROM " + table + " WHERE " + nameColumn + " = ?";
         try (PreparedStatement stmt = connection.prepareStatement(sql)) {
             stmt.setString(1, name);
             ResultSet resultSet = stmt.executeQuery();
             if (resultSet.next()) {
                 return resultSet.getInt("id");
             } else {
                 throw new SQLException("Aucun ID trouvé pour " + name);
             }
         }
     }
 
    public void setEtudiantList(ObservableList<Etudiant> etudiantList) {
        this.etudiantList = etudiantList;
    }
    public void setEtudiantTable(TableView<Etudiant> etudiantTable) {
        this.etudiantTable = etudiantTable;
    }
    
 
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
