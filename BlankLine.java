/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 * 
 * Description: A blank line (representing the blank space between two black
 * lines on a staff) from the sheet music image
 */

public class BlankLine implements Line {

    private static int[][] image; //2D array representation of music image
    private static int rows, cols;
    private static int grayThreshold;
         
    private int y;
    private int dy; //distance to black lines above and below
    
    private int currCol; //current column of line being looked at
    private int staffIdx;
    
    private int startCol = Integer.MIN_VALUE; //optional use
    private int endCol = Integer.MAX_VALUE; //optional use
    
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
     * Constructor: make a BlankLine object 
     * @param the line's y and the distance to the next line
     */
    public BlankLine(int y, int dy) {
        
        if (image == null) {
            throw new RuntimeException("BlankLine class not yet initialized");
        }
        
        this.y = y;
        this.dy = dy;
    }
     
    /**
     * Sees if there is another readable column in the image's row (line)
     * @return if the image has another readable column
     */
    public boolean hasNext() {
        
        return currCol < (cols - dy / 2 + 1) && currCol <= endCol;
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
        
        Note newNote = new QuarterNote(currCol, y, staffIdx);
        return newNote;
    }
    
    /**
     * Makes half note at current position (currCol, y)
     */
    public Note makeHalfNote() {
        
        Note newNote = new HalfNote(currCol, y, staffIdx);
        return newNote;
    }

  /**
     * Checks for a black "blob" (quarter note) on the blank line at currCol 
     * assumes currCol is set to valid index
     * @return whether there is a quarter note on the line
     */
    public boolean checkQuarterNote() { 
    //IMPORTANT: up in image array corresponds with down in actual image    
        
        checkErrors();
        
        boolean U, D, UL, UR, DL, DR, L, R;
        boolean isNote = true;   
        boolean center = image[y][currCol] < grayThreshold;         
        isNote = center;
        
        for (int i = 1; i <= (dy / 2); i++) {
            U = image[y - i][currCol] < grayThreshold; 
            UL = image[y - i][currCol - i] < grayThreshold;
            UR = image[y - i][currCol + i] < grayThreshold;           
            D = image[y + i][currCol] < grayThreshold; 
            DL = image[y + i][currCol - i] < grayThreshold; 
            DR = image[y + i][currCol + i] < grayThreshold; 
            L = image[y][currCol - i] < grayThreshold; 
            R = image[y][currCol + i] < grayThreshold; 
            isNote &= U && UL && UR && D && DL && DR && L && R;
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
        
        boolean U, D, UL, UR, DL, DR, L, R;
        boolean isNote = !(image[y][currCol] < grayThreshold); //center blank
        
        //check immediate surrounding pixels of note 
        for (int i = 0; i < 2; i++) {
            U = image[y - i][currCol] < grayThreshold; 
            UL = image[y - i][currCol - i] < grayThreshold;
            UR = image[y - i][currCol + i] < grayThreshold;
            D = image[y + i][currCol] < grayThreshold; 
            DL = image[y + i][currCol - i] < grayThreshold; 
            DR = image[y + i][currCol + i] < grayThreshold; 
            L =image[y][currCol - i] < grayThreshold; 
            R =image[y][currCol + i] < grayThreshold;
            
            //true if all are blank
            isNote &= !(U && UL && UR && D && DL && DR && L && R);
        }
                
        //check if pair of further upper left and lower right pixels are black 
        //(at least 2 in a row)
        boolean furtherPixels = false;
        boolean prevFurtherPixels = false;
        for (int i = 2; i < dy / 2; i++) {
            UL = image[y - i][currCol - i] < grayThreshold;
            DR = image[y + i][currCol + i] < grayThreshold; 
            furtherPixels = UL && DR;
            if (furtherPixels && prevFurtherPixels) {
                break;
            }
            prevFurtherPixels = furtherPixels;
        }
        
        //true if at least two pairs of further pixels in a row are black
        //and immediately surrounding pixels are blank
        isNote &= furtherPixels && prevFurtherPixels;
        
        //check if pair of further upper and lower pixels are black 
        boolean vertFurtherPixels = false;
        for (int i = 2; i < dy / 2; i++) {
            U = image[y - i][currCol - i] < grayThreshold;
            D = image[y + i][currCol + i] < grayThreshold; 
            furtherPixels = U && D;
            if (vertFurtherPixels) {
                break;
            }
        }

        return isNote &= vertFurtherPixels;
    }
    
    /**
     * Set the note that the line can play
     * @param int which staff it is
     */
    public void setStaffIdx(int staffIdx) {
        
        this.staffIdx = staffIdx;
    }
    
    /**
     * Get the y position midpoint of the line
     * @return the y position midpoint of the line
     */
    public int getY() {
        
        return y;
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
     * WARNING: if not done before checking for notes, currCol will be reset
     */
    public void setXlim(int start, int end) {
        
        //shift startCol to the right if wanting to exclude something
        if (start > startCol) {
            startCol = start;
            currCol = start; 
        }
        //shift endCol to the left if wanting to exclude something
        if (end < endCol) {
            endCol = end;
        }
    }
    
    /**
     * Checks that the values of currCol and dy have been set, and
     * whether the given line is too close to the upper/lower image edge to
     * be able to check for a note
     * If not, throws an error
     */
    private void checkErrors() {
                     
        if (dy == 0) {
            throw new RuntimeException("ERROR: dy not set for blankLine");
        }
              
        if ((y - dy) < 0 || (y + dy) >= rows) {
            throw new IllegalArgumentException("ERROR: staff lines too close" +
                                               "to upper/lower image edge");
        }
        
        if (currCol == 0) {
            throw new RuntimeException("ERROR: currCol not initialized");
        } 
    }
    
    /**
     * Visualize the black lines for user
     */ 
    public void draw() {
        
        PennDraw.setPenColor(PennDraw.WHITE);
        int x1, x2;
        
        if (startCol == Integer.MIN_VALUE || endCol == Integer.MAX_VALUE) {
            x1 = startCol;
            x2 = endCol;
        }
        else {
            x1 = 0; 
            x2 = cols;
        }
        
        PennDraw.line(x1, y, x2, y);
    }
}