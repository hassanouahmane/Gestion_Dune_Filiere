package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import Models.Matiere;
import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Menu_Controller {
    @FXML
    private Label nmbr_ensg;

    @FXML
    private Label nmbr_etu;

    @FXML
    private Label nmbr_matieres;

    @FXML
    private Button Dash;

    @FXML
    private Button deconnection;

    @FXML
    private Button enseignants;

    @FXML
    private Button etudiants;

    @FXML
    private Button matiere;

    @FXML
    private Button profile;

    @FXML
    private VBox menu;

    @FXML
    private TableView<Matiere> matiereTable;

    @FXML
    private TableColumn<Matiere, Integer> ide;

    @FXML
    private TableColumn<Matiere, String> nom;

    @FXML
    private TableColumn<Matiere, String> prof;


    @FXML
    private TableColumn<Matiere, Integer> Filliere;


    @FXML
    private Button filliere;
    
    private ObservableList<Matiere> matieres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeTableColumns();
        loadCounts();

 
        setupButtonEvents(Dash);
        setupButtonEvents(deconnection);
        setupButtonEvents(enseignants);
        setupButtonEvents(etudiants);
        setupButtonEvents(matiere);
        setupButtonEvents(filliere);
        setupButtonEvents(profile);
    }
    
    private void loadCounts() {
        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                // Requête pour compter les étudiants
                String countEtudiantsQuery = "SELECT COUNT(*) AS count FROM etudiant";
                ResultSet etudiantsResultSet = statement.executeQuery(countEtudiantsQuery);
                if (etudiantsResultSet.next()) {
                    nmbr_etu.setText(String.valueOf(etudiantsResultSet.getInt("count")));
                }

                // Requête pour compter les matières
                String countMatieresQuery = "SELECT COUNT(*) AS count FROM module";
                ResultSet matieresResultSet = statement.executeQuery(countMatieresQuery);
                if (matieresResultSet.next()) {
                    nmbr_matieres.setText(String.valueOf(matieresResultSet.getInt("count")));
                }

                // Requête pour compter les enseignants
                String countEnseignantsQuery = "SELECT COUNT(*) AS count FROM enseignant";
                ResultSet enseignantsResultSet = statement.executeQuery(countEnseignantsQuery);
                if (enseignantsResultSet.next()) {
                    nmbr_ensg.setText(String.valueOf(enseignantsResultSet.getInt("count")));
                }
            } catch (Exception e) {
             }
        } else {
            System.err.println("Connexion à la base de données échouée.");
        }
    }


    private void initializeTableColumns() {
        if (nom != null && prof != null && Filliere != null) {
            nom.setCellValueFactory(new PropertyValueFactory<>("nom_matiere"));
            prof.setCellValueFactory(new PropertyValueFactory<>("proffesseur"));
            Filliere.setCellValueFactory(new PropertyValueFactory<>("filiere"));

            // Charger les données depuis la base de données
            loadDataFromDatabase();

            // Lier les données au tableau
            matiereTable.setItems(matieres);
        }
    }


    private void loadDataFromDatabase() {
        Connection connection = ConnexionMysql.ConnexionDB();
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT m.id,m.Nom_Module AS NomModule, CONCAT(e.nom, ' ', e.prenom) AS NomEnseignant, f.Nom_Filiere AS NomFilliere " +
                               "FROM module m " +
                               "JOIN enseignant e ON m.enseignant = e.id " +
                               "JOIN filiere f ON m.FILLIERE = f.id";
                ResultSet resultSet = statement.executeQuery(query);
                matieres.clear();
                while (resultSet.next()) {
                    matieres.add(new Matiere(
                        resultSet.getInt("id"),
                        resultSet.getString("NomModule"),
                        resultSet.getString("NomEnseignant"),
                        resultSet.getString("NomFilliere")
                    ));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données : " + e.getMessage());
            }
        } else {
            System.err.println("Connexion à la base de données échouée.");
        }
    }


    private void setupButtonEvents(Button button) {
        button.setOnMouseEntered(event -> button.getStyleClass().add("hovered-button"));
        button.setOnMouseExited(event -> button.getStyleClass().remove("hovered-button"));
        button.setOnAction(event -> handleButtonClick(button));
    }

    private void handleButtonClick(Button button) {
         button.getStyleClass().add("active-button");

        // Chargez la vue associée au bouton
        if (button == Dash) {
            loadView("/application/Menu.fxml");
        } else if (button == deconnection) {
            Deconnexion(new ActionEvent());
        } else if (button == enseignants) {
            loadView("/application/GestionEnseignants.fxml");
        } else if (button == etudiants) {
            loadView("/application/GestionEtudiant.fxml");
        } else if (button == matiere) {
            loadView("/application/GestionMatiere.fxml");
        } else if (button == filliere) {
            loadView("/application/Gestion_Filliere.fxml");
        } else if (button == profile) {
            loadView("/application/Profile.fxml");
        }
    }

 
    @FXML
    void Deconnexion(ActionEvent event) {
        javafx.application.Platform.exit();

    	
    }

    private void loadView(String fxmlPath) {
        try {
            Pane root = FXMLLoader.load(getClass().getResource(fxmlPath));
            menu.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue : " + fxmlPath + " - " + e.getMessage());
        }
    }
}
