/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 * 
 * Execution: java PlaySheetMusic 
 *            java PlaySheetMusic imageFile.ext
 *            java PlaySheetMusic imageFile.ext Sharp note
 *            java PlaySheetMusic imageFile.ext Flat note
 *            java PlaySheetMusic imageFile.ext Sharp note1 Flat note2...
 * 
 * Recommended Executions:
 * java PlaySheetMusic
 * java PlaySheetMusic music_yankee.jpg Sharp F
 * java PlaySheetMusic music_ode.jpg Flat B
 *
 * Description: Plays sheet music (image). Sheet music must be only treble 
 * cleft: notes low D to high G. Only reads quarter notes and sometimes 
 * recognizes half notes. User can optionally input any sharps or flats as
 * arguments to program.
 * Also prints/draws sequence of notes that have been read by program. 
 * Chords printed as note1/note2/...
 */

public class PlaySheetMusic {
    
    private static final int GRAY_THRESHOLD = 8224160;//8553100; //8224160; //upper bound
//    private static final int BLUE = 2765471; //for debugging   
    private static final double LINE_THRESHOLD = 0.5;
    
    private static LinkedList<Note> notes = new LinkedList<Note>();
    private static int[][] image;    
    private static int rows, cols;
    
    private static BlackLine[] blackLines = new BlackLine[1000];
    private static int numBlackLines = 0;
    
    private static Staff[] staffs = new Staff[200];
    private static int numStaffs = 0;
    
    private static int textX;
    private static int textY;
    
    //reads image and changes notes to sharps/flats depending on strings 
    //inputted as arguments
    public static void main(String[] args) {
            
        String filename; 
        double scaleSize = 1;
        
        int length = args.length;
        if (length > 0) {
            filename = args[0];
        }
        else {
            filename = "my_yankee.png"; 
            QuarterNote.setSharp("F");
            HalfNote.setSharp("F");
        }
        
        //assign any inputted sharps/flats to Note classes
        if (length > 1) {
            for (int i = 1; i < length; i += 2) {
                if (args[i].equals("Sharp")) {
                    QuarterNote.setSharp(args[i + 1]);
                    HalfNote.setSharp(args[i + 1]);
                }
                else if (args[i].equals("Flat")) {
                    QuarterNote.setFlat(args[i + 1]);
                    HalfNote.setFlat(args[i + 1]);
                }
            }
        }
        
        //load image data
        image = ImageData.load(filename);
        rows = image.length;
        cols = image[0].length;
//        System.out.println("rows: " + rows + " cols: " + cols); 
        
        //PennDraw figure settings
        PennDraw.setCanvasSize(cols, rows);
        int xSize = (int) (cols * scaleSize);
        int ySize = (int) (rows * scaleSize);
        PennDraw.setXscale(0, xSize);
        PennDraw.setYscale(0, ySize);
        
        //PennDraw font settings
        PennDraw.setFontBold();
        textX = cols - 150;
        textY = rows - 40;
                
        if (image.length == 0) {
            throw new IllegalArgumentException("inputted image file does " +
                                               "not exist");
        }
        
        //initialize line objects to contain image and correct variable values
        BlackLine.initialize(image, GRAY_THRESHOLD);
        BlankLine.initialize(image, GRAY_THRESHOLD);
    
        makeStaffs();
        setStaffLim(filename);        
        storeNotes();
        
        //draw staff objects and notes, and scale figure size by scaleSize
        drawNotes(scaleSize);
        playNotes();
    }
    
    /**
     * play notes
     */
    private static void playNotes() {
        
        Note currNote;
        ListIterator<Note> iter = notes.listIterator();
        
        while(iter.hasNext()) {
            currNote = iter.next();
            currNote.play();
        }
    }
    
    /**
     * draw staffs and notes on top of staffs
     * @param scalar by which to size image (e.g. x2, x0.5)
     */
    private static void drawNotes(double scaleSize) {
        
        drawStaffs(scaleSize);
        
        Note currNote;
        ListIterator<Note> iter = notes.listIterator();
        
        while(iter.hasNext()) {
            currNote = iter.next();
            currNote.draw();
            System.out.print(currNote.getNoteString() + " ");
        }
        System.out.println();
    }
    
    /**
     * reads and stores all (perceived) notes in image 
     * and saves them into linked list: notes
     */
    private static void storeNotes() {

        for (int i = 0; i < numStaffs; i++) { 
            staffs[i].read(notes);
            staffs[i].next();
        }
    }
        
    /**
     * draw staffs (lines drawn in black, blank lines drawn in white)
     * @param scalar by which to size image (e.g. x2, x0.5)
     */
    private static void drawStaffs(double scaleSize) {
        
        PennDraw.setCanvasSize(cols, rows);
        PennDraw.clear(PennDraw.WHITE); // set canvas color
        PennDraw.setPenColor(PennDraw.BLACK);
        
        int xSize = (int) (cols * scaleSize);
        int ySize = (int) (rows * scaleSize);
        PennDraw.setCanvasSize(xSize, ySize); 
        PennDraw.setXscale(0, cols);
        PennDraw.setYscale(rows, 0); //invert image vertically
       
        for (int i = 0; i < numStaffs; i++) {
            staffs[i].draw();
        }
    }  
      
    /**
     * prompts user to choose staff's start and end x limits
     */
    private static void setStaffLim(String filename) {

        int startX, endX, staffNum;
        String text;
        
        // execute as long as window is open
        for (int i = 0; i < numStaffs; i++) {
            
            staffNum = i + 1;
            startX = 0;
            endX = 0;
            
            drawAndPause(filename); //display sheet music and pause for 500 ms
            text = "Click before the first note on staff " + staffNum;
            PennDraw.text(textX, textY, text); //prompt user
//            System.out.println("Click before the first note on staff " +
//                               staffNum);
            
            while (startX == 0) {
                if (PennDraw.mousePressed()) {
                    // get the coordinates of the mouse cursor
                    int x = (int) PennDraw.mouseX();
                    startX = x;
                }
            }
            
            drawAndPause(filename); //display sheet music and pause for 500 ms
            text = "Click after the last note on staff " + staffNum;
            PennDraw.text(textX, textY, text); //prompt user
//            System.out.println("Click after the last note on staff " + 
//                               staffNum);
            
            while (endX == 0) {
                if (PennDraw.mousePressed()) {
                    // get the coordinates of the mouse cursor
                    int x = (int) PennDraw.mouseX();
                    endX = x;
                }
            }
            staffs[i].setXlim(startX, endX);
        }
    }
    
    /**
     * creates staff objects by finding all black lines in sheet music
     * and grouping to together into groups of 5
     * calls groupLines() method to group the lines
     */
    public static void makeStaffs() {
                
        int rows = image.length; 
        int cols = image[0].length;
        
        boolean prevBlackRow = false; //true if last pixel row was a line
        boolean beforeLine = true; //haven't reach 1st black col of 1st line
        boolean blankGap = false; //don't make new staffs until false
        
        //temporary xStart and xEnd
        int maybeXStart = 0;
        int maybeXEnd = 0;
        
        int yStart = 0; //starting y coordinate of blackLine
        int xStart = Integer.MAX_VALUE; //starting x coordinate of blackLine
        int xEnd = Integer.MIN_VALUE;
        
        BlackLine newLine; //each new line to be added
        
        //loop through pixels to find consistent black values across columns of 
        //each row (lines)    
        for (int i = 0; i < rows; i++) {
            int numBlack = 0;
//            System.out.println(image[i][200]);
            for (int j = 0; j < cols; j++) {
                if (image[i][j] < GRAY_THRESHOLD) {
                    numBlack++;
                    if (beforeLine) { //if first black pixel found
                        maybeXStart = j;
                        beforeLine = false;
                    }
                }
                else if (!beforeLine) { //if blank and not before line
                    maybeXEnd = j;
                }
            }
//            System.out.println(i + " " + numBlack / (double) cols);
            if ((numBlack / (double) cols) > LINE_THRESHOLD) { //if a black row
                if (maybeXStart < xStart) {
                        xStart = maybeXStart; //assign xStart for staffs
                    }
                if (maybeXEnd > xEnd) {
                        xEnd = maybeXEnd; //assign xEnd for staffs
                    }
                if (prevBlackRow == false) { //if last row was a blank row
                    yStart = i;  
                }
                prevBlackRow = true;
                blankGap = false;
                beforeLine = true;
            }
            else {                          //if not a blank row
                if (prevBlackRow == true) { //if previous was a black row
                    //new BlackLine object
                    newLine = new BlackLine(yStart, i - 1); 
                    blackLines[numBlackLines++] = newLine;
                }
                prevBlackRow = false;
                
                //if 5th black line has been created
                if (numBlackLines % 5 == 0 && numBlackLines != 0 && 
                    !blankGap) { //if haven't already done so
//                    System.out.println(xStart + " " + xEnd);
                    groupLines(numBlackLines - 5, xStart, xEnd); //make staff
                    //start looking for xStart and xEnd again for next staff
                    xStart = Integer.MAX_VALUE; 
                    xEnd = Integer.MIN_VALUE;
                    blankGap = true;
                }
            }
        }        
    }  
    
    /**
     * draws image and pauses while mouse is still clicked
     * @param filename of image
     */
    private static void drawAndPause(String filename) {
        
        PennDraw.picture(cols / 2, rows / 2, filename);
        while (PennDraw.mousePressed()) {}
    }
    
    /**
     * group 5 black lines and make a staff object
     * called by makeStaffs()
     * @param startIdx: index of first black line (of 5) in blackLines array
     *        xStart: desired starting x (col) index of staff to be made
     *        xEnd: desired ending x (col) index of staff to be made 
     */
    public static void groupLines(int startIdx, int xStart, int xEnd) {

        Staff newStaff = new Staff(blackLines, startIdx, xStart, xEnd);
        staffs[numStaffs++] = newStaff;
    }
    
//    /**
//     * visualize one particular column of the image, stretched to size of
//     * image (uses int[][] image already read in by program)
//     * @param column of image to be visualized 
//     */
//    private static void visualizeColumn(int col) {
//        
//        int[][] stretchColArray = new int[rows][cols];
//        
//        for (int i = 0; i < rows; i++) { //for each row    
//            //current column color
//            int currColor = image[i][col];
////            System.out.println(currColor);
//            
//            if (currColor < 8600000) {
//                colorRow(stretchColArray, i, currColor);
//            }
//            else {
//                colorRow(stretchColArray, i, BLUE);
//            }
//        }
////        ImageData.show(stretchColArray);
//    }

//    /**
//     * visualize given color value, stretched to size of image 
//     * (uses int[][] image already read in by program)
//     * @param integer color value
//     */
//    private static void visualizeColor(int color) {
//        
//        int[][] oneColorArray = new int[rows][cols];
//        
//        for (int i = 0; i < rows; i++) { //for each row
//            for (int j = 0; j < cols; j++) {
//                oneColorArray[i][j] = color; //8224160;
//                //1579032; //7500402; //8553090; //8224125; //16777215;
//            }
//        }
//        
//        ImageData.show(oneColorArray);
//        
//    }
       
//    /**
//     * assign entire row of given array a single color
//     * @param the array, row to color, integer color value
//     */
//    private static void colorRow(int[][] arr, int row, int color) {
//        
//        for (int j = 0; j < arr[0].length; j++) {
//            arr[row][j] = color;
//        }
//    }
}