/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author gsb
 */
public class Borders {
    
    //Borders
    private BorderStroke bdStrokeRed = new BorderStroke(Color.RED, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    private BorderStroke bdStrokeWhite = new BorderStroke(Color.WHITE, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    private BorderStroke bdStrokeGreen = new BorderStroke(Color.GREEN, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    private BorderStroke bdStrokeBlack = new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    private BorderStroke bdStrokeLightBlue = new BorderStroke(Color.LIGHTBLUE, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    private BorderStroke bdStrokeAliceBlue = new BorderStroke(Color.ALICEBLUE, new BorderStrokeStyle(StrokeType.CENTERED, 
                    StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, null), new CornerRadii(4), new BorderWidths(1));
    
    Border borderBlack = new Border(bdStrokeBlack);
    Border borderRed = new Border(bdStrokeRed);
    Border borderWhite = new Border(bdStrokeWhite);
    Border borderGreen = new Border(bdStrokeGreen);
    Border borderLightBlue = new Border(bdStrokeLightBlue);
    Border borderAliceBlue = new Border(bdStrokeAliceBlue);
}
