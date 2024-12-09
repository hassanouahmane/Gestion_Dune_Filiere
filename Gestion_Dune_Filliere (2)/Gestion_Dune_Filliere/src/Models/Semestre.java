package Models;

public class Semestre {
    private int id;
    private String nom_semestre;
    private String nom_filliere;
    private String nom_module;
    private int Annee;


    public Semestre(int id, String nom_semestre , String nom_filliere, String nom_module , int Annee) {
        this.id = id;
        this.nom_semestre = nom_semestre;
        this.nom_module = nom_module;
        this.nom_filliere = nom_filliere;
        this.Annee = Annee;
        
    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNom_semestre() {
		return nom_semestre;
	}


	public void setNom_semestre(String nom_semestre) {
		this.nom_semestre = nom_semestre;
	}


	public String getNom_filliere() {
		return nom_filliere;
	}


	public void setNom_filliere(String nom_filliere) {
		this.nom_filliere = nom_filliere;
	}


	public String getNom_module() {
		return nom_module;
	}


	public void setNom_module(String nom_module) {
		this.nom_module = nom_module;
	}


	public int getAnnee() {
		return Annee;
	}


	public void setAnnee(int annee) {
		Annee = annee;
	}

 
}