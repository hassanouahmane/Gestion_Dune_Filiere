package controllers;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Models.Etudiant;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GestionetudiantController {
 

    @FXML
    private TableColumn<Etudiant, Integer> id;
    
    @FXML
    private TableView<Etudiant> etudiantTable;
    @FXML
    private TableColumn<Etudiant, String> CneColumn;
    @FXML
    private TableColumn<Etudiant, String> NomColumn;
    @FXML
    private TableColumn<Etudiant, String> PrenomColumn;
    @FXML
    private TableColumn<Etudiant, String> FiliereColumn;

    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;

    @FXML
    private Button exporterPDFBtn;

    private ObservableList<Etudiant> etudiantList = FXCollections.observableArrayList();

 

    @FXML
    public void initialize() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        CneColumn.setCellValueFactory(new PropertyValueFactory<>("cne"));
        NomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        PrenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        FiliereColumn.setCellValueFactory(new PropertyValueFactory<>("filiere"));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
    	String sql = "SELECT e.id,e.cne, e.nom, e.prenom, f.Nom_Filiere AS NomFilliere " +
                "FROM etudiant e " +
                "JOIN filiere f ON e.FILLIERE = f.id";

        Connection connection = ConnexionMysql.ConnexionDB();

        try (Connection conn = connection;
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                etudiantList.add(new Etudiant(
                    rs.getInt("id"),
                    rs.getString("cne"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("NomFilliere")
                ));
            }

            etudiantTable.setItems(etudiantList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void refreshTable() {
    	etudiantList.clear();
        loadDataFromDatabase();
        etudiantTable.refresh(); // Forcer le rafraîchissement
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/EtudiantForm.fxml"));
            Parent root = loader.load();
            EtudiantFormController formController = loader.getController();
            formController.setEtudiantList(etudiantList);
            formController.setParentController(this); // Passer le contrôleur parent

            Scene scene = new Scene(root, 472, 542);

            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Ajouter Etudiant");
            stage.setScene(scene);
            stage.show();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        Etudiant selectedEtudiant = etudiantTable.getSelectionModel().getSelectedItem();

        if (selectedEtudiant != null) {

                 try {
                     FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/EtudiantForm.fxml"));
                     Parent root = loader.load();

                     EtudiantFormController formController = loader.getController();
                     formController.setEtudiant(selectedEtudiant); 
                     formController.setEtudiantList(etudiantList);
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
                 showAlert("Erreur", "Veuillez sélectionner un etudiant à modifier !");
             }
        }
        
      

    @FXML
    void supprimer(ActionEvent event) {
        Etudiant selectedEtudiant = etudiantTable.getSelectionModel().getSelectedItem();
      
        if (selectedEtudiant != null) {
        	  Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
              confirmationAlert.setTitle("Confirmation de suppression");
              confirmationAlert.setHeaderText("Supprimer Enseignant");
              confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'enseignant " + selectedEtudiant.getNom() + " ?");
              
              if (confirmationAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            String sql = "DELETE FROM etudiant WHERE id = ?";
            Connection connection = ConnexionMysql.ConnexionDB();

            try (Connection conn = connection;
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, selectedEtudiant.getId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    etudiantList.remove(selectedEtudiant);
                    etudiantTable.refresh();

                    showAlert("Succès", "Etudiant supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Aucun étudiant trouvé avec ce CNE.");
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression de l'étudiant : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un étudiant à supprimer !");
        }
    }}
    @FXML
    void exporterPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        java.io.File file = fileChooser.showSaveDialog(etudiantTable.getScene().getWindow());

        if (file != null) {
            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                try {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance("C:\\Users\\huawei\\OneDrive\\Desktop\\logo.png");
                    logo.scaleToFit(200, 200);
                    logo.setAlignment(com.itextpdf.text.Image.ALIGN_RIGHT);
                    document.add(logo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                document.add(new Paragraph("Liste des Étudiants"));
                document.add(new Paragraph(" ")); 

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);

                table.addCell("CNE");
                table.addCell("Nom");
                table.addCell("Prénom");
                table.addCell("Filière");

                for (Etudiant etudiant : etudiantList) {
                    table.addCell(etudiant.getCne());
                    table.addCell(etudiant.getNom());
                    table.addCell(etudiant.getPrenom());
                    table.addCell(etudiant.getFiliere());
                }

                document.add(table);

                document.close();

                showAlert("Succès", "Les étudiants ont été exportés dans un fichier PDF avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'exportation des étudiants dans le fichier PDF : " + e.getMessage());
            }
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
