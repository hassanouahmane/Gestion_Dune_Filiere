package Models;

public class Matiere {
    private int id;
    private String nom_matiere;
    private String proffesseur;
    private String filiere;

    // Constructeur
    public Matiere(int id, String nom_matiere, String proffesseur, String filiere) {
        this.id=id;
    	this.nom_matiere = nom_matiere;
        this.proffesseur = proffesseur;
        this.filiere = filiere;
    }

    // Getters et setters conformes aux conventions JavaBeans
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_matiere() {
        return nom_matiere;
    }

    public void setNom_matiere(String nom_matiere) {
        this.nom_matiere = nom_matiere;
    }

    public String getProffesseur() {
        return proffesseur;
    }

    public void setProffesseur(String proffesseur) {
        this.proffesseur = proffesseur;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }
}
