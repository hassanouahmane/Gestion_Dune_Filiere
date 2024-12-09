package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Ensignant;
import Models.Filiere;
import Models.Matiere;
import Models.Semestre;
import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FiliereFormController {

    @FXML
    private ComboBox<String> semesterComboBox;

    @FXML
    private ComboBox<String> moduleComboBox;

    @FXML
    private ComboBox<String> enseignantComboBox;

    @FXML
    private TextField filiereTextField;
    
    @FXML
    Button saveButton;
    
    @FXML
    private TextField nombreEtudiantsTextField;

    @FXML
    private TableView<Filiere> Filieretable;

    private ObservableList<Semestre> semestreList = FXCollections.observableArrayList();
    private ObservableList<Matiere> matiereList = FXCollections.observableArrayList();
    private ObservableList<Ensignant> enseignantList = FXCollections.observableArrayList();
    private ObservableList<Filiere> FiliereList;
    private Filiere currentFiliere;

    public void setFiliereList(ObservableList<Filiere> filiereList) {
        this.FiliereList = filiereList;
    }

    public void setFiliereTable(TableView<Filiere> filiereTable) {
        this.Filieretable = filiereTable;
    }

    public void setCurrentFiliere(Filiere filiere) {
        this.currentFiliere = filiere;
        populateFormFields(filiere, null);
    }

    @FXML
    public void initialize() {
        if (semesterComboBox == null || moduleComboBox == null || enseignantComboBox == null) {
            System.out.println("L'un des ComboBox est null ! Vérifiez les liaisons FXML.");
        } else {
            initializeSemestreComboBox();
            initializeModuleComboBox();
            initializeEnseignantComboBox();
        }
    }
    private void initializeSemestreComboBox() {
        String query = "SELECT id, nom_semestre FROM semestre"; // Assurez-vous de récupérer l'ID aussi
        try (Connection connection = ConnexionMysql.ConnexionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<String> semestreNames = new ArrayList<>();
            while (resultSet.next()) {
                String nomSemestre = resultSet.getString("nom_semestre");
                semestreNames.add(nomSemestre);
                // Ajouter l'objet Semestre à la liste semestreList
                int id = resultSet.getInt("id");
                semestreList.add(new Semestre(id, nomSemestre,null,null,0)); // Assurez-vous que la classe Semestre a un constructeur adapté
            }
            semesterComboBox.setItems(FXCollections.observableArrayList(semestreNames));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'initialisation des semestres.");
        }
    }


    private void initializeModuleComboBox() {
        String sql = "SELECT Nom_Module FROM module";
        try (Connection connection = ConnexionMysql.ConnexionDB();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<String> moduleNames = new ArrayList<>();
            while (rs.next()) {
                moduleNames.add(rs.getString("Nom_Module"));
            }
            moduleComboBox.setItems(FXCollections.observableArrayList(moduleNames));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors du chargement des modules.");
        }
    }

    private void initializeEnseignantComboBox() {
        String sql = "SELECT nom FROM enseignant";
        try (Connection connection = ConnexionMysql.ConnexionDB();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<String> enseignantNames = new ArrayList<>();
            while (rs.next()) {
                enseignantNames.add(rs.getString("nom"));
            }
            enseignantComboBox.setItems(FXCollections.observableArrayList(enseignantNames));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors du chargement des enseignants.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void populateFormFields(Filiere filiere, Semestre semestre) {
        if (filiere != null) {
        	filiereTextField.setText(filiere.getNomFiliere());
            nombreEtudiantsTextField.setText(String.valueOf(filiere.getNombreEtudiants()));

            if (semestre == null) {
                semestre = semestreList.stream()
                    .filter(s -> s.getId() == filiere.getId_semestre())
                    .findFirst()
                    .orElse(null);
            }

            if (semestre != null) {
            	semesterComboBox.setValue(semestre.getNom_semestre());
            } else {
            	semesterComboBox.setValue(null);
            }
        }
    }

    @FXML
    void saveFiliere() {
        String nomFiliere = filiereTextField.getText();
        String nombreEtudiantsText = nombreEtudiantsTextField.getText();

        if (nomFiliere.isEmpty() || nombreEtudiantsText.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        int nombreEtudiants;
        try {
            nombreEtudiants = Integer.parseInt(nombreEtudiantsText);
        } catch (NumberFormatException e) {
            System.out.println("Le nombre d'étudiants doit être un entier.");
            return;
        }

        // Récupération de l'objet Semestre
        String selectedSemestre = semesterComboBox.getValue();
        Semestre semestre = semestreList.stream()
                .filter(s -> s.getNom_semestre().equals(selectedSemestre))
                .findFirst()
                .orElse(null);

        if (semestre == null) {
            System.out.println("Veuillez sélectionner un semestre valide.");
            return;
        }

        int semestreId = semestre.getId();

        if (currentFiliere == null) {
            ajouterFiliere(nomFiliere, nombreEtudiants, semestreId);
        } else {
            modifierFiliere(currentFiliere.getId(), nomFiliere, nombreEtudiants, semestreId);
        }
    }

    private void ajouterFiliere(String nomFiliere, int nombreEtudiants, int semestre) {
        String sql = "INSERT INTO filiere (Nom_Filiere, Nbr_etudiants, Semestre) VALUES (?, ?, ?)";
        try (Connection connection = ConnexionMysql.ConnexionDB();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nomFiliere);
            stmt.setInt(2, nombreEtudiants);
            stmt.setInt(3, semestre);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                FiliereList.add(new Filiere(0, nomFiliere, nombreEtudiants, semestre)); 
                Filieretable.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifierFiliere(int id, String nomFiliere, int nombreEtudiants, int semestre) {
        String sql = "UPDATE filiere SET Nom_Filiere = ?, Nbr_etudiants = ?, Semestre = ? WHERE id = ?";
        try (Connection connection = ConnexionMysql.ConnexionDB();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nomFiliere);
            stmt.setInt(2, nombreEtudiants);
            stmt.setInt(3, semestre);
            stmt.setInt(4, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                currentFiliere.setNomFiliere(nomFiliere);
                currentFiliere.setNombreEtudiants(nombreEtudiants);
                currentFiliere.setNom_semestre(semestre);
                Filieretable.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
