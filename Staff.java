/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 * 
 * Description: A staff (a group of 5 black lines and the "blank lines" 
 * between and sandwiching the black lines) from the sheet music image
 */

public class Staff {
    
    //chord threshold: 
    //# col pixels within which several notes make a chord
    private static final int CHORD_THRESHOLD = 3;
    
    //maximum # pixels between lines: needed to determine how far off
    //right/left edge of image to start/end searching for notes 
    private int maxDY = Integer.MIN_VALUE;
    
    //main staff lines: 1-9
    //staff lines 0 and 11 are the blank lines above and below staff
    private Line[] lines = new Line[11]; //evens: blackLines, odds: blankLines 
        
    /**
     * Constructor: creates staff
     * @param index in blackLines array of first black line to be put in staff 
     *        xStart: desired starting x (col) index of staff to be made
     *        xEnd: desired ending x (col) index of staff to be made 
     */
    public Staff(BlackLine[] allBlackLines, int index, int xStart, int xEnd) {
        
        BlackLine blackLine;
        BlankLine blankLine = new BlankLine(0, 0);
        BlackLine prevLine;
        
        //y positions for prev and current black lines, blank lines, and dy
        int b1, b2, y, dy; 
        
        //make the blackLine at index 1
        blackLine = allBlackLines[index++];
        blackLine.setStaffIdx(1);
        lines[1] = blackLine; //index first black line in array
        
        prevLine = blackLine; //assign first black line as previous
        BlackLine firstBlackLine = blackLine;
        int firstPos = blackLine.getYEnd(); //position of first black line
        int ySecondBlankLine = 0; 
        
        //make alternating blank and black lines at indicies 2 - 9
        for (int i = 1; i < 5; i++) {       
            
            //take next black line from inputted lines
            blackLine = allBlackLines[index++];
            
            //make new blank line based off of previous and current black line
            b1 = prevLine.getYEnd();
            b2 = blackLine.getYStart();
            y = (b1 + b2) / 2;
            dy = b2 - y;
            blankLine = new BlankLine(y, dy);
            
            if (i == 1) {
                ySecondBlankLine = y;
            }
            
            //set dy for black lines
            prevLine.setDyDown(dy);
            blackLine.setDyUp(dy);
//            System.out.println(dy);

            //assign each line an index on staff, then put into lines array
            addToLineArray(blankLine, i * 2);
            addToLineArray(blackLine, i * 2 + 1);
            
            prevLine = blackLine;
            
            if (dy > maxDY) {
                maxDY = dy;
            }
        }      

        //make blankLine at index 0 and index 10 (above and below main staff)
        int y1 = firstPos; //position of first black line
        int y2 = blackLine.getYStart(); //position of last black line
        int dy1 = ySecondBlankLine - y1;
        int dy2 = y2 - blankLine.getY();

        BlankLine firstBlankLine = new BlankLine(y1 - dy1, dy1);
        BlankLine lastBlankLine = new BlankLine(y2 + dy2, dy2);
        firstBlackLine.setDyUp(dy1);
        blackLine.setDyDown(dy2);
        
        //assign first/last blank lines an index on staff, put into lines array
        addToLineArray(firstBlankLine, 0);
        addToLineArray(lastBlankLine, 10);
        
        //set start and end limits of each line for checking for notes
        for (int i = 0; i < lines.length; i++) {
            lines[i].setXlim(maxDY); 
//            lines[i].setXlim(xStart, xEnd);
        }
    }
            
    public void next() {
        for (int i = 0; i < 5; i++) {
            lines[i].next();
        }
    }
    
    /**
     * reads each line in image, and stores any notes that are found on each
     * line into linked list notes, in the order that they appear
     * if notes appear in almost the same column on different lines, they are
     * overlayed over the first note, which can play them as a chord
     * @param linked list that holds notes
     */
    public void read(LinkedList<Note> notes) {
    //WARNING: assumes currCol set to appropriate value
        
        boolean isNote, isQuarterNote, isHalfNote;
        boolean[] prevIsNote = new boolean[lines.length];        
        boolean firstTime = true; //true when haven't yet found note in column
        
        Note newNote = null;
        Note firstNote = null; //first note in column
        
        int firstNoteCount = -1000000;
        int counter = 0;
        
        while (lines[0].hasNext()) { //for each column in image
            for (int i = 0; i < lines.length; i++) { //for each line in staff 
                
                //check for notes
                isQuarterNote = lines[i].checkQuarterNote();
                isHalfNote = lines[i].checkHalfNote();
                isNote = isQuarterNote || isHalfNote;
                
                //if is a note and previous column on line wasn't a note
                if (isNote && !prevIsNote[i]) {
                    
                    //create the note
                    if (isQuarterNote) {
                        newNote = lines[i].makeQuarterNote();
                    }
                    else {
                        newNote = lines[i].makeHalfNote();
                    }
                    
                    //if within CHORD_THRESHOLD columns of another note
                    if (counter - firstNoteCount <= CHORD_THRESHOLD) {
                        //overlay new note over the first one
                        overlayNotes(firstNote, newNote); //make chord
                    }               
                    //if first note found within CHORD_THRESHOLD columns
                    else if (firstTime) {
                        firstNote = newNote;
                        notes.add(newNote); //add note to linked list
                        firstTime = false;
                        firstNoteCount = counter;
                    }
                }
                prevIsNote[i] = isNote;
                lines[i].next();
            }
            firstTime = true;
            counter++;
        }
    }
       
    /**
     * assign staff index (later pertinent to what pitched note it can make)
     * to given line, and put line into lines array 
     * @param black line and desired staff index
     */
    private void addToLineArray(BlackLine line, int index) {
                           
        line.setStaffIdx(index);
        lines[index] = line;
    }
          
    /**
     * assign staff index (later pertinent to what pitched note it can make)
     * to given line, and put line into lines array 
     * @param blank line and desired staff index
     */
    private void addToLineArray(BlankLine line, int index) {
                           
        line.setStaffIdx(index);
        lines[index] = line;
    }

    /**
     * overlays first inputted note with samples from second inputted note
     * @param note 1 and note 2
     */
    public static void overlayNotes(Note note1, Note note2) {
        
        if (note2 instanceof HalfNote) {
            note2.overlayNote(note1);
        }
        else {
            note1.overlayNote(note2);
        }
    }
    
    /**
     * Set start and end columns in all lines of staff
     * @param the start and end columns to set as limits
     */
    public void setXlim(int startCol, int endCol) {
        
        for (int i = 0; i < lines.length; i++) {
            lines[i].setXlim(startCol, endCol);
        }
    }
        
    /**
     * draw lines and blanklines of staff
     */
    public void draw() {

        for (int i = 0; i < lines.length; i += 1) {
            lines[i].draw();
        }  
    }

}