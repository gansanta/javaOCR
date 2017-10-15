/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



/**
 *
 * @author gsb
 */
public class SQLiteManager {
    private static Connection connection;
    private static boolean hasData = false;
    
    public static void showWholeDataFromTable(String catagoryName, String dbName){
        //SQLiteTest sqliteTest = new SQLiteTest();
        ResultSet resultSet;
         
        try {
            resultSet = getWholeDataFromTable(catagoryName, dbName);

            while (resultSet.next()) {
                   System.out.println(resultSet.getString("characterKey") 
                           + " " + resultSet.getString("character"));
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static ResultSet getWholeDataFromTable(String catagoryName, String dbName) throws SQLException, ClassNotFoundException{
        if(connection == null){
            getConnection(dbName);
        }
        
        Statement statement = connection.createStatement();
        ResultSet rSet = statement.executeQuery(
//                statement.executeQuery("SELECT characterKey FROM "+catagoryName);
                "SELECT characterKey, character FROM "+catagoryName+" ORDER BY characterKey");
        return rSet;
    }
    
    //Before calling it, you should verify that the table exists
    public static ArrayList<String[]> getAllRowsFromTable(String tableName, String dbName) throws SQLException, ClassNotFoundException{
        ResultSet res = getWholeDataFromTable(tableName, dbName);
        
        ArrayList<String[]> rowList = new ArrayList<>();
        while (res.next()) {
                   String cKey = res.getString("characterKey");
                   String c = res.getString("character");
                   String[] row = {cKey, c};
                   rowList.add(row);
                }
        return rowList;
    }
    
    private static void getConnection(String dbName) throws ClassNotFoundException, SQLException{
        //SQLite driver
        Class.forName("org.sqlite.JDBC");
        // database path, if it's new database, it will be created in the project folder
        connection = DriverManager.getConnection("jdbc:sqlite:"+dbName);
//        createCatagoryTable(catagoryName);
        
    }
    
    public static void addCharacterToDB(String catagoryName, String dbName,
            String characterKey, String character) 
            throws ClassNotFoundException, SQLException {
        if(connection == null) {
            // get connection
            getConnection(dbName);
        }
         PreparedStatement prep = connection
                   .prepareStatement("INSERT INTO "+catagoryName+" VALUES(?,?,?);");
                 prep.setString(2, characterKey);
                 prep.setString(3, character);
                 prep.execute();

    }
    
    private static void initialize(String catagoryName) throws SQLException {
        if( !hasData ) {
            hasData = true;
            // check for database table
            Statement state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE "
                    + "type='table' AND name='"+catagoryName+"'");
            if( !res.next()) {
                System.out.println("Building the table with prepopulated values.");
                // need to build the table
                 Statement state2 = connection.createStatement();
                 state2.executeUpdate("create table "+catagoryName+"(id integer,"
                   + "characterKey varchar(255)," + "character varchar(200)," 
                         + "primary key (id));");
            }

        }
    }
    
    public static void createCatagoryTable(String catagoryName, String dbName) throws SQLException, ClassNotFoundException{
        if(connection == null){
            getConnection(dbName);
        }
        Statement statement = connection.createStatement();
        statement.executeUpdate("create table "+catagoryName+"(id integer,"
                   + "characterKey varchar(255)," + "character varchar(200)," 
                         + "primary key (id));");
        
        //Check if the table actually created
        boolean result = catagoryTableExists(catagoryName, dbName);
        if(result) System.out.println("Table really created!");
        else System.out.println("Sorry, table creation failed for no reason.");
    }
    
    //checks whether a table exists, if not create a table
    public static boolean catagoryTableExists(String catagoryName, String dbName) throws SQLException, ClassNotFoundException {
        if(connection == null){
            getConnection(dbName);
        }
        
        
        Statement state = connection.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE "
                + "type='table' AND name='"+catagoryName+"'");
        if( !res.next()) {
            System.out.println("No table found in this catagory.");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("No table found in this catagory");
            alert.showAndWait();
            return false;
        } else return true;
    }
    
    public static void deleteTableFromDB(String catagoryName, String dbName)throws SQLException, ClassNotFoundException{
        if(connection == null){
            getConnection(dbName);
        }
        Statement state = connection.createStatement();
        state.executeUpdate("DROP TABLE IF EXISTS "+catagoryName);
    }
    
    public static void deleteRowFromTable(String chKey, String table, String dbName) throws SQLException, ClassNotFoundException{
        
        if(!chKey.isEmpty()){
            if(connection == null){
                getConnection(dbName);
            }
            Statement state = connection.createStatement();
            state.executeUpdate("DELETE FROM "+table+" WHERE characterKey='"+chKey+"'");
        }
        
    }
    
    public static ArrayList<String> getAllTableNamesFromDB(String dbName) throws SQLException, ClassNotFoundException{
        if(connection == null){
            getConnection(dbName);
        }
        
        //We give a choicebox for all tables
        ArrayList<String> tableList = new ArrayList<>();
        
        Statement state = connection.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM sqlite_master WHERE "
                + "type='table' ORDER BY name ASC");
        
        while(res.next()){
            tableList.add(res.getString("name"));
        }
        
        if(tableList.isEmpty()){
            System.out.println("No table found in the database.");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("No table found in the database!");
            alert.showAndWait();
        }
        
//        while(!res.next()){
//            System.out.println("No table found in the database.");
//            Alert alert = new Alert(AlertType.INFORMATION);
//            alert.setContentText("No table found in the database!");
//            alert.showAndWait();
//        }
//        
//        else if(res.next()){
//            tableList.add(res.getString("name"));
//        }
//        
        return tableList;
    }
    
    public static String getCharacterFromDB(String catagoryName, String dbName,
            String characterKey) 
            throws ClassNotFoundException, SQLException{
        String character = "";
        ResultSet resultSet = getWholeDataFromTable(catagoryName, dbName);
        
        //String res = resultSet.getArray(character).;
        
        
        while (resultSet.next()) {
               
               String rowId = resultSet.getRow()+"";
               String ckey = resultSet.getString("characterKey");
               String c = resultSet.getString("character");
               
              // System.out.println("DB row "+rowId+". "+ckey+" "+c);
              // System.out.println("Image CharacterKey: "+characterKey);
               //if(c)
               
               if(characterKey.equals(ckey)){
                   character = c;
                   return character;
               }
               
            }
        
        return character;
    }
     
}
