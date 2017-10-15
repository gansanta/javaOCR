/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

/**
 *
 * @author gsb
 */
public class DatabaseDialogClass {
    Dialog databaseDialog; 
    
    //ChoiceBox<String> databaseCB;
//    ChoiceBox<String> rowListChoice;
    ListView<String> dbListView;
    ListView<String> tableListView;
    ListView<String> rowListView;
    
    String selectedChKey = "";
    String selectedTable = "";
    String selectedDatabase = "burmese.db";//default database
    
    ButtonType deleteButtonType;
    // to store recent table's characterKeys
    ArrayList<String> characterKeyList = new ArrayList<>();
    
//    TextField textField;
//    TextField textField2;
    
    public DatabaseDialogClass(){
  
        DialogPane dgPane = new DialogPane();//create a custom dialogpane
        //dgPane.setHeader(null); // no need of header, that's ugly!
        
        GridPane gridPane = new GridPane();
        
        //----------------Handling Database list--------------------------------
        Label dbLabel = new Label("Select database:");
        
        //Get the database files
        String[] dbList = getDBFileList();
        
        //Add the database files in the dbListView
        dbListView = new ListView<>();
        if(dbList.length>0)dbListView.getItems().addAll(dbList);
        
        dbListView.setPrefSize(150, 100);
        dbListView.getSelectionModel().selectedItemProperty()
                .addListener(this::dbSelectionChanged);
        
        Button dbDeleteButton = new Button("Delete Selected Database");
        dbDeleteButton.setOnAction(ac -> {
            if(!selectedDatabase.isEmpty()){
                
                
                
                //Get the path of the selected database file
                String curDir = System.getProperty("user.dir")+"/"+selectedDatabase;
                
                System.out.println("filePath: "+curDir);
                
                File ff = new File(curDir);
                
                if(!ff.delete()){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Cannot delete File. Something went wrong!");
                    alert.setContentText(null);
                    alert.showAndWait();
                }
                else{
                    
                    //After successfully deleting file, update the database list in the UI
                    dbListView.getItems().clear();
                    tableListView.getItems().clear();
                    rowListView.getItems().clear();
                    
                    String[] dbList2 = getDBFileList();
                    if(dbList2.length>0)dbListView.getItems().addAll(dbList2);
                }
            }
        });
        
        //----------------Handling Table list--------------------------------
        Label cgLabel = new Label("Select a table:");
//        tablelistChoice  = new ChoiceBox<>();
//        tablelistChoice.setMinWidth(200);
//        //tablelistChoice.getSelectionModel().
        
        tableListView = new ListView<>();
        tableListView.setPrefSize(200, 100);
        tableListView.getSelectionModel().selectedItemProperty()
                .addListener(this::tableSelectionChanged);
        
        Button tableDeleteButton = new Button("Delete Selected Table");
        tableDeleteButton.setOnAction(ac -> {
            if(!selectedTable.isEmpty()){
                try {
                    SQLiteManager.deleteTableFromDB(selectedTable, selectedDatabase);

                    ArrayList<String> tableList1 = SQLiteManager.getAllTableNamesFromDB(selectedDatabase);

                    //clear the previous list of tables in the tablelistView
                    tableListView.getItems().clear();
                    rowListView.getItems().clear();
                    //load the tablelist of current database to the tableListView
                    if(!tableList1.isEmpty())tableListView.getItems().addAll(tableList1);


                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(DatabaseDialogClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        
        //----------------Handling row list--------------------------------
        Label rowLabel = new Label("Select row:");
        rowListView = new ListView<>();
        rowListView.setPrefSize(500, 200);
        rowListView.getSelectionModel().selectedItemProperty()
                .addListener(this::rowSelectionChanged);
        
        Button rowDeleteButton = new Button("Delete Selected Row");
        rowDeleteButton.setOnAction(ac -> {
            if(!selectedChKey.isEmpty()){
                try {
                    SQLiteManager.deleteRowFromTable(selectedChKey, 
                            selectedTable, selectedDatabase);
                    ArrayList<String> rows2 = this.getRows(selectedTable, selectedDatabase);
                    
                    rowListView.getItems().clear();
                    if(!rows2.isEmpty())rowListView.getItems().addAll(rows2);
                    
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(DatabaseDialogClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        
        gridPane.addRow(0, dbLabel, cgLabel, rowLabel);
        gridPane.addRow(1, dbListView, tableListView, rowListView);
        gridPane.addRow(2, dbDeleteButton, tableDeleteButton, rowDeleteButton);
        
        //gridPane.addRow(1, rowLabel, rowListChoice);
        
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        
        //Add gridpane to the dialogPane
        dgPane.setContent(gridPane); 
        dgPane.setHeaderText("Database Operations");
        
        //Add cancel and Add button to the dialogPane
        
        //ButtonType addButton = new ButtonType("Add");
//        deleteButtonType = new ButtonType("Delete");
        dgPane.getButtonTypes().addAll(ButtonType.CLOSE); 
//        textField.requestFocus();
        
        databaseDialog = new Dialog(); //create a dialog
        databaseDialog.setDialogPane(dgPane);// set the dialogpane to the dialog
        //settingsDialog.showAndWait();
    }
    //-----------------------constructor ended----------------------------------
    //---------------------methods starting-------------------------------------
    public String[] getDBFileList(){
        String curDir = System.getProperty("user.dir")+"/src/ocr3";
        System.out.println("Current directory Path: "+curDir);
        
        File ff = new File(curDir);
        FilenameFilter dbFileFilter = new DBFileFilter("db");
        
      //  String[] fileList = ff.list();
        String[] dbList = ff.list(dbFileFilter);
        return dbList;
    }
    
    public void dbSelectionChanged(ObservableValue<? extends String> observable,
            String oldDB, String newDB){
        System.out.println("Database selected: "+ newDB);
        
        
        //ArrayList<String> tableList2;
        
        if(characterKeyList != null)characterKeyList.clear();
        
        try {
            if(!newDB.isEmpty()){
                selectedDatabase = newDB;
                ArrayList<String> tableList1 = SQLiteManager.getAllTableNamesFromDB(newDB);
           
                //clear the previous list of tables in the tablelistView
                tableListView.getItems().clear();

                //load the tablelist of current database to the tableListView
                if(!tableList1.isEmpty())tableListView.getItems().addAll(tableList1);

            }
           
           
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseDialogClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tableSelectionChanged(ObservableValue<? extends String> observable,
            String oldTable, String newTable){
        System.out.println("Table selected: "+ newTable);
        
        if(newTable != null){
            selectedTable = newTable;
            ArrayList<String> rows2 = this.getRows(selectedTable, selectedDatabase);
      
            rowListView.getItems().clear();
            if(!rows2.isEmpty())rowListView.getItems().addAll(rows2);
        }
        
    }
    
    public void rowSelectionChanged(ObservableValue<? extends String> observable,
            String oldRow, String newRow){
        int index = rowListView.getSelectionModel().getSelectedIndex();
        System.out.println("row Index: "+index);
        
        //when group changes, rowIndex becomes -1, so ignore it.
        if(index>=0){
            selectedChKey = characterKeyList.get(index);
            System.out.println("Selected character Key: "+ selectedChKey);
        }
        
    }
    
    public ArrayList<String> getRows(String table, String database){
        ArrayList<String> rows2 =  new ArrayList<>();
        try {
           if(!table.isEmpty() || table == null || database == null || !database.isEmpty()){
               
               ArrayList<String[]> rows = SQLiteManager.getAllRowsFromTable(table, database);
               characterKeyList.clear();
               
               System.out.println("table and database exist.");
               
                if(!rows.isEmpty()){
                    System.out.println("rows not empty.");
                    
                    rows.forEach(e -> {
                        rows2.add(e[0]+" = "+e[1]);
                        characterKeyList.add(e[0]);
                    });
                    
                    System.out.println("characterKeyList length: "+characterKeyList.size());
                }
                
               
           }
           
           
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseDialogClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rows2;
    }
     
}
