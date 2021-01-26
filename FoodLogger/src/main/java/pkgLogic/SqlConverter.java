package pkgLogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlConverter {
	private String databaseTable;
	private String columns;
	private ServerInfo server;
	
	public SqlConverter(String database, String table, String columns, ServerInfo server) {
		this.databaseTable = database + "." + table;
		this.columns = columns;
		this.server = server;
	}
	
	public ArrayList<String> getAllFoodNames() {
		ArrayList<String> food = new ArrayList<String>();
		ResultSet resultSet = null;
		try (Connection connection = DriverManager.getConnection(getConnectionInfo());
            Statement statement = connection.createStatement();) {
            resultSet = statement.executeQuery(generateSelectAllCommand());
            while (resultSet.next()) {
            	food.add(resultSet.getString(1));
            }
            return food;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return food;
        }
	}
	
	public void printAllFoodNames() {		
		ArrayList<String> food = getAllFoodNames();
		for (int i = 0; i < food.size(); i++) {
			System.out.println(food.get(i));
		}
	}
	
	private String getConnectionInfo() {
		return "jdbc:sqlserver://"
				+ server.getIp()
				+ ":" + server.getPort()
				+ ";databaseName=" + server.getDatabase()
				+ ";user=" + server.getUsername()
				+ ";password=Chiopet1";
	}
	
	private String generateSelectAllCommand() {
		return "SELECT Name FROM " + databaseTable + ";";
	}
	
	public void insertFoodIntoTable(Food food) {
		try (Connection connection = DriverManager.getConnection(getConnectionInfo()); PreparedStatement prepsInsertProduct = connection.prepareStatement(generateInsert(food), Statement.RETURN_GENERATED_KEYS);) {
			ResultSet resultSet = null;
            prepsInsertProduct.execute();
            resultSet = prepsInsertProduct.getGeneratedKeys();
            while (resultSet.next()) {
                //System.out.println("Generated: " + resultSet.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	private String generateInsert(Food food) {
		return "INSERT INTO " + databaseTable + " " + columns + " VALUES " + formatFood(food);
	}
	
	private String formatFood(Food food) {
		return "('" + food.getName().substring(0,1).toUpperCase() + food.getName().substring(1) + "'," + String.valueOf(food.getCalories()) + 
				"," + String.valueOf(food.getProtein()) + "," + 
				String.valueOf(food.getCarbohydrates()) + "," + 
				String.valueOf(food.getFat()) + ");";
	}
	
	public ArrayList<String> getFoodByName(String name) {
		ArrayList<String> food = new ArrayList<String>();
		ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection(getConnectionInfo()); Statement statement = connection.createStatement();) {
        	resultSet = statement.executeQuery(generateSelectFoodCommand(name));
        	ResultSetMetaData metaData = resultSet.getMetaData();
        	if(resultSet.isBeforeFirst()) {
	            while (resultSet.next()) {
	            	for(int i = 1; i <= metaData.getColumnCount(); i++) {
	            		food.add(resultSet.getString(i));
	            		//System.out.print(resultSet.getString(i));
	            		//System.out.print(" ");
	            	}
	            	//System.out.println();
	            }
        	}
        	return food;
        }
        catch (SQLException e) {
        	e.printStackTrace();
        	return food;
        }
	}
	
	public void printFoodByName(String name) {
		ArrayList<String> food = getFoodByName(name);
		for(String column : food) {
			System.out.print(column);
			System.out.print(" ");
		}
		System.out.println();
	}
	
	private String generateSelectFoodCommand(String name) {
		return "SELECT * FROM " + databaseTable + " WHERE Name = '" + name + "';";
	}
	
	/*
	 * I want to make a calorie checker and tracker
	 * User input should be able to:
	 * - add in new foods (maybe disable this for guest mode) INSERT INTO
	 * - pull foods in database already given name SELECT * FROM Person WHERE Name = 'B';
	 * - pull entire list of foods SELECT Name FROM 
	 */
}