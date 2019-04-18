/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 * 
 * Description: A black line from the sheet music image
 */

public class BlackLine implements Line {

    private static int[][] image; //2D array representation of music image
    private static int rows, cols; 
    private static int grayThreshold;
        
    private int yStart, yEnd;
    private int currCol; //current column of line being looked at
    private int staffIdx;
        
    private int dyUp, dyDown; //distance to blank lines above and below
    
    private int startCol = Integer.MIN_VALUE; //optional to change in program
    private int endCol = Integer.MAX_VALUE; //optional to change in program
    
    /**
     * Saves sheet music image into the line classes
     * @param 2D array of sheet music image, int value for grey color 
     */
    public static void initialize(int[][] insertedImage, int GRAY_THRESHOLD) {
        
        image = insertedImage;
        rows = image.length;
        cols = image[0].length;
        grayThreshold = GRAY_THRESHOLD;
    }
    
    /**
     * Constructor to make a BlackLine object 
     * @param where BlackLine starts and ends
     */
    public BlackLine(int yStart, int yEnd) {  
        
        if (image == null) {
            throw new RuntimeException("BlackLine class not yet initialized");
        }
        
        this.yStart = yStart;
        this.yEnd = yEnd;
    }
     
    /**
     * Sees if there is another readable column in the image's row (line)
     * @return if the image has another readable column
     */
    public boolean hasNext() {
        
        return currCol < (cols - dyUp / 2 + 1) &&
            currCol < (cols - dyDown / 2 + 1) && 
            currCol <= endCol;
    }
    
    /**
     * Moves on to the next column in the image (line)
     */
    public void next() {
        
        currCol++;
    }
    
    /**
     * Makes quarter note at current position (currCol, y)
     */
    public Note makeQuarterNote() {
        
        int y = (yStart + yEnd) / 2;
        Note newNote = new QuarterNote(currCol, y, staffIdx);
        return newNote;
    }
    
    /**
     * Makes half note at current position (currCol, y)
     */
    public Note makeHalfNote() {
        
        int y = (yStart + yEnd) / 2;
        Note newNote = new HalfNote(currCol, y, staffIdx);
        return newNote;
    }
    
    /**
     * Checks for a black "blob" (quarter note) on the black line at currCol 
     * assumes currCol is set to valid index
     * @return whether there is a quarter note on the line
     */
    public boolean checkQuarterNote() {
    //IMPORTANT: up in image array corresponds with down in actual image
        
        checkErrors();
        
        boolean U, D, UL, UR, DL, DR;
        boolean isNote = true;
        
        for (int i = 1; i <= (dyUp / 2); i++) {
            U = image[yStart - i][currCol] < grayThreshold; 
            UL = image[yStart - i][currCol - i] < grayThreshold;
            UR = image[yStart - i][currCol + i] < grayThreshold;
            isNote &= U && UL && UR;
        }
                 
        for (int i = 1; i <= (dyDown / 2); i++) {  
            D = image[yEnd + i][currCol] < grayThreshold; 
            DL = image[yEnd + i][currCol - i] < grayThreshold; 
            DR = image[yEnd + i][currCol + i] < grayThreshold; 
            isNote &= D && DL && DR;
        }
            
        return isNote;
    }
    
    /**
     * Checks for a half note on the black line at currCol 
     * assumes currCol is set to valid index
     * @return whether there is a half note on the line
     */
    public boolean checkHalfNote() {
    //IMPORTANT: up in image array corresponds with down in actual image
        
        checkErrors();
        
        boolean U, D, UL, UR, DL, DR;
        boolean isNote = true;
        
        //check immediate surrounding pixels of note 
            U = image[yStart - 1][currCol] < grayThreshold; 
            UL = image[yStart - 1][currCol - 1] < grayThreshold;
            UR = image[yStart - 1][currCol + 1] < grayThreshold;
            D = image[yEnd + 1][currCol] < grayThreshold; 
            DL = image[yEnd + 1][currCol - 1] < grayThreshold; 
            DR = image[yEnd + 1][currCol + 1] < grayThreshold; 
        
            //true if all are blank
            isNote &= !(U && UL && UR && D && DL && DR);
        
        //check if pair of further upper left and lower right pixels are black 
        //(at least 2 in a row)
        int dy;
        if (dyUp < dyDown) {
            dy = dyUp;
        }
        else {
            dy = dyDown;
        }   
        boolean furtherPixels = false;
        boolean prevFurtherPixels = false;
        for (int i = 2; i <= dy / 2; i++) {
            UL = image[yStart - i][currCol - i] < grayThreshold;
            DR = image[yEnd + i][currCol + i] < grayThreshold; 
            furtherPixels = UL && DR;
            if (furtherPixels && prevFurtherPixels) {
                break;
            }
            prevFurtherPixels = furtherPixels;
        }
        
        //true if at least two pairs of further pixels in a row are black
        //and immediately surrounding pixels are blank
        isNote &= furtherPixels && prevFurtherPixels;
        
        return isNote;
    }
           
    /**
     * Move currCol enough to the right that checkNote can check to the left
     * @param int distance between the line and the line above it
     */
    public void setDyUp(int dyUp) {
        
        this.dyUp = dyUp;
        
        if (currCol < dyUp) {
            currCol = dyUp; 
        }
    }
    
    /**
     * Move currCol enough to the right that checkNote can check to the left
     * @param int distance between the line and the line below it
     */
    public void setDyDown(int dyDown) {
        
        this.dyDown = dyDown;

        if (currCol < dyDown) {
            currCol = dyDown;
        }
    }
    
    /**
     * Set the note that the line can play
     * @param int which staff it is
     */
    public void setStaffIdx(int staffIdx) {
        
        this.staffIdx = staffIdx;
    }
        
    /**
     * Get where the line starts
     * @return where the line starts
     */
    public int getYStart() {
        
        return yStart;
    }
    
    /**
     * Get where the line ends
     * @return where the line ends
     */
    public int getYEnd() {
        
        return yEnd;
    }
    
    /**
     * Get the y position midpoint of the line
     * @return the y position midpoint of the line
     */
    public int getY() {
        
        return (yStart + yEnd) / 2;
    }
    
    /**
     * Set currCol/startCol and endCol
     * @param the number of columns
     */
    public void setXlim(int numCols) {
        
        startCol = numCols;
        currCol = numCols; 
        endCol = cols - 1 - numCols;
    }
    
    /**
     * Optional function to further restrict columns that are searched
     * @param start and end columns
     */
    public void setXlim(int startCol, int endCol) {
        
        this.startCol = startCol;
        this.endCol = endCol;
        
        //shift currCol to the right if wanting to exclude something
        if (startCol > currCol) {
            currCol = startCol; 
        }
    }
    
    /**
     * Checks that the values of currCol and dyUp/dyDown have been set, and
     * whether the given line is too close to the upper/lower image edge to
     * be able to check for a note
     * If not, throws an error
     */
    private void checkErrors() {
                      
        if (currCol == 0) {
            throw new RuntimeException("ERROR: currCol not initialized");
        }
        
        if (dyUp == 0 || dyDown == 0) {
            throw new RuntimeException("ERROR: dy not set for blackLine");
        }   
        
        if ((yStart - dyUp) < 0 || (yEnd + dyDown) >= rows) {
            throw new IllegalArgumentException("ERROR: staff lines too close" +
                                               "to upper/lower image edge");
        }
    }
    
    /**
     * Visualize the black lines for user
     */
    public void draw() {   
        
        PennDraw.setPenColor(PennDraw.BLACK);
        int x1, x2;
        
        if (startCol == Integer.MIN_VALUE || endCol == Integer.MAX_VALUE) {
            x1 = startCol;
            x2 = endCol;
        }
        else {
            x1 = 0; 
            x2 = cols; 
        }
        
        if (yStart == yEnd) {
            PennDraw.line(x1, yStart, x2, yStart);
        }
        else {
            double xCenter = (x2 - x1) / 2.0;
            double yCenter = (yStart + yEnd) / 2.0;
            double halfWidth = (x2 + x1) / 2.0;
            double halfHeight = yEnd - yStart;
            PennDraw.filledRectangle(xCenter, yCenter, halfWidth, halfHeight);
        }
    }
}