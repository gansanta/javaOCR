/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr3;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author gsb
 */
public class OCR3 extends Application {
    Scene scene;
    HBox root = new HBox();
    double prefSceneHeight = 600;
    double prefSceneWidth = 1200;
    
    ToolBar toolbarLeft;
    ToolBar toolbarRight;
    Text imageText;
    
    //image width and height
    //To be initialized at getHalfSizedCanvasImage() method.
    
    int iWidth, iHeight; 
    
    int blackMinX = 10000;
    int blackMinY = 10000; // top black row
    int blackMaxX = 0;
    int blackMaxY = 0; // bottom black row
    int bwidth = 0;
    int bheight = 0;
    
    CharacterModel cModel = new CharacterModel();
    
    ArrayList<Integer[]> yBlockSize;
    ArrayList<Integer[]> bpBlockList;
    
    String characterKey = "";
//    String text = "";
    
    Color black = Color.color(0,0,0,1);
    
    int blockPositionStored = 0;
    int blockCount=0;
    int blockType = 0;
    
    boolean blockType1 =false;
    boolean blockType2 = false;
    boolean blockType3 = false;
    boolean blockType4 = false;
    boolean blockType5 = false;
    boolean blockType6 = false;
    boolean blockType7 = false;
    boolean blockType8 = false;
    boolean blockType9 = false;
    boolean blockType10 = false;
    boolean blockType11 = false;
    boolean blockType12 = false;
    boolean blockTypeUnknown = false;
    
    boolean paraStart = false;
    boolean needToAddDwetho = false;
    boolean needToAddDoubleDaari = false;
    
    Canvas mainCanvas;
    Image mainImage;
   // HBox imBox = new HBox();
     ScrollBar scrollbar;//scrollbar for image scrolling
    ImageView imView;//It will show the image of the page
    TextArea textArea; // It will show the extracted Text
    
    ArrayList<Integer> minbBlockRowList = new ArrayList<>();
    
    Backgrounds bgs = new Backgrounds();
    Borders borders = new Borders();
    
    @Override
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException {
       
        scene = new Scene(root, prefSceneWidth, prefSceneHeight);
        
        
        
        //A Heading for the image
        imageText = new Text("Image");
        imageText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        //Initialize textArea at the outset, otherwise an ugly nullpointer exception
        //is thrown.
        textArea = new TextArea();
        textArea.appendText("");
        textArea.prefWidthProperty().bind((scene.widthProperty().divide(2)).add(100));
        textArea.prefHeightProperty().bind(scene.heightProperty().subtract(100));
        textArea.setFont(Font.font("Noto Sans Myanmar UI", FontWeight.NORMAL, 20));;
        textArea.setWrapText(true);
//        textArea.setEditable(false);
        
        
        //put that halfImage in the viewport
        imView = new ImageView();
        Rectangle2D viewport = new Rectangle2D(0,0, 700, prefSceneHeight);
        imView.setViewport(viewport);
        
        //-----------scrolling imageView---------------------------------------
        scrollbar = new ScrollBar();
        scrollbar.setOrientation(Orientation.VERTICAL);
        scrollbar.setMin(0);
        
        if(imView.getImage() != null){
            scrollbar.setMax(imView.getImage().getHeight());
            System.out.println("imview height: "+ imView.getImage().getHeight());
            
        } else scrollbar.setMax(0);
        
        scrollbar.maxHeightProperty().bind(scene.heightProperty()
            .subtract(80));
        scrollbar.valueProperty().addListener(
            (observable, oldvalue, newvalue) ->{
                int i = newvalue.intValue();

                imView.setViewport( new Rectangle2D(0, i, 700, prefSceneHeight));
            });
    
        HBox box = new HBox(10, imView, scrollbar);
        box.setPadding(new Insets(20,0,0,20));
        box.setAlignment(Pos.TOP_CENTER);
        //-------------imageView ends-------------------------------------------
        
        //get a toolbar
        toolbarLeft = getToolBarLeft();
        toolbarRight = getToolBarRight();
        
        //get vbleft
        VBox vbleft = new VBox(imageText, toolbarLeft, box);
        vbleft.setBorder(borders.borderLightBlue);
        vbleft.setAlignment(Pos.TOP_CENTER);
        
        //A Heading for the textAREA
        Text textAreaHeader = new Text("Captured Text");
        textAreaHeader.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
              
        //get vbright
        VBox vbright = new VBox(textAreaHeader, toolbarRight, textArea);
        vbright.setBorder(borders.borderLightBlue);
        vbright.setAlignment(Pos.TOP_CENTER);
       
        root.getChildren().addAll(vbleft, vbright);
         
        primaryStage.setTitle("OCR3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void loadImage() throws IOException, SQLException, ClassNotFoundException{
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image file");
        fileChooser.getExtensionFilters().add(
         new ExtensionFilter("Image Files", "*.png", "*.jpg"));
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile == null) return;
        
        System.out.println(selectedFile.getName());
        System.out.println(selectedFile.getAbsolutePath());
        String fileName = "file://"+selectedFile.getAbsolutePath();
        mainImage = new Image(fileName);
        //Image ss = new Image("file:///home/gsb/NetBeansProjects/MyFaFaR3/src/myfafar3/images/zoomin.png");
       
        //Load an image
        //image = new Image(getClass().getResourceAsStream("images/mlines.png"));
        
        mainCanvas = getCanvas(mainImage, false); //grid = false
          
        //get half sized writable image of a canvas containing the above image
        Image halfImage = getHalfSizedCanvasImage(mainCanvas);
        imView.setImage(halfImage);
        scrollbar.setMax(imView.getImage().getHeight());

    }
    
//    public void startTask(){
//        //Create a runnable
//        Runnable task = () -> runTask();
//        
//    }
    
//    public 
    
    public Canvas getCanvas(Image image, boolean showGrid) throws SQLException, ClassNotFoundException{
        
        if(image == null)return null;
        
        iWidth = (int)image.getWidth();
        iHeight = (int)image.getHeight();
        
        //Create a canvas and draw the image on it
        Canvas canvas = new Canvas(iWidth, iHeight);
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
        
        //read the pixels and get the lines and characters in the canvas
        if(showGrid == true)readPixelsInfo(gc, image, canvas, showGrid);
       
        return canvas;
    }
    
    public Image getHalfSizedCanvasImage(Canvas canvas){
        
        //take a snapshot of the whole canvas and turn it to an image
        //All your jobs of finding lines and characters should be done
        //on the canvas before this lines!
        if(canvas == null) return null;
        
        
        WritableImage canvasImage = canvas.snapshot(null, null);
        
        
        
        //Put the canvasImage to an imageView of half Width, preserving 
        //aspect ratio.
        ImageView tempImView = new ImageView(canvasImage);
        tempImView.setFitWidth(700);
        tempImView.setPreserveRatio(true);
        
        //Take a snapshot of the halfSized imageView and return it 
        //for further processing.
        WritableImage halfImageOfImageView = tempImView.snapshot(null, null);
        return halfImageOfImageView;
   }
   
    public ArrayList<Integer[]> getCharacters(int imageW, int lineTop, int lineBottom, 
            Canvas canvas, PixelReader pixelReader){
        
//        System.out.println("\n//----------getCharacters() method starting ...-----");
        //int lineHeight = lineBottomNow - lineTopNow;
        boolean blackColumnOn = false;
        int blackStart = 0;
        int blackEnd = 0;
        //paraStart = false;
        
        ArrayList<Integer[]> charactersXY = new ArrayList<>();
        
//        System.out.println("lineTopNow: "+lineTopNow+", lineHeight: "+lineHeight);
        
        //Read all pixels column by column
        for(int x=0; x<imageW; x++){
            //System.out.println("x = "+x);
            for(int y=lineTop; y<lineBottom; y++){
                
             //   System.out.println("    y = "+y);
                Color color = pixelReader.getColor(x, y);
                
                if(color.equals(black)){
                    //System.out.println("pixel is black at x = "+x);
                    
                    if(!blackColumnOn) {
                        blackColumnOn = true;
//                        System.out.println("character starts at x = "+x);
                        blackStart = x;
                        
                        //Mark paragraph start
                        if(blackStart<100) paraStart = true;
                        
                        
                        break;
                    } 
                    else if(x == imageW-1){
                        blackEnd = x;
                        break;
                    }
                    else break; //continue to next column
                }
                
                else{
//                    System.out.println("pixel is not black at x = "+x);
//                    System.out.println("blackColumnOn = "+blackColumnOn);
//                    
                    if(blackColumnOn && y == lineBottom-1){
                        blackColumnOn = false;
//                        System.out.println("character ends at x = "+x);
                        blackEnd = x-1;
                        
                        Integer[] s = {blackStart, blackEnd};
                        charactersXY.add(s);
                    }
                }
                
                
                
            }//one column of pixels finished!
        }
        
        System.out.println("Found "+charactersXY.size()+" characters");
        for(int i=0; i<charactersXY.size(); i++){
            System.out.println((i+1)+". blackStart: "+charactersXY.get(i)[0]+", blackEnd: "
                    +charactersXY.get(i)[1]+", width: "
                    +(charactersXY.get(i)[1] - charactersXY.get(i)[0]));
        }
        
        
        return charactersXY;
    }
    
    public void readPixelsInfo(GraphicsContext gc, Image image, 
            Canvas canvas, boolean showGrid) throws SQLException, ClassNotFoundException{
        minbBlockRowList.clear();
        
       //Obtain pixelReader from image
        PixelReader pixelReader = image.getPixelReader();
        if(pixelReader == null){
            System.out.println("Error reading pixels from image. Exiting.");
            return;
        }

        //get the number of lines
        ArrayList<Integer[]> myLines = getLines(canvas, image, gc, pixelReader);
        
        System.out.println("rows of minimum black blocks:");
        for(int in = 0; in<minbBlockRowList.size(); in++){
            System.out.println(in+". y = "+minbBlockRowList.get(in));
        }
       
        
        
        //for each line, get the characters--------------------------------------
        for(int c=0; c<myLines.size(); c++){
            
            //Enable or disable the following statement to test a particular line
            //if (c>0) break;
             
            paraStart = false;
            
            ArrayList<Integer[]> chars 
                    = getCharacters(iWidth, minbBlockRowList.get(c), 
                            minbBlockRowList.get(c+1), canvas, pixelReader);
            
//            Add newLine in the beginning of new paragraph
            if(paraStart)textArea.appendText("\n");
            
            for(int i=0; i< chars.size(); i++){
                System.out.println("Line: "+c+", Character: "+i);

                //Add space between characters if found
                if(i>0){
                    int whiteSpaceWidth = chars.get(i)[0]-chars.get(i-1)[1];
                    if(whiteSpaceWidth > 20) textArea.appendText(" ");
                } 
                
                //Now identify characters
                String abortHint = identifyCharacters(chars.get(i)[0], chars.get(i)[1], 
                        minbBlockRowList.get(c), minbBlockRowList.get(c+1), 
                        pixelReader, gc, canvas, textArea);
                if(abortHint.equals("abort")) return;
                System.out.println("------------------------------");
            }
            
             textArea.appendText(" ");
        }
        
        
    }
    
    /*
    public void readPixelsInfo(GraphicsContext gc, Image image, 
            Canvas canvas, boolean showGrid) throws SQLException, ClassNotFoundException{
        minbBlockRowList.clear();
        
       //Obtain pixelReader from image
        PixelReader pixelReader = image.getPixelReader();
        if(pixelReader == null){
            System.out.println("Error reading pixels from image. Exiting.");
            return;
        }

        //get the number of lines
        ArrayList<Integer[]> myLines = getLines(canvas, image, gc, pixelReader);
        
        System.out.println("rows of minimum black blocks:");
        for(int in = 0; in<minbBlockRowList.size(); in++){
            System.out.println(in+". y = "+minbBlockRowList.get(in));
        }
       
        
        
        //for each line, get the characters--------------------------------------
        for(int c=0; c<myLines.size(); c++){
            
            //Enable or disable the following statement to test a particular line
            //if (c>0) break;
             
            paraStart = false;
            
            ArrayList<Integer[]> chars 
                    = getCharacters(iWidth, minbBlockRowList.get(c), 
                            minbBlockRowList.get(c+1), canvas, pixelReader);
            
            //Add newLine in the beginning of new paragraph
            if(paraStart)textArea.appendText("\n");
            
            for(int i=0; i< chars.size(); i++){
                System.out.println("Line: "+c+", Character: "+i);

                //Add space between characters if found
                if(i>0){
                    int whiteSpaceWidth = chars.get(i)[0]-chars.get(i-1)[1];
                    if(whiteSpaceWidth > 20) textArea.appendText(" ");
                } 
                
                //Now identify characters
                String abortHint = identifyCharacters(chars.get(i)[0], chars.get(i)[1], 
                        minbBlockRowList.get(c), minbBlockRowList.get(c+1), 
                        pixelReader, gc, canvas, textArea);
                if(abortHint.equals("abort")) return;
                System.out.println("------------------------------");
            }
            
             textArea.appendText(" ");
        }
        
        
    }
   
    */
    
    public String identifyCharacters(int bMinX, int bMaxX, int bMinY, int bMaxY, 
            PixelReader pixelReader, GraphicsContext gc,
            Canvas canvas, TextArea textArea) throws SQLException, ClassNotFoundException{
        
//        System.out.println("\n//----------identifyCharacters() method starting ...-----");
//        System.out.println("bMin: "+bMin);
        blackMinY = 10000;
        blackMaxY = 0;
        
        
        
        boolean wBlockOn = false;
        int wBlockCount = 0;
        
        // to store y value of the beginning and end of white blocks
        int wBlockStart = 0;
        int wBlockEnd = 0;
        
        ArrayList<Integer> wBlockStartList = new ArrayList<>();
        ArrayList<Integer> wBlockEndList = new ArrayList<>();
        
        //Read all pixels row by row
        for(int y=bMinY; y<bMaxY; y++){
            
            
           for(int x=bMinX; x<=bMaxX; x++){
               
                Color color = pixelReader.getColor(x, y);
                
                if(color.equals(black)){
                    
                    //To eliminate unnecessary black spots on the top and bottom side 
                    //of the line, we need to configure blackMiny and blackMaxY so that
                    // if there are more than 15 rows without any black block, then 
                    //we can safely assume that it is safe to exclude that unnecessary part.
                    if(wBlockOn) {
//                        System.out.println("WBlock ended");
                        wBlockOn = false;
//                        System.out.println("wBlockCount: "+wBlockCount);
                        if(wBlockCount > 20) {
                            
                            wBlockStartList.add(wBlockStart);
                            wBlockEndList.add(y);
                            
                            if(y<(bMinY+(bMaxY-bMinY)/2))blackMinY = y;
                            
                            wBlockCount = 0;
                            
                        }
                        break;
                    }
                    
                    //if(x < blackMinX) blackMinX = x;
                    //if(x > blackMaxX) blackMaxX = x;
                    
                    if(y < blackMinY) blackMinY = y;
                    if(y > blackMaxY) {
                        blackMaxY = y;
//                        System.out.println("blackMaxY set at y="+blackMaxY);
                    }
                    break;
                }
                else if(x == bMaxX){
                    if(!wBlockOn ){
                        
                        wBlockOn = true;
                        wBlockStart = y;
                        wBlockCount++;
                    }
                    else wBlockCount++;
                }
            }//one row of pixels finished!
        }
        
        //setting blackMaxY
        if(wBlockStartList.size()>0){
            wBlockStartList.forEach(a -> {
                
                if(a>= (bMinY+(bMaxY-bMinY)/2) && a <= bMaxY){
                    
                    blackMaxY = a;
                    System.out.println("blackMaxY set at y="+a);
                }
                    
                
            });
        }
        
        blackMinX = bMinX;
        blackMaxX = bMaxX;
//        System.out.println("blackMinX= "+blackMinX);
//        System.out.println("blackMinY= "+blackMinY);
//        System.out.println("blackMaxX= "+blackMaxX);
//        System.out.println("blackMaxY= "+blackMaxY);
//      
        int cWidth = blackMaxX - blackMinX;
        int cHeight = blackMaxY - blackMinY;
        
        //System output the info of characer width and height
        System.out.println("cWidth: "+cWidth+", cHeight: "+cHeight);
        
        String aspectRatio = String.format("%.5g%n",(double)cWidth/cHeight);
        System.out.println("Aspect Ratio: "+aspectRatio);
        
        //Get a surrounding red rectangle
        gc.setStroke(Color.RED);
            gc.strokeRect(blackMinX-1, blackMinY-1,
                    blackMaxX - blackMinX+1, blackMaxY - blackMinY+1);
            
        //update imageView after drawing red boundaries on each character
        this.updateImView(canvas);
            
        //take a snapshot of the character to be used in the dialog
        //for adding new character to database if needed
        WritableImage charSnapShot;
        if(blackMinX-2 >= 0 && blackMinY-2 >= 0 &&
                blackMaxX-blackMinX+2 >=0 && blackMaxY-blackMinY+2 >= 0){
            SnapshotParameters params = new SnapshotParameters();
            Rectangle2D viewport = new Rectangle2D(blackMinX-2,blackMinY-2, 
                blackMaxX-blackMinX+2, blackMaxY-blackMinY+2);
            params.setViewport(viewport);
             charSnapShot = canvas.snapshot(params, null);
        }
        else charSnapShot = null;
            
        characterKey = "";
        //get the number of black pixels in the blocks
        getBlackBlocks(bMinX, bMaxX, bMaxY, pixelReader);
//        
        System.out.println("Image characterKey : "+characterKey);
        
        //Verify character by comparing characterKey from database
        String cc;
        if(charSnapShot != null){
            cc = cModel.verifyCharacter(bwidth, bheight, 
                charSnapShot, characterKey, textArea);
            if("abort".equals(cc)) return cc;
        }
        else cc = "";
        
        System.out.println("character: "+cc);
         System.out.println("cModel dwetho: "+cModel.dwethoAtTheEnd);
        System.out.println("needToAddDwetho now: "+needToAddDwetho);
        
        //handle dwetho problem separately
        this.handleDwethoProblem(cc);
        
        
        //Resetting needToAddDwetho for the next character
        needToAddDwetho = cModel.dwethoAtTheEnd;
        needToAddDoubleDaari = cModel.daariAtTheEnd;
       
        return "";
//    System.out.println("//----------identifyCharacters() method ended ...-----\n");
    }

    /*
    public ArrayList<Integer[]> getLines(Canvas canvas, Image image, GraphicsContext gc,
            PixelReader pixelReader) throws SQLException, ClassNotFoundException{
        
        //ArrayList black
        boolean blackBlockOn = false;
//        int blackStart = 0;
//        int blackEnd = 0;
        int blackBlockCount = 0;
        
        boolean firstBlockOn = false;
//        boolean smallLineOn = false;
//        int smallLines = 0;
//        int smallLineTop = 0;
//        int smallLineBottom = 0;
//        int firstBlockX = 0;
        int lastBlockX = 0;
        
        int lineGapCount = 0; // used to count lines from the bottom of a full line
        
//        int minbBlockRow = 0;
//        int minbBlock = 10000;
//        
//        int[] rowsWithbBlocks = new int[iHeight];
        boolean lineOn = false;
        int lineTopNow = 0; int lineBottomNow = 0; 
        int lineTopBefore = 0; int lineBottomBefore = 0;
        ArrayList<Integer[]> lines = new ArrayList<>();
        
        //Read all pixels row by row
        //------------------------------------y loop starts---------------------
        for(int y=0; y<iHeight; y++){
            blackBlockCount = 0;
            
            firstBlockOn =false;
//            lastBlockOn =false;
//            firstBlockX = 0;
            lastBlockX = 0;
            
            //------------------------------------x loop starts-----------------
            for(int x=0; x<iWidth; x++){
                
                Color color = pixelReader.getColor(x, y);
                
                if(color.equals(black)){
                    
                    if(!blackBlockOn) { // after a white block
                        blackBlockOn = true;
                        blackBlockCount++;
                        
                        if(!firstBlockOn){
                            firstBlockOn = true;
//                            firstBlockX = x;
                            lastBlockX = x;
                        }
                        //if first block on
                        else{
                            if(x>lastBlockX) lastBlockX = x;
                        }
//                        System.out.println("bakck Column starts at x = "+x);
                        //blackStart = x;
                    } 
                    else if(x == iWidth-1){
                        //end of black column at the endX
//                        blackEnd = x;
                        blackBlockCount++;
                        blackBlockOn = false;
                    }
                }
                
                else{ //if pixel is white
                    if(blackBlockOn){
//                        System.out.println("blackBlock ends at "+x);
//                        blackEnd = x;
                        blackBlockOn = false; // end of black block
                        //blackBlockCount++;
//                        System.out.println("blackBlock: "+blackBlockCount);
                    }
                }
            }//------------------------------------x loop ends-----------------    
            
            //processing 
             if(blackBlockCount>=20){
//                System.out.println("    Entered into blackBlockCount >= 20 at y="+y);
                if(!lineOn) {
                    lineOn = true;
                    lineTopNow = y;
                    lineGapCount = 0;
                }
            }
             else{
//                System.out.println("    Not blackBlockCount >= 20 at y="+y);
                
                if(lineOn){
                    lineOn = false;
                    lineBottomNow = y;
//                    System.out.println("    line on.");
                    
//                    System.out.println("line height: "+(lineBottomNow-lineTopNow));
                    if(lineBottomNow-lineTopNow >= 15){
                        
                        if(lineTopNow-lineBottomBefore > 150){
                            System.out.println("small line found after line"+(lines.size()-1));
                            System.out.println("smallLines Top will be: "+(lineBottomBefore+60));
                            System.out.println("smallLines Bottom will be: "+(lineBottomBefore+100));
                            Integer[] l = {(lineBottomBefore+60), lineBottomBefore+100};
                            lines.add(l);
                        }
                        
                        Integer[] l = {lineTopNow, lineBottomNow};
                        
                        lines.add(l);
                        
                        //After adding store them for the next line
                        lineTopBefore = lineTopNow;
                        lineBottomBefore = lineBottomNow;
                        
                        lineTopNow = 0;
                        lineBottomNow = 0;
                        
//                        System.out.println("    line added.");
                        lineGapCount++;
                    }
                }
               
            }
            
        }//------------------------------------y loop ends-----------------
        
        
        //Let's try to find undetected smaller lines
        
        //create a working copy of lines and add found lines there in that copy.
        ArrayList<Integer[]> lines2 = lines;
        
        
        
        for(int i=0; i<lines.size(); i++){
            System.out.println("line "+i+". Top: "+lines.get(i)[0]
                    +", Bottom: "+lines.get(i)[1]);
            
            if(i == lines.size()-1){
                int lineGap = iHeight - lines.get(i)[1]; 
                
                if(lineGap>150){
                    System.out.println("line found!");
                    Integer[] l = {(lines.get(i)[1]+60), (lines.get(i)[1]+100)};
                    lines.add(l);
                }
            }
            
           
            //Get a surrounding blue rectangle for minimum lineHeight
            gc.setStroke(Color.BLUE);
            gc.strokeRect(0, lines.get(i)[0],
                     iWidth+1, lines.get(i)[1] - lines.get(i)[0]+1);
            
            //blueR.setFill(null);
            //root.getChildren().add(blueR);
        }
        
        //----------------Smaller lines detection ------------------------------
        System.out.println("----------------Detecting smaller lines--------------------------");
        

          //Detect smaller lines
        
        
        
//        for(int i=0; i<lines.size();i++){
//            if((i<lines.size()-1) && (lines.get(i+1)[0]-lines.get(i)[1])>50)
//        }
        //-----------------------------Smaller line detection ends -------------
//        
        //update imageView with recently deteted lines
//        mainImage = image;
        this.updateImView(canvas);
            
        return lines;     
    }
    
    
    */
    
    public ArrayList<Integer[]> getLines(Canvas canvas, Image image, GraphicsContext gc,
            PixelReader pixelReader) throws SQLException, ClassNotFoundException{
        
        //ArrayList black
        boolean blackBlockOn = false;
        int blackStart = 0;
        int blackEnd = 0;
        int blackBlockCount = 0;
        
        boolean firstBlockOn = false;
        boolean smallLineOn = false;
        int smallLines = 0;
        int smallLineTop = 0;
        int smallLineBottom = 0;
        int firstBlockX = 0;
        int lastBlockX = 0;
        
        int lineGapCount = 0; // used to count lines from the bottom of a full line
        
        int minbBlockRow = 0;
        int minbBlock = 10000;
        
        int[] rowsWithbBlocks = new int[iHeight];
        boolean lineOn = false;
        int lineTopNow = 0; int lineBottomNow = 0; 
        ArrayList<Integer[]> lines = new ArrayList<>();
        
        //Read all pixels row by row
        for(int y=0; y<iHeight; y++){
            blackBlockCount = 0;
            
            firstBlockOn =false;
//            lastBlockOn =false;
            firstBlockX = 0;
            lastBlockX = 0;
            
            for(int x=0; x<iWidth; x++){
                
                Color color = pixelReader.getColor(x, y);
                
                if(color.equals(black)){
                    
                    if(!blackBlockOn) { // after a white block
                        blackBlockOn = true;
                        blackBlockCount++;
                        
                        if(!firstBlockOn){
                            firstBlockOn = true;
                            firstBlockX = x;
                            lastBlockX = x;
                        }
                        //if first block on
                        else{
                            if(x>lastBlockX) lastBlockX = x;
                        }
//                        System.out.println("bakck Column starts at x = "+x);
                        //blackStart = x;
                    } 
                    else if(x == iWidth-1){
                        //end of black column at the endX
                        blackEnd = x;
                        blackBlockCount++;
                        blackBlockOn = false;
                    }
                }
                
                else{ //if pixel is white
                    if(blackBlockOn){
//                        System.out.println("blackBlock ends at "+x);
                        blackEnd = x;
                        blackBlockOn = false; // end of black block
                        //blackBlockCount++;
//                        System.out.println("blackBlock: "+blackBlockCount);
                    }
                }
            }//for x ends; one row of pixels finished!
            
            rowsWithbBlocks[y] = blackBlockCount;
            
//            if(blackBlockCount<15 && blackBlockCount < minbBlock){
//                minbBlock = blackBlockCount;
//                minbBlockRow = y;
//            }
            
            
            
            if(blackBlockCount>=20){
//                System.out.println("    Entered into blackBlockCount >= 20 at y="+y);
                
                if(!lineOn) {
                    lineOn = true;
                    lineTopNow = y;
                    
                    lineGapCount = 0;
                    
                }
            }
            
            else{
//                System.out.println("    Not blackBlockCount >= 20 at y="+y);
                
                if(lineOn){
                    lineOn = false;
                    lineBottomNow = y;
//                    System.out.println("    line on.");
                    
//                    System.out.println("line height: "+(lineBottomNow-lineTopNow));
                    if(lineBottomNow-lineTopNow >= 15){
                        
                        Integer[] l = {lineTopNow, lineBottomNow};
                        lines.add(l);
//                        System.out.println("    line added.");
                        lineGapCount++;
//                        System.out.println("minbBlock: "+minbBlock+" in line: y="+minbBlockRow);
                        if(minbBlockRow == 0) minbBlockRow = smallLineBottom+30;
                        minbBlockRowList.add(minbBlockRow);
                        minbBlock = 10000;
                        minbBlockRow = 0;
                    }
                }
                else{
//                    System.out.println("    line not on.");
                }
            }
            
            if(blackBlockCount<20){
//                System.out.println("Entering blackBlockCount < 20 at y = "+y);
                lineGapCount++;
                
//                System.out.println("lineGap: "+lineGapCount+"blackBlockCount: "+blackBlockCount+", "
//                        + "minbBlock: "+minbBlock+", minbBlockRow: "+minbBlockRow);
                
                if((lineGapCount<= 60 && blackBlockCount<10 && blackBlockCount < minbBlock)){
//                    System.out.println("minbBlock will be set.");
                    
                    minbBlock = blackBlockCount;
                    minbBlockRow = y;
                }
                if(lineGapCount>100 && blackBlockCount<10 && 
                        blackBlockCount < minbBlock && y<smallLineTop) {
                    minbBlock = blackBlockCount;
                    minbBlockRow = y;
                }
                
                //put a for loop to detect short left-aligned sentence.
//                System.out.println("y = "+y+", "+"lastBlock: "+lastBlockX+", "
//                        + "firstBlock: "+firstBlockX+", width = "+(lastBlockX - firstBlockX));
                if(lineGapCount > 60 && (lastBlockX - firstBlockX)>20 && 
                        (lastBlockX - firstBlockX)< 400 && firstBlockX<300 && lastBlockX<700){
//                    System.out.println("fits as small line.");
//                    System.out.println("        fit as small line");
//                    System.out.println("        lineGap: "+lineGapCount);
//                    System.out.println("        y = "+y+", "+"lastBlock: "+lastBlockX+", "
//                        + "firstBlock: "+firstBlockX+", width = "+(lastBlockX - firstBlockX));
                    if(smallLineOn){
                        smallLines++;
//                        System.out.println("        small line counting.");
                    }
                    else{
                        smallLineOn = true;
                        smallLineTop = y;
                        smallLines++;
//                        System.out.println("  small line starting new...");
                    }
                }
                
                else {
//                    System.out.println("  not fit as small line. small line: "+smallLines);
//                    System.out.println("  lineGap: "+lineGapCount);
//                    System.out.println("  y = "+y+", "+"lastBlock: "+lastBlockX+", "
//                        + "firstBlock: "+firstBlockX+", width = "+(lastBlockX - firstBlockX));
                    if(smallLineOn){
                        smallLineOn = false;
                        smallLineBottom = y;
                        
                        if(lineGapCount>100) {
                            smallLineBottom = smallLineTop + 40;
                            
                        }
//                        System.out.println("    Small line number: "+smallLines);
                        
                        if(smallLines >= 50){

                            Integer[] l = {smallLineTop, smallLineBottom};
                            lines.add(l);
//                            System.out.println("small line added.");
                            
//                            System.out.println("minbBlock: "+minbBlock+" in line: y="+minbBlockRow);
                            if(minbBlockRow == 0) minbBlockRow = smallLineBottom+30;
                            minbBlockRowList.add(minbBlockRow);
                            minbBlock = 10000;
                            minbBlockRow = 0;
                        }else{
//                            System.out.println("small line below 50. so not added.");
                        }
                        
                        if(lineGapCount>100){
                            minbBlock = 10000;
                            minbBlockRow = 0;
                        }
                        smallLines = 0;
                        
                    }
                    
                    else {
                        smallLines = 0;
                    }
                }
                
            }
            
            
            if(y==iHeight-1){
//                System.out.println("minbBlock: "+minbBlock+" in line: y="+minbBlockRow);
                if(minbBlockRow == 0) minbBlockRow = smallLineBottom+30;
                minbBlockRowList.add(minbBlockRow);
                minbBlock = 10000;
                minbBlockRow = 0;
            }
            
        }//for y ends
        
        for(int i=0; i<lines.size(); i++){
            System.out.println("line "+i+". Top: "+lines.get(i)[0]
                    +", Bottom: "+lines.get(i)[1]);
            
            //Get a surrounding blue rectangle for minimum lineHeight
            gc.setStroke(Color.BLUE);
           gc.strokeRect(0, lines.get(i)[0],
                    iWidth+1, lines.get(i)[1] - lines.get(i)[0]+1);
            
            //blueR.setFill(null);
            //root.getChildren().add(blueR);
        }
        
        //update imageView with recently deteted lines
//        mainImage = image;
        this.updateImView(canvas);
            
        return lines;     
    }
    
   
    
    
    public void getBlackBlocks(int bMin, int bMax, int height, PixelReader pixelReader){
        
//        System.out.println("\n//----------getBlackBlocks() method starting ...-----");
        
        bwidth = blackMaxX - blackMinX;
        bheight = blackMaxY - blackMinY;
        
        int bblock = 0, bblockStart = 0, wblock = 0;
        boolean bblockOn = false, wblockOn = false;
        
        
        int w0= blackMinX, w1 = blackMinX + bwidth/4, w2 = blackMinX+bwidth/2, 
                w3 = blackMinX+3*bwidth/4, w4 = blackMinX+bwidth;
        int h0=blackMinY, h1 = blackMinY + bheight/4, h2 = blackMinY+bheight/2, 
                h3 = blackMinY+3*bheight/4, h4 = blackMinY+bheight;
        
        int ww0 = blackMinX, wwl = blackMinX + bwidth/3, wwc = blackMinX+(bwidth*2)/3, 
                wwr = blackMinX+bwidth;
//        int hh0 = blackMinX, hhl = blackMinX + bwidth/3, wwc = blackMinX+(bwidth*2)/3, 
//                wwr = blackMinX+bwidth;
//        
//        System.out.println("ww0: "+ww0+", wwl: "+wwl+", wwc: "+wwc+", wwr: "+wwr);
//        System.out.println("h0: "+blackMinY+", h1: "+h1+", h2: "+h2+", h3: "+h3+", h4: "+h4);
        
        bpBlockList = new ArrayList<>(); // to store black blocks<>
        
        int x=0,y=0;
        
        yBlockSize = new ArrayList<>();
        
     
        blockType1 =false;
        blockType2 = false;
        blockType3 = false;
        blockType4 =false;
        blockType5 = false;
        blockType6 = false;
        blockType7 =false;
        blockType8 = false;
        blockType9 = false;
        blockType10 =false;
        blockType11 = false;
        blockType12 = false;
        blockTypeUnknown = false;
        
      
        blockType = 0;
        blockPositionStored = 0;
        blockCount = 0;
        
        for(y=0; y<height; y++){
            
//            System.out.println("\n y="+y);
            bpBlockList.clear();
            bblockOn = false;
            wblockOn = false;
            
            for(x=bMin; x<bMax; x++){
                
                Color color = pixelReader.getColor(x, y);
                
                //First row 
                if(y>=blackMinY && y<=h4){
                    if(x == blackMinX){
                        
//                        blocks[0]++;
                        if(color.equals(black)){
                            bblockOn = true;
                            bblock=1;
                            bblockStart=x;
                        //    System.out.println("bblock starts at: x="+x
                       //         +"bblockOn="+bblockOn+", wblockOn="+wblockOn);
                        }
                        
                        //if the block is white
                        else{
                            wblockOn = true;
                            wblock=1;
                         //   System.out.println("wblock starts at: x="+x);
                        }
                    }
                    else if(x>blackMinX && x<w4){
                       // System.out.println("working at x="+x);
                       // blocks[0]++;
                        
                        if(color.equals(black)){
                           // System.out.println("pixel: black");
                            if(bblockOn)bblock++;
                            
                            else if(wblockOn){
                                
                                
                                wblockOn = false;
                               // System.out.println(" wblock ended. wblock:"+wblock);
                                
                                
                                bblockOn = true;
                                bblock=1;
                                bblockStart=x;
                                //System.out.println(" bblock starts at: x="+x);
                            }
                        }
                        
                        //else if pixel is not black
                        else {
                          //  System.out.println("pixel: white");
                            if(wblockOn) {
                                wblock++;
                                //System.out.println("white block on, so wblock++");
                            }
                            
                            else if(bblockOn){
                               // System.out.println("black block on. examining..");
                                //ignore maximum 2pixels gap
                                
                                
                                    Color nColor1 = Color.WHITE;
                                    Color nColor2 = Color.WHITE;
                                    if(x+1 < bMax)
                                        nColor1 = pixelReader.getColor(x+1, y);
                                    if(x+2 < bMax)
                                        nColor2 = pixelReader.getColor(x+2, y);
                                    
                                    if(nColor1.equals(black)) {
                                        wblock++;
//                                        System.out.println("ignoring 1 single black pixel");
                                    }
                                    else if(!nColor1.equals(black) &&
                                        nColor2.equals(black)){
                                        wblock++;
//                                        System.out.println("ignoring 2 black pixels");
                                    }
                                
                                
                                
                                else{
                                    bblockOn = false;

//                                    System.out.println(" bblock ended. bblock:"+bblock);
                                    Integer[] ii = {bblock, bblockStart};
                                    if(bblock >=3) {
                                        bpBlockList.add(ii);
//                                        System.out.println(" bblock added to bpBlockList.");
                                    }else{
//                                        System.out.println(" bblock is less than 3, so not added to the list.");
                                    }
                                    bblockStart=0;

                                    wblockOn = true;
                                    wblock = 1;
                                }
                                
                       //         System.out.println(" wblock starts at: x="+x);
                            }
                        }
                    } else if(x == w4){
                        if(wblockOn){
                            wblockOn = false;
                         //   System.out.println(" wblock ended. wblock:"+wblock);
                            wblock = 0;
                        }
                        if(bblockOn){
                            bblockOn = false;
//                            System.out.println(" bblock ended. bblock:"+bblock);
                            Integer[] ii = {bblock, bblockStart};
                            if(bblock >=3) {
                                bpBlockList.add(ii);
//                                System.out.println(" bblock added to bpBlockList.");
                            }else{
//                                System.out.println(" bblock is less than 3, so not added to the list.");
                            }
                            
                            bblock = 0;
                            bblockStart=0;
                        }
                        
                    }
                }
            }
 
 //-------------------------UPPER HALF------------------------------------------           
            if(y>=blackMinY && y<h2) {
              // System.out.println("")
                if(bpBlockList.isEmpty()){
//                    System.out.println("No black block!");
//                    System.out.println("blocks in a row = "+bpBlockList.size());
                    addToBlockInfoList();
                    resetAll();
                }
                
                
                else if(bpBlockList.size() == 1 || 
                        bpBlockList.size() == 2 ||
                        bpBlockList.size() == 3 ||
                        bpBlockList.size() == 4 ||
                        bpBlockList.size() == 5 ||
                        bpBlockList.size() == 6 ||
                        bpBlockList.size() == 7 ||
                        bpBlockList.size() == 8 ||
                        bpBlockList.size() == 9 ||
                        bpBlockList.size() == 10 ||
                        bpBlockList.size() == 11 ||
                        bpBlockList.size() == 12 )
                    bTypePreProcessing(bpBlockList.size(), wwl, wwc, wwr, 0); // 0 means upper part
                
                else{
                     System.out.println("blocks in a row = "+bpBlockList.size());
                    System.out.println("Unhandled blockSize in upper part.");
                    
                    if(blockTypeUnknown){
                        System.out.println("recurring unknown blockType. so counting.");
                        blockCount++;
                    }else defaultActions(0,0,1);
                    
                }
                
            }

 //-------------------------LOWER HALF------------------------------------------
            else if(y>=h2 && y<h4) {
                if(bpBlockList.isEmpty()){
//                    System.out.println("No black block!");
                    addToBlockInfoList();
                    resetAll();
                }
                else if(bpBlockList.size() == 1 || 
                        bpBlockList.size() == 2 ||
                        bpBlockList.size() == 3 ||
                        bpBlockList.size() == 4 ||
                        bpBlockList.size() == 5 ||
                        bpBlockList.size() == 6 || 
                        bpBlockList.size() == 7 ||
                        bpBlockList.size() == 8 ||
                        bpBlockList.size() == 9 ||
                        bpBlockList.size() == 10 ||
                        bpBlockList.size() == 11 ||
                        bpBlockList.size() == 12 )
                bTypePreProcessing(bpBlockList.size(), wwl, wwc, wwr, 1); // 1 means lower part
                
                else{
                     System.out.println("blocks in a row = "+bpBlockList.size());
                    System.out.println("Unhandled blockSize in lower part.");
                    
                    if(blockTypeUnknown){
                        System.out.println("recurring unknown blockType. so counting.");
                        blockCount++;
                    }else defaultActions(0,0,1);
                }

            }//lower half part ended
            
            //
            else if(y == h4){
                //at the end add all continued blocks to the list
                if(blockCount >=1){
                    Integer[] zz = {blockType, blockPositionStored, blockCount};
                    yBlockSize.add(zz);
//                    System.out.println("block added to the list. end of block.");
                    
                    if("".equals(characterKey)) characterKey 
                            = characterKey+blockType+blockPositionStored;
                    else characterKey = characterKey+"-"+blockType+blockPositionStored;
//                    this.printBlockInfoList(yBlockSize);//print info
                }else{
//                    System.out.println("blockCount<3. so not added to the list. \nend of block.");
                }
            }
            
        }//iteration of y ends
        
//        
//        int size = yBlockSize.size();
//        for(int i=0; i<size; i++){
//            System.out.println(i+". blockType: "+yBlockSize.get(i)[0]
//                +", blockPosition: "+yBlockSize.get(i)[1]
//                +", blockCount: "+yBlockSize.get(i)[2]);
//        }
        
        
       // getCharacter(bwidth, bheight, yBlockSize, imlabel);
//    System.out.println("//----------getBlackBlocks() method ended ...-----\n");    
    }
    
    public void bTypePreProcessing(int bpBlockListSize, int wwl, int wwc, int wwr, 
            int vp){ //vp = vertical position, i.e. upper part or lower part
//        System.out.println("blockType="+bpBlockListSize);
        int bTypeNow = bpBlockListSize;
        int bPositionNow = 0;
        
        //Find the present position
        switch (bTypeNow) {
            case 1:
                if(bpBlockList.get(0)[1] < wwl && vp == 0)bPositionNow = 1;
                else if(bpBlockList.get(0)[1] < wwl && vp == 1)bPositionNow = 4;
                
                else if(bpBlockList.get(0)[1]>=wwl && bpBlockList.get(0)[1]<wwc && vp == 0)bPositionNow = 2;
                else if(bpBlockList.get(0)[1]>=wwl && bpBlockList.get(0)[1]<wwc && vp == 1)bPositionNow = 5;
                
                else if(bpBlockList.get(0)[1]>=wwc && bpBlockList.get(0)[1]<=wwr && vp == 0)bPositionNow = 3;
                else if(bpBlockList.get(0)[1]>=wwc && bpBlockList.get(0)[1]<=wwr && vp == 1)bPositionNow = 6;
                
                else bPositionNow = 100;
                break;
            case 2:
                if(bpBlockList.get(0)[1] < wwl &&  bpBlockList.get(1)[1]>=wwl &&
                        bpBlockList.get(1)[1]<wwc && vp == 0)bPositionNow = 1;
                else if(bpBlockList.get(0)[1] < wwl &&  bpBlockList.get(1)[1]>=wwl &&
                        bpBlockList.get(1)[1]<wwc && vp == 1)bPositionNow = 4;
                
                else if(bpBlockList.get(0)[1] < wwl && bpBlockList.get(1)[1]>=wwc &&
                        bpBlockList.get(1)[1]<=wwr && vp == 0)bPositionNow = 2;
                else if(bpBlockList.get(0)[1] < wwl && bpBlockList.get(1)[1]>=wwc &&
                        bpBlockList.get(1)[1]<=wwr && vp == 1)bPositionNow = 5;
                
                else if(bpBlockList.get(0)[1]>=wwl && bpBlockList.get(0)[1]<wwc &&
                        bpBlockList.get(1)[1]>=wwc && bpBlockList.get(1)[1]<=wwr &&
                        vp == 0)bPositionNow = 3;
                else if(bpBlockList.get(0)[1]>=wwl && bpBlockList.get(0)[1]<wwc &&
                        bpBlockList.get(1)[1]>=wwc && bpBlockList.get(1)[1]<=wwr &&
                        vp == 1)bPositionNow = 6;
                else bPositionNow = 200; //error catching
                break;
            case 3:
//                if(bpBlockList.get(0)[1] < wwl &&
//                        bpBlockList.get(1)[1]>=wwl && bpBlockList.get(1)[1]<wwc &&
//                        bpBlockList.get(2)[1]>=wwc && bpBlockList.get(2)[1]<=wwr &&
//                        vp == 0)bPositionNow = 1;
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 4:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 5:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 6:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
             case 7:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 8:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 9:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break; 
            case 10:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 11:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;
            case 12:
                if(vp == 0)bPositionNow = 1;
                else if(vp == 1)bPositionNow = 2;
                break;    
            default:
                bPositionNow = 0;
                break;
        }
        
//        bpBlockList.forEach(ac -> {
//            System.out.println("black block start at: "+ac[1]);
//        });
//        
        bTypePositionProcessing(bTypeNow, bPositionNow);
    }
    
    public void increaseBlockCount(){
//        System.out.println("Stored blockPosition = "+blockPositionStored);
//        System.out.println("Own position. so counting.");
        blockCount++;
    }
    
    public void defaultActions(int bType, int bPosition, int bCount){

//        System.out.println("Stored blockPosition ="+blockPositionStored);
//        System.out.println("Not own position. "
//                + "so start counting anew.");

        addToBlockInfoList();

        switch(bType){
            case 0:
               blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = true;//1
                break;
            case 1:
                blockType1 = true;//1
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
            case 2:
                blockType1 = false;
                blockType2 = true;//2
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
            case 3:
                blockType1 = false;
                blockType2 = false;
                blockType3 = true;//3
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 4:
                 blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = true;//4
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 5:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = true;//5
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 6:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = true;//6
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            
            case 7:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = true;//7
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 8:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = true;//8
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 9:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = true;//9
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 10:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = true;//10
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 11:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = true;//11
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 12:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = true;//12
                blockTypeUnknown = false;
                break;    
                                                            
            default:
                System.out.println("Unknown blockType!"+bType);
                break;
        }

        //blockTypeUnknown = false;
        blockType = bType;
        blockPositionStored=bPosition;
        blockCount = bCount;
    }
   
    public void bTypePositionProcessing(int bTypeNow, int bPositionNow){
//        System.out.println(" blockPosition = "+bPositionNow);
        //processing if the stored blockType is blockTypeOne
        if(blockType1 || blockType2 || blockType3 || blockType4 
                || blockType5 || blockType6 || blockType7 || blockType8 || blockType9 || blockType10 
                || blockType11 || blockType12 ||blockTypeUnknown) {
//            System.out.println("Stored blockType = "+blockType);
            
            if(blockPositionStored == bPositionNow && blockType == bTypeNow)increaseBlockCount();
            else defaultActions(bTypeNow, bPositionNow, 1);
        }
        
        //or else for the first time, so turn blockTypeNow
        else{
//            System.out.println("no stored blockType found."
//                    + "so turning blockType "+bTypeNow+" = true.");
//            System.out.println("and blockPosition = "+bPositionNow);
            switch(bTypeNow){
            case 0:
               blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = true;//1
                break;
            case 1:
                blockType1 = true;//1
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
            case 2:
                blockType1 = false;
                blockType2 = true;//2
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
            case 3:
                blockType1 = false;
                blockType2 = false;
                blockType3 = true;//3
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 4:
                 blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = true;//4
                blockType5 = false;
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 5:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = true;//5
                blockType6 = false;
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;
                
            case 6:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = true;//6
                blockType7 =false;
                blockType8 = false;
                blockType9 = false;
                blockType10 =false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            
            case 7:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = true;//7
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 8:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = true;//8
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 9:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = true;//9
                blockType10 = false;
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 10:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = true;//10
                blockType11 = false;
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 11:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = true;//11
                blockType12 = false;
                blockTypeUnknown = false;
                break;    
            case 12:
                blockType1 = false;
                blockType2 = false;
                blockType3 = false;
                blockType4 = false;
                blockType5 = false;
                blockType6 = false;
                blockType7 = false;
                blockType8 = false;
                blockType9 = false;
                blockType10 = false;
                blockType11 = false;
                blockType12 = true;//12
                blockTypeUnknown = false;
                break;    
                
            default:
                break;
        }
            
            blockType = bTypeNow;
            blockPositionStored=bPositionNow;
            blockCount = 1;
        }
    }
    
    
     public void resetAll(){
        blockType1 =false;
        blockType2 = false;
        blockType3 = false;
        blockType4 = false;
        blockType5 = false;
        blockType6 = false;
        blockType7 = false;
        blockType8 = false;
        blockType9 = false;
        blockType10 = false;
        blockType11 = false;
        blockType12 = false;
        blockTypeUnknown = false;

        blockType = 0;
        blockPositionStored = 0;
        blockCount = 0;
    }
    
    
    public void addToBlockInfoList(){
        if(blockCount >=1){
            Integer[] zz = {blockType, blockPositionStored, blockCount};
            yBlockSize.add(zz);
//            this.printBlockInfoList(yBlockSize);//print info
            if("".equals(characterKey)) characterKey 
                    = characterKey+blockType+blockPositionStored;
            else characterKey = characterKey+"-"+blockType+blockPositionStored;
        }else{
//            System.out.println("blockCount is < 3. "
//                    + "so not adding to the blockInfoList.");
        }
    }
    
    //IT should be called from extractButton, where canvas is not available
    public void updateImView(Image image, boolean showGrid) throws SQLException, ClassNotFoundException{
        //set grid = true to analyze lines and charaacters
            if(showGrid)  mainCanvas = getCanvas(image, true);
            else    mainCanvas = getCanvas(image, false);
      
        //get half sized writable image of a canvas containing the above image
        Image halfImage = getHalfSizedCanvasImage(mainCanvas);
        imView.setImage(halfImage);
        scrollbar.setMax(imView.getImage().getHeight());
    }
    
    //It should be called from getLines() and other methods where canvas is being updated
    public void updateImView(Canvas canvas) throws SQLException, ClassNotFoundException{
        
        mainCanvas = canvas; 
        
        //get half sized writable image of a canvas containing the above image
        Image halfImage = getHalfSizedCanvasImage(mainCanvas);
        imView.setImage(halfImage);
        scrollbar.setMax(imView.getImage().getHeight());
    }
    
    public ToolBar getToolBarLeft(){
        
        ToolBar toolBar = new ToolBar();
        
        //analyzeButton Button
        Button analyzeButton = new Button();
        analyzeButton.setGraphic(new ImageView(
            new Image(getClass().getResourceAsStream("icons/grid16.png"))));
        analyzeButton.setTooltip(new Tooltip("Extract Text"));
        analyzeButton.setText("Extract Text");
        analyzeButton.setPrefWidth(120);
        analyzeButton.setPadding(new Insets(3));
        
        analyzeButton.setOnAction(event -> {
            try {
                
                //start processing image, should be in a background thread
                if(imView.getImage() != null)updateImView(mainImage, true);
                
            
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(OCR3.class.getName()).log(Level.SEVERE, null, ex);
            }
          
            
        });
        

        Separator separator = new Separator();
        separator.setVisible(true);
        separator.prefWidthProperty().bind(scene.widthProperty().divide(2)
                .subtract(analyzeButton.prefWidthProperty()).subtract(200));
        
        //Load image Button
        Button loadButton = new Button();
        loadButton.setGraphic(new ImageView(
            new Image(getClass().getResourceAsStream("icons/load16.png"))));
        loadButton.setTooltip(new Tooltip("Load Image"));
        loadButton.setText("Load Image");
        loadButton.setPrefWidth(130);
        loadButton.setPadding(new Insets(3));
        
        loadButton.setOnAction(event -> {
            try {
                try {
                    loadImage();
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(OCR3.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(OCR3.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
//        // Menu Items for database button
//        MenuItem deleteRowItem = new MenuItem("Delete Row");
//        deleteRowItem.setOnAction(e -> {
//            try {
//                
//               ArrayList<String> ss =  SQLiteManager.getAllTableNamesFromDB();
//               if(!ss.isEmpty())SQLiteManager.deleteRowFromTable(ss);
//               else {
//                    System.out.println("No Table Found in the database!");
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setContentText("No table found in the database!");
//                    alert.showAndWait();
//               }
//               
//            } catch (SQLException | ClassNotFoundException ex) {
//                Logger.getLogger(OCR3.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
//        
//        MenuItem deleteTableItem = new MenuItem("Delete Table");
//        deleteTableItem.setOnAction(e -> {
//            try {
//                SQLiteManager.deleteTableFromDB();
//            } catch (SQLException | ClassNotFoundException ex) {
//                Logger.getLogger(OCR3.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
        
        //database Button
        Button dbButton = new Button();
        dbButton.setOnAction(ac -> {
            DatabaseDialogClass ddClass = new DatabaseDialogClass();
            ddClass.databaseDialog.showAndWait();
        });
        
        dbButton.setGraphic(new ImageView(
            new Image(getClass().getResourceAsStream("icons/gear.png"))));
        dbButton.setTooltip(new Tooltip("Database Operations"));
        dbButton.setText("Databases");
        dbButton.setPrefWidth(150);
        dbButton.setPadding(new Insets(3));
        
        
        toolBar.getItems().addAll(loadButton,analyzeButton, separator, dbButton);
        toolBar.prefWidthProperty().bind(scene.widthProperty().divide(2));
        toolBar.setPadding(new Insets(0, 0, 0, 10));
        toolBar.setBackground(bgs.bgAliceBlue);
        toolBar.setBorder(borders.borderLightBlue);
        toolBar.setCursor(Cursor.DEFAULT);
        
        return toolBar;
    }
     
    public ToolBar getToolBarRight(){
        
        ToolBar toolBar = new ToolBar();
        
        //show Button
        Button saveButton = new Button();
        saveButton.setGraphic(new ImageView(
            new Image(getClass().getResourceAsStream("icons/saveInMyanmar16.png"))));
        saveButton.setTooltip(new Tooltip("Save in Myanmar Pali"));
        saveButton.setText("Save");
        saveButton.setPrefWidth(120);
        saveButton.setPadding(new Insets(3));
        
        
        //hide Button
        Button convertButton = new Button();
        convertButton.setGraphic(new ImageView(
            new Image(getClass().getResourceAsStream("icons/convertToRoman16.png"))));
        convertButton.setTooltip(new Tooltip("Convert to Roman Pali"));
        convertButton.setText("Convert");
        convertButton.setPrefWidth(120);
        convertButton.setPadding(new Insets(3));

        toolBar.getItems().addAll(saveButton, convertButton);
        toolBar.prefWidthProperty().bind(scene.widthProperty().divide(2));
        toolBar.setPadding(new Insets(0, 0, 0, 10));
        toolBar.setBackground(bgs.bgAliceBlue);
        toolBar.setBorder(borders.borderLightBlue);
        toolBar.setCursor(Cursor.DEFAULT);
        
        return toolBar;
    }
    
    
    public void handleDoubleDariProblem(String cc){
        if(cc.endsWith("၊") && this.needToAddDoubleDaari){
            cc = "။";
            textArea.appendText(cc);
        }
        else {
            cc = "၊"+cc;
            textArea.appendText(cc);
        }
        
        
    }
    
    public String handleDwethoProblem(String cc){
        if(needToAddDwetho){
            
            if(cc.length() == 1) {
                cc = cc+"ေ";
            }
            else if(cc.length()==2){
                String stringFirst = cc.substring(0, 1);
                System.out.println("FirstString: "+stringFirst);
                
                String stringLast = cc.substring(1, cc.length());
                System.out.println("LastString: "+stringLast);
                
                //To handle owa, e.g. yoe ရွေ , fwee ဖြေ etc.
                if(stringLast.equals("ွ") || stringLast.equals("ြ")
                        || stringLast.equals("ျ")){
                    cc=stringFirst+stringLast+"ေ";
                }else cc = stringFirst+"ေ"+stringLast;
            }
            
            else if(cc.length()==3){
                String stringFirst = cc.substring(0, 1);
//                System.out.println("FirstString: "+stringFirst);
                
                
                String stringSecond = cc.substring(1, 2);
//                System.out.println("stringSecond: "+stringSecond);
                
                String stringLast = cc.substring(2, cc.length());
//                System.out.println("stringLast: "+stringLast);
                
                if(stringSecond.equals("ွ") || stringSecond.equals("ြ")
                        || stringSecond.equals("ျ")){
                    cc=stringFirst+stringSecond+"ေ"+stringLast;
                }
                
                else if(stringSecond.equals("္")){
                    cc=stringFirst+stringSecond+stringLast+"ေ";
                }
                
                else cc = stringFirst+"ေ"+stringSecond+stringLast;
               
            }
            
            else if(cc.length()>3){
                String stringFirst = cc.substring(0, 1);
//                System.out.println("stringFirst: "+stringFirst);
                
                
                String stringSecond = cc.substring(1, 2);
//                System.out.println("stringSecond: "+stringSecond);
                
                String stringThird = cc.substring(2, 3);
//                System.out.println("stringThird: "+stringThird);
                
                
                String stringLast = cc.substring(3, cc.length());
                System.out.println("stringLast: "+stringLast);
                
                if(stringSecond.equals("ွ") || stringSecond.equals("ြ")
                        || stringSecond.equals("ျ")){
                    cc=stringFirst+stringSecond+"ေ"+stringLast;
                }
                
                else if(stringSecond.equals("္")){
                    cc=stringFirst+stringSecond+stringThird+"ေ"+stringLast;
                }
                
                else cc = stringFirst+"ေ"+stringSecond+stringThird+stringLast;
               
            }
            
            textArea.appendText(cc);
        }
        else textArea.appendText(cc);
        
        return cc;
    }
                
    
}
