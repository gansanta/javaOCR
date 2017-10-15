/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 *
 * @author gsb
 */
public class ShowCharacterDialogClass {
    Dialog showCharDialog = new Dialog(); //create a dialog 
    TextField textField = new TextField();
    
    public ShowCharacterDialogClass(Image charSnapshot, 
            int cWidth, int cHeight, String group, String characterKey){
  
        DialogPane dgPane = new DialogPane();//create a custom dialogpane
        dgPane.setHeader(null); // no need of header, that's ugly!
        
        GridPane gridPane = new GridPane();
        
        //Show the snapshot of the character 
        ImageView charView = new ImageView();
        charView.setImage(charSnapshot);
        gridPane.addRow(0, charView);
        
        // Show tihe Width and Height in a row
        Label wLabel = new Label();
        Label hLabel = new Label();
        wLabel.setText("Width: "+cWidth);
        hLabel.setText("Height: "+cHeight);
        gridPane.addRow(1, wLabel, hLabel);
       
        // Show tihe Group in a row
        Label groupLabel = new Label("Group:");
        Label groupLabel2 = new Label(group);
        gridPane.addRow(2, groupLabel, groupLabel2);
        
        // Show Aspect Ratio in a row
        Label arLabel = new Label("Aspect Ratio:");
        String aspectRatio = String.format("%.5g%n",(double)cWidth/cHeight);
        Label arLabel2 = new Label(aspectRatio);
        gridPane.addRow(3, arLabel, arLabel2);
        
        // Show CharacterKey in a row
        Label keyLabel = new Label("CharacterKey: ");
        Label keyLabel2 = new Label(characterKey);
        gridPane.addRow(4, keyLabel, keyLabel2);
        
        //Get a textField for new character to be added to the database
        Label cLabel = new Label("Enter Character");
        textField = new TextField();
        gridPane.addRow(5, cLabel, textField);
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        
        //Add gridpane to the dialogPane
        dgPane.setContent(gridPane); 
        dgPane.setHeaderText("What is this character?");
        //Add cancel and Add button to the dialogPane
        
        //ButtonType addButton = new ButtonType("Add");
        dgPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL); 
//        textField.requestFocus();
        
        
        showCharDialog.setDialogPane(dgPane);// set the dialogpane to the dialog
        //settingsDialog.showAndWait();
    }
    
}
