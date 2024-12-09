package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

public class PieChartController implements Initializable {

	
	  @FXML
	    private PieChart pieChart;
	  
	  
	  
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				 new PieChart.Data("Etudiant redoublant", 50),
				 new PieChart.Data("Etudiant reussit", 50));
				 pieChart.setData(pieChartData);
				};
		
	

}
