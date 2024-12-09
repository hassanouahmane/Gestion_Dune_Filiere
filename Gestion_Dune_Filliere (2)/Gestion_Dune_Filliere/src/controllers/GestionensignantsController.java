package controllers;

import java.io.FileOutputStream;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Models.Ensignant;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GestionensignantsController {
	
 

    @FXML
    private TableColumn<Ensignant, Integer> id;
    @FXML
    private TableView<Ensignant> ensignantTable;
    @FXML
    private TableColumn<Ensignant,String> CneColumn;
    @FXML
    private TableColumn<Ensignant,String> NomColumn;
    @FXML
    private TableColumn<Ensignant,String> PrenomColumn;
    @FXML
    private TableColumn<Ensignant,String> MatiereColumn;
    @FXML
    private TableColumn<Ensignant,String> FiliereColumn;

    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private Button exporterpdfbtn;

    @FXML
    private TextField cneField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField matiereField;
    @FXML
    private TextField filiereField;
    private ObservableList<Ensignant> ensignantList = FXCollections.observableArrayList();
 

 
    @FXML
    public void initialize() {
         CneColumn.setCellValueFactory(new PropertyValueFactory<>("cne"));
        NomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        PrenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
 

        loadDataFromDatabase();
        ensignantTable.setItems(ensignantList);

    }
    
    private void loadDataFromDatabase() {
        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM enseignant";
                ResultSet resultSet = statement.executeQuery(query);
                ensignantList.clear();
                while (resultSet.next()) {
                	ensignantList.add(new Ensignant(
                         resultSet.getInt("id"),
                        resultSet.getString("cne"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom")
                     ));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données : " + e.getMessage());
            }
        } else {
            System.err.println("Connexion à la base de données échouée.");
        }
    }
    
    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/EnsignantForm.fxml"));
            Parent root = loader.load();
            EnsignantFormController formController = loader.getController();
            formController.setEnsignantList(ensignantList);
            formController.setEnsignantTable(ensignantTable);

            Scene scene = new Scene(root, 472, 542);

            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Ajouter Enseignant");
            stage.setScene(scene);
            stage.show();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void modifier(ActionEvent event) {
        Ensignant selectedEnsignant = ensignantTable.getSelectionModel().getSelectedItem();

        if (selectedEnsignant != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/EnsignantForm.fxml"));
                Parent root = loader.load();

                EnsignantFormController formController = loader.getController();
                formController.setEnsignant(selectedEnsignant); 
                formController.setEnsignantList(ensignantList);
                formController.setEnsignantTable(ensignantTable);

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
            showAlert("Erreur", "Veuillez sélectionner un enseignant à modifier !");
        }
    }



    @FXML
    void supprimer(ActionEvent event) {
        Ensignant selectedEnsignant = ensignantTable.getSelectionModel().getSelectedItem();
        Connection connection = ConnexionMysql.ConnexionDB();

        if (selectedEnsignant != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Supprimer Enseignant");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'enseignant " + selectedEnsignant.getNom() + " ?");
            
            if (confirmationAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {

            String sql = "DELETE FROM enseignant WHERE  id = ?";

            try (Connection conn = connection;
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, selectedEnsignant.getId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ensignantList.remove(selectedEnsignant);
                    ensignantTable.refresh();

                    showAlert("Succès", "Enseignant supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Aucun enseignant trouvé avec ce CNE.");
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression de l'enseignant : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un enseignant à supprimer !");
        }
        }
    }
    
    @FXML
    void esporterpdf(ActionEvent event) {
        if (ensignantList.isEmpty()) {
            showAlert("Erreur", "La liste des ensignants est vide. Rien à exporter !");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        java.io.File file = fileChooser.showSaveDialog(ensignantTable.getScene().getWindow());

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

                document.add(new Paragraph("Liste des enseigants", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD)));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);

                table.addCell("CNE");
                table.addCell("Nom");
                table.addCell("Prénom");
                

                for (Ensignant ensignant : ensignantList) {
                    table.addCell(ensignant.getCne());
                    table.addCell(ensignant.getNom());
                    table.addCell(ensignant.getPrenom());
                }

                document.add(table);
                document.close();

                showAlert("Succès", "Les ensignants ont été exportés dans un fichier PDF avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'exportation des ensignants dans le fichier PDF : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Aucun fichier sélectionné pour l'exportation.");
        }
    }



    private void clearFields() {
        cneField.clear();
        nomField.clear();
        prenomField.clear();
 
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
