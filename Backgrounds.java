/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author gsb
 */
public class Backgrounds {
    
     //Backgrounds
    BackgroundFill bgFill = new BackgroundFill(Color.SNOW, new CornerRadii(1), new Insets(1));
    BackgroundFill bgFill2 = new BackgroundFill(Color.SANDYBROWN,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillWhite = new BackgroundFill(Color.FLORALWHITE,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillRed = new BackgroundFill(Color.RED,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillGrey = new BackgroundFill(Color.GREY,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillLightBlue = new BackgroundFill(Color.LIGHTBLUE,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillAliceBlue = new BackgroundFill(Color.ALICEBLUE,new CornerRadii(1), new Insets(1));
    BackgroundFill bgFillLightSteelBlue = new BackgroundFill(Color.LIGHTSTEELBLUE,new CornerRadii(1), new Insets(1));
    
    Background bgSnow = new Background(bgFill);
    Background bgSandyBrown = new Background(bgFill2);
    Background bgWhite = new Background(bgFillWhite);
    Background bgRed = new Background(bgFillRed);
    Background bgGrey = new Background(bgFillGrey);
    Background bgAliceBlue = new Background(bgFillAliceBlue);
    Background bgLightSteelBlue = new Background(bgFillLightSteelBlue);
    Background bgLightBlue = new Background(bgFillLightBlue);
}
