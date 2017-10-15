/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * @author gsb
 * *****************************************************************************
 * width category: 
 * w/h  0 = 0-30;       1 = 30-40;      2 = 50-75;      3 = 75-100; 
 *      4 = 100-130;    5 = 130-140     6 = 150-175     7 = 175-200
 *      8 = 200-230
 *      
 * example: w3h5 means width range 75-100 and height range 130-140
 * 
 */
public class CharacterModel {
    ButtonType addButton;
    String character;
    String currentText;
    boolean dwethoAtTheEnd = false;
    boolean daariAtTheEnd = false;
    
    /**
 * @author gsb
 * *****************************************************************************
 * width category: 
 * w/h  0 = 0-30;       1 = 30-50;      2 = 50-75;      3 = 75-100; 
 *      4 = 100-130;    5 = 130-145     6 = 150-175     7 = 175-200
 *      8 = 200-230
 *      
 * example: w3h5 means width range 75-100 and height range 130-140
     * @param w: width of the character
     * @param h: height of the character
     * @param snapShot: the snapshot of the character in red border,
     *          used to show in the dialog to add new characterKey in database.
     * @param key: key is the characterKey being examined
     * @return the character.
     * 
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
 * 
 */
    
    public String verifyCharacter(int w, int h, 
            WritableImage snapShot,
            String key, TextArea textArea) throws SQLException, ClassNotFoundException{
        
        String retrievedCharacter = "";
        
        String widthCatagory = "";
        String heightCatagory = "";
        String catagoryName = "";
        
        if(w>0 && w<=10) widthCatagory = "w01";
            else if(w>10 && w<=20) widthCatagory = "w02";
            else if(w>20 && w<=30) widthCatagory = "w03";
            
            else if(w>30 && w<=34) widthCatagory = "w11";
            else if(w>34 && w<=40) widthCatagory = "w12";
            else if(w>40 && w<=50) widthCatagory = "w13";
            
            else if(w>50 && w<=55) widthCatagory = "w21";
            else if(w>55 && w<=65) widthCatagory = "w22";
            else if(w>65 && w<=75) widthCatagory = "w23";
            
            else if(w>75 && w<=80) widthCatagory = "w31";
            else if(w>80 && w<=90) widthCatagory = "w32";
            else if(w>90 && w<=100) widthCatagory = "w33";
            
            else if(w>100 && w<=110) widthCatagory = "w41";
            else if(w>110 && w<=120) widthCatagory = "w42";
            else if(w>120 && w<=130) widthCatagory = "w43";
            
            else if(w>130 && w<=134) widthCatagory = "w51";
            else if(w>134 && w<=140) widthCatagory = "w52";
            else if(w>140 && w<=150) widthCatagory = "w53";
            
            else if(w>150 && w<=155) widthCatagory = "w61";
            else if(w>155 && w<=165) widthCatagory = "w62";
            else if(w>165 && w<=175) widthCatagory = "w63";
            
            else if(w>175 && w<=180) widthCatagory = "w71";
            else if(w>180 && w<=190) widthCatagory = "w72";
            else if(w>190 && w<=200) widthCatagory = "w73";
            
            else if(w>200 && w<=210) widthCatagory = "w81";
            else if(w>210 && w<=220) widthCatagory = "w82";
            else if(w>220 && w<=230) widthCatagory = "w83";
            
            else if(w>230 && w<=234) widthCatagory = "w91";
            else if(w>234 && w<=240) widthCatagory = "w92";
            else if(w>240 && w<=250) widthCatagory = "w93";
            
            else if(w>250 && w<=255) widthCatagory = "w101";
            else if(w>255 && w<=265) widthCatagory = "w102";
            else if(w>265 && w<=275) widthCatagory = "w103";
            
            else if(w>275 && w<=280) widthCatagory = "w111";
            else if(w>280 && w<=290) widthCatagory = "w112";
            else if(w>290 && w<=300) widthCatagory = "w113";
            
            else System.out.println("Error! width beyond catagory!");
        
        if(h>0 && h<=30) heightCatagory = "h01";
            else if(h>10 && h<=20) heightCatagory = "h02";
            else if(h>20 && h<=30) heightCatagory = "h03";
            
            else if(h>30 && h<=34) heightCatagory = "h11";
            else if(h>34 && h<=40) heightCatagory = "h12";
            else if(h>40 && h<=50) heightCatagory = "h13";
            
            else if(h>50 && h<=55) heightCatagory = "h21";
            else if(h>55 && h<=65) heightCatagory = "h22";
            else if(h>65 && h<=75) heightCatagory = "h23";
            
            else if(h>75 && h<=80) heightCatagory = "h31";
            else if(h>80 && h<=90) heightCatagory = "h32";
            else if(h>90 && h<=100) heightCatagory = "h33";
            
            else if(h>100 && h<=110) heightCatagory = "h41";
            else if(h>110 && h<=120) heightCatagory = "h42";
            else if(h>120 && h<=130) heightCatagory = "h43";
            
            else if(h>130 && h<=134) heightCatagory = "h51";
            else if(h>134 && h<=140) heightCatagory = "h52";
            else if(h>140 && h<=150) heightCatagory = "h53";
            
            else if(h>150 && h<=155) heightCatagory = "h61";
            else if(h>155 && h<=165) heightCatagory = "h62";
            else if(h>165 && h<=175) heightCatagory = "h63";
            
            else if(h>175 && h<=180) heightCatagory = "h71";
            else if(h>180 && h<=190) heightCatagory = "h72";
            else if(h>190 && h<=200) heightCatagory = "h73";
            
            else if(h>200 && h<=210) heightCatagory = "h81";
            else if(h>210 && h<=220) heightCatagory = "h82";
            else if(h>220 && h<=230) heightCatagory = "h83";
            
            else if(h>230 && h<=234) heightCatagory = "h91";
            else if(h>234 && h<=240) heightCatagory = "h92";
            else if(h>240 && h<=250) heightCatagory = "h93";
            
            else if(h>250 && h<=255) heightCatagory = "h101";
            else if(h>255 && h<=265) heightCatagory = "h102";
            else if(h>265 && h<=275) heightCatagory = "h103";
            
            else if(h>275 && h<=280) heightCatagory = "h111";
            else if(h>280 && h<=290) heightCatagory = "h112";
            else if(h>290 && h<=300) heightCatagory = "h113";
                    
            else System.out.println("Error! height beyond catagory!");
        
        if(!widthCatagory.isEmpty() && !heightCatagory.isEmpty()){
            catagoryName = widthCatagory+heightCatagory;
            System.out.println("Catagory Name: "+catagoryName);
            
            //It will check if table exists in this catagory
            //if not, it will create a new table
            DatabaseDialogClass ddClass = new DatabaseDialogClass();
            
            if(SQLiteManager.catagoryTableExists(catagoryName, ddClass.selectedDatabase)){
                
                //if table found, search for character
                String resultedText 
                        = SQLiteManager.getCharacterFromDB(
                                catagoryName, ddClass.selectedDatabase, key);
                
                
                //if character not found, then add character to database
                if(resultedText.isEmpty() && !key.isEmpty()){
                    String newChar = this.addCharacter(catagoryName, key, snapShot, w, h, textArea);
                    
                    //Check the table
                   // SQLiteManager.showWholeDataFromTable(catagoryName);
                
                    if(newChar.equals("abort")) return "abort";
                    else retrievedCharacter = newChar;
                }
                //if character found then pass it to the caller method
                else {
                    resultedText = this.preProcessCurrentText(resultedText);
                    retrievedCharacter = resultedText;
                }
                    
            }
            
            //catagory table not found. so create a new catagory table
            else {
                System.out.println("catagory table not found. Creating new catagory table.");
                
                SQLiteManager.createCatagoryTable(catagoryName, ddClass.selectedDatabase);
                
                String newChar = this.addCharacter(catagoryName, key, snapShot, w, h, textArea);
                
                //Check the table
//                SQLiteManager.showWholeDataFromTable(catagoryName);
                
                if(newChar.equals("abort")) return "abort";
                else retrievedCharacter = newChar;
                
                
                //Build a method that will show a character input dialog
                
                //Add new character to the table
               // SQLiteManager.addCharacterToDB(catagoryName, key, character);
            }
        }
        
       
        return retrievedCharacter;
    }
    
    public void showError(){
         System.out.println("Error! characterkey not matched. Please add it.");
//                imlabel.setText(imlabel.getText()+"\nError! characterkey "
//                        + "not matched. Please add it.");
    }
    
    //Before calling this method, be sure that table exists! or create a new table!
    public String addCharacter(String catagoryName, String key, Image snapShot, 
            int w, int h, TextArea textArea){
        currentText = "";
        
        AddCharacterDialogClass acDialog 
                        = new AddCharacterDialogClass(snapShot, w, h, catagoryName, key);
        DatabaseDialogClass ddClass = new DatabaseDialogClass();
                //addButton = new ButtonType("Add");
                
        acDialog.addCharDialog.showAndWait().ifPresent(consumer -> {
            
            if( consumer.equals(ButtonType.OK) && !acDialog.textField.getText().isEmpty()){
                currentText = acDialog.textField.getText();
                System.out.println("catagoryName from dialog: "+currentText);
                System.out.println("selectedDatabase: "+ddClass.selectedDatabase);
                System.out.println("characterKey from dialog: "+key);
                System.out.println("character from dialog: "+currentText);
                
                
                
                try {

                    SQLiteManager.addCharacterToDB(catagoryName, ddClass.selectedDatabase, key, currentText);

                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(CharacterModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                currentText = this.preProcessCurrentText(currentText);

            } 

        else if(consumer.equals(ButtonType.CANCEL)) currentText = "abort";

        else {
            System.out.println("character is in else: "+currentText);
            currentText = "";
        }

        });
                
        return currentText;  
                
    }
    
    public String preProcessCurrentText(String currentText){
        //if currentText ends with "၊" or "ေ", then process them differently
//        if(currentText.endsWith("၊")){
//            currentText = this.processTextForDaari(currentText);
//        }

        //process for "ေ"
        if(currentText.endsWith("ေ")){
            currentText = this.processTextForDwetho(currentText);
        }

        else {
            daariAtTheEnd = false;
            dwethoAtTheEnd = false;
        }
        
        return currentText;
    }
    
       
    public String processTextForDaari(String currentText){
        
        System.out.println("currentText ends with \"၊\"");
        if(currentText.length() == 1) currentText = "";
        else if(currentText.length()>1){
            //Getting a substring without "ေ" at the end
            currentText = currentText.substring(0, currentText.length()-1);
        }
        daariAtTheEnd = true;
        
        return currentText;
    }
    
    
    public String processTextForDwetho(String currentText){

        System.out.println("currentText ends with \"ေ\"");
        if(currentText.length() == 1) currentText = "";
        else if(currentText.length()>1){
            //Getting a substring without "ေ" at the end
            currentText = currentText.substring(0, currentText.length()-1);
        }
        dwethoAtTheEnd = true;

        return currentText;
    }
    
    /**
 * @author gsb
 * *****************************************************************************
 * width category: 
 * w/h  0 = 0-30;       1 = 30-50;      2 = 50-75;      3 = 75-100; 
 *      4 = 100-130;    5 = 130-150     6 = 150-175     7 = 175-200
 *      8 = 200-230
 *      
 * example: w3h5 means width range 75-100 and height range 130-140
 * 
 */
    
    
}
