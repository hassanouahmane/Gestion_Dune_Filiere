package Models;

public class Filiere {
		
    private int id;
    private String nomFiliere;
    private int nombreEtudiants;
    private int id_semestre;
 

    public Filiere(int id ,String nomFiliere, int nombreEtudiants, int id_semestre) {
    	this.id=id;
        this.nomFiliere = nomFiliere;
        this.nombreEtudiants = nombreEtudiants;
        this.id_semestre = id_semestre;

    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNomFiliere() {
		return nomFiliere;
	}


	public void setNomFiliere(String nomFiliere) {
		this.nomFiliere = nomFiliere;
	}


	public int getNombreEtudiants() {
		return nombreEtudiants;
	}


	public void setNombreEtudiants(int nombreEtudiants) {
		this.nombreEtudiants = nombreEtudiants;
	}


	public int getId_semestre() {
		return id_semestre;
	}


	public void setNom_semestre(int id_semestre) {
		this.id_semestre = id_semestre;
	}




 
}