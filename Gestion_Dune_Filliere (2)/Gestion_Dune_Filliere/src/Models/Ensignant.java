package Models;

 
public class Ensignant {
	private int id;
    private  String cne;
    private String nom;
    private String prenom;
    
 


    public Ensignant(int id,String cne, String nom, String prenom) {
    	this.id = id;
        this.cne = cne;
        this.nom = nom;
        this.prenom =prenom;
    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getCne() {
		return cne;
	}


	public String getNom() {
		return nom;
	}


	public String getPrenom() {
		return prenom;
	}


 


	public void setCne(String cne) {
		this.cne = cne;
	}


	public void setNom(String nom) {
		this.nom = nom;
	}


	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

 

    
}
