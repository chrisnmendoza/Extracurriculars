package app.controller;

import app.CalorieChecker;
import app.Food;
import app.ServerInfo;
import app.SqlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import pkgLogic.Loan;
import pkgLogic.Payment;
import pkgEnums.eModes;

import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class CalorieCheckerController implements Initializable {
	private eModes mode = null;
	private CalorieChecker SC = null;

	@FXML
	private ComboBox command;

	@FXML
	private TextField foodName;

	@FXML
	private TextField calories;

	@FXML
	private TextField protein;

	@FXML
	private TextField carbohydrates;

	@FXML
	private TextField fat;
	
	@FXML
	private Label lblFoodName;
	
	@FXML
	private Label lblCalories;
	
	@FXML
	private Label lblProtein;
	
	@FXML
	private Label lblCarbohydrates;
	
	@FXML
	private Label lblFat;

	@FXML
	private ListView allFoods;
	
	@FXML
	private GridPane nutritionalFacts;
	
	@FXML
	private Label foodNameOutput;
	
	@FXML
	private Label caloriesOutput;
	
	@FXML
	private Label proteinOutput;
	
	@FXML
	private Label carbohydratesOutput;
	
	@FXML
	private Label fatOutput;
	
	@FXML
	private Label insertSuccess;

	private String table = "dbo.Food";
	private String database = "testDatabase";
	private String columns = "(Name, Calories, [Protein (g)], [Carbohydrates (g)], [Fat (g)])";
	String username = "REDACTED";
	String password = "REDACTED";
	String ip = "REDACTED";
	private ServerInfo server = new ServerInfo(ip, "49172", "testDatabase", username, password);
	private SqlConverter sqlConverter = new SqlConverter(database, table, columns, server);
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		foodNameOutput.setText("");
		caloriesOutput.setText("");
		proteinOutput.setText("");
		carbohydratesOutput.setText("");
		fatOutput.setText("");
		insertSuccess.setVisible(false);
		String table = "dbo.Food";
        String database = "testDatabase";
        String columns = "(Name, Calories, [Protein (g)], [Carbohydrates (g)], [Fat (g)])";
        ServerInfo server = new ServerInfo("192.168.1.46", "49172", "testDatabase", "chrisnmendoza", "Chiopet1");
        SqlConverter sqlConverter = new SqlConverter(database, table, columns, server);
        ArrayList<String> foods = sqlConverter.getAllFoodNames();

		command.getItems().addAll("Insert A Food Item", "Show All Food Names", "Find Nutritional Facts About a Food Item", "Turbo Add");

		command.getSelectionModel().select(command.getItems().get(0));
		toggleCommands(command.getItems().get(0).toString());
		mode = eModes.INSERT;

		command.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				toggleLabelsAndTextFields(t1);
				toggleCommands(t1);
				btnClearResults(null);
				changeMode(t1);
			}
		});
		
		allFoods.getItems().addAll(foods);
		// PaintChart();
	}

	public void setMainApp1(CalorieChecker sc) {
		this.SC = sc;
	}

	@FXML
	private void btnClearFields(ActionEvent event) {

		btnClearResults(event);
		foodName.clear();
		calories.clear();
		protein.clear();
		carbohydrates.clear();
		fat.clear();
	}

	private void toggleEscrow(String strLoanType) {
		carbohydrates.setVisible((strLoanType == "Home"));
	}
	
	private void toggleCommands(String command) {
		allFoods.setVisible(command.equals("Show All Food Names"));
		nutritionalFacts.setVisible(command.equals("Find Nutritional Facts About a Food Item"));
		if(command.equals("Show All Food Names")) {
			ArrayList<String> foods = sqlConverter.getAllFoodNames();
			allFoods.getItems().clear();
			allFoods.getItems().addAll(foods);
		}
		else if(command.equals("Find Nutritional Facts About a Food Item")) {
		}
		else if(command.equals("Insert A Food Item")) {
			
		}
	}
	
	private void toggleLabelsAndTextFields(String command) {
		lblFoodName.setVisible(!command.equals("Show All Food Names"));
		lblCalories.setVisible(command.equals("Insert A Food Item"));
		lblProtein.setVisible(command.equals("Insert A Food Item"));
		lblCarbohydrates.setVisible(command.equals("Insert A Food Item"));
		lblFat.setVisible(command.equals("Insert A Food Item"));
		allFoods.setVisible(command.equals("Show All Food Names"));
		foodName.setVisible(!command.equals("Show All Food Names"));
		calories.setVisible(command.equals("Insert A Food Item"));
		protein.setVisible(command.equals("Insert A Food Item"));
		carbohydrates.setVisible(command.equals("Insert A Food Item"));
		fat.setVisible(command.equals("Insert A Food Item"));
		insertSuccess.setVisible(false);
	}
	
	private void changeMode(String command) {
		if(command.equals("Insert A Food Item")) {
			mode = eModes.INSERT;
		}
		else if(command.equals("Show All Food Names")) {
			mode = eModes.VIEWALL;
		}
		else if(command.equals("Find Nutritional Facts About a Food Item")) {
			mode = eModes.VIEWONE;
		}
		//TODO: add turboaddmode
		else {
			mode = eModes.ERROR;
		}
	}

	@FXML
	private void btnClearResultsKeyPress(KeyEvent event) {
		btnClearResults(null);
	}

	/**
	 * btnClearFields - Clear the input fields and output chart
	 * 
	 * @param event
	 */

	@FXML
	private void btnClearResults(ActionEvent event) {		
	}

	private boolean ValidateData() {
		
		boolean isError = false; //Checks if at least one error is present
		StringBuilder errors = new StringBuilder();  //StringBuilder for error message
		Alert err = new Alert(AlertType.ERROR);
		err.setHeaderText("Incorrectly Entered Data");
		
		//foodName Validations
		if(foodName.getText().trim().isEmpty()) {	
			isError = true;
			errors.append("Name of Food is required\n");
		}
		
		if(mode == eModes.INSERT) {
		
			//calories Validations
			if(calories.getText().trim().isEmpty()) {
				isError = true;
				errors.append("Calories is required\n");
			}
			else if(Double.parseDouble(calories.getText().trim()) < 0) {
				isError = true;
				errors.append("Positive calories is required\n");
			}
			
			//protein Validations
			if(protein.getText().trim().isEmpty()) {
				isError = true;
				errors.append("Protein is required\n");
			}
			else if(Double.parseDouble(protein.getText().trim()) < 0) {
				isError = true;
				errors.append("Positive protein is required\n");
			}
			
			//carbohydrates Validation
			if(carbohydrates.getText().trim().isEmpty()) {
				isError = true;
				errors.append("Carbohydrates is required\n");
			}
			else if(Double.parseDouble(carbohydrates.getText().trim()) < 0) {
				isError = true;
				errors.append("Positive carbohydrates is required\n");
			}
			
			//fat Validation
			if(fat.getText().trim().isEmpty()) {
				isError = true;
				errors.append("Fat is required\n");
			}
			else if(Double.parseDouble(fat.getText().trim()) < 0) {
				isError = true;
				errors.append("Positive fat is required\n");
			}
		}
		
		if(isError) {
			err.setContentText(errors.toString());
			err.showAndWait();
			return false;
		}

		return true;
	}

	@FXML
	private void btnCalcLoan(ActionEvent event) {

		btnClearResults(event);

		// Validate the data. If the method returns 'false', exit the method
		if (ValidateData() == false)
			return;
		
		switch(mode) {
			case VIEWONE:
				ArrayList<String> food = sqlConverter.getFoodByName(foodName.getText());
				if(food.size() > 0) {
					foodNameOutput.setText(food.get(0));
					caloriesOutput.setText(food.get(1));
					proteinOutput.setText(food.get(2));
					carbohydratesOutput.setText(food.get(3));
					fatOutput.setText(food.get(4));
				}
				else {
					foodNameOutput.setText("Food Not Found");
					caloriesOutput.setText("");
					proteinOutput.setText("");
					carbohydratesOutput.setText("");
					fatOutput.setText("");
				}
				break;
			case INSERT:
				Food foodInsert = new Food(foodName.getText(), Integer.parseInt(calories.getText()), Integer.parseInt(protein.getText()), Integer.parseInt(carbohydrates.getText()), Integer.parseInt(fat.getText()));
				sqlConverter.insertFoodIntoTable(foodInsert);
				insertSuccess.setVisible(true);
				btnClearFields(null);
				break;
				
			//TODO add turboadder
			case ERROR:
				System.out.println("error in combobox selection");
				break;
			default:
				break;		
		}
	}
}
