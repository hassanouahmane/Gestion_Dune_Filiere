package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Models.Ensignant;
import application.ConnexionMysql;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class EnsignantFormController {
	 
	    private ObservableList<Ensignant> ensignantList; 
	    private TableView<Ensignant> ensignantTable; 
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

    @FXML
    private TextField filiere;

    @FXML
    private Button savebtn;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField matiere;
    private Ensignant currentEnsignant;
    @FXML
    void save(ActionEvent event) {
        String cne = cneField.getText();
        String nom = nomField.getText();
        String prenom = prenomField.getText();

        if (areFieldsEmpty()) {
            showErrorAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        if (currentEnsignant == null) {
        	ajouterEnseignant(cne, nom, prenom);
        } else {
        	modifierEnseignant(currentEnsignant.getId(), cne, nom, prenom);
        }  
    }
    private void ajouterEnseignant(String cne, String nom, String prenom) {
            Connection connection = ConnexionMysql.ConnexionDB();
            String sql = "INSERT INTO enseignant (cne, nom, prenom) VALUES (?, ?, ?)";
            try (Connection conn = connection; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cne);
                stmt.setString(2, nom);
                stmt.setString(3, prenom);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    ensignantList.add(new Ensignant(
                            0,
                            cne,
                            nom,
                            prenom
                    ));
                    showAlert("Succès", "Enseignant ajouté avec succès.");
                    clearFields();
                    ensignantTable.refresh();
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de l'ajout de l'enseignant : " + e.getMessage());
            }
            
        }
    
    private void modifierEnseignant(int id,String cne, String nom, String prenom) {
        Connection connection = ConnexionMysql.ConnexionDB();
            String sql = "UPDATE enseignant SET cne=? ,nom = ?, prenom = ? WHERE id = ?";
            try (Connection conn = connection; PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cne );
                stmt.setString(2, nom);
                stmt.setString(3, prenom );
                 stmt.setInt(4, currentEnsignant.getId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    currentEnsignant.setCne(cne);
                    currentEnsignant.setNom(nom);
                    currentEnsignant.setPrenom(prenom);
 
                     ensignantTable.refresh();

                    showAlert("Succès", "Enseignant modifié avec succès.");
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification de l'enseignant : " + e.getMessage());
            }
        }
    


 
    private boolean areFieldsEmpty() {
        return cneField.getText().trim().isEmpty() ||
               nomField.getText().trim().isEmpty() ||
               prenomField.getText().trim().isEmpty();
    }
    private void clearFields() {
        cneField.clear();
        nomField.clear();
        prenomField.clear();
 
    }
    public void setEnsignantList(ObservableList<Ensignant> ensignantList) {
        this.ensignantList = ensignantList;
    }

    public void setEnsignantTable(TableView<Ensignant> ensignantTable) {
        this.ensignantTable = ensignantTable;
    }

    public void setEnsignant(Ensignant ensignant) {
        this.currentEnsignant = ensignant;
        if (ensignant != null) {
            cneField.setText(ensignant.getCne());
            nomField.setText(ensignant.getNom());
            prenomField.setText(ensignant.getPrenom());
 
        }
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
    }}
