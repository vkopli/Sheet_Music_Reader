/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 *
 * Description: Line interface used by blackLine and blankLine classes
 */

public interface Line {
    
    /**
     * sees if there is another readable column in the image's row (line)
     * @return if the image has another readable column
     */
    public boolean hasNext();
    
    /**
     * moves on to the next column in the image (line)
     */
    public void next();
    
    /**
     * make quarter note at current position (currCol, y)
     */
    public Note makeQuarterNote();

    /**
     * make quarter note at current position (currCol, y)
     */
    public Note makeHalfNote();
               
    /** 
     * Checks for a black "blob" (quarter note) on the line at currCol 
     * assumes currCol is set to valid index
     * @return whether there is a note on the line
     */
    public boolean checkQuarterNote();
    
    /** 
     * Checks for a half note on the line at currCol 
     * assumes currCol is set to valid index
     * @return whether there is a note on the line
     */
    public boolean checkHalfNote();
    
    /**
     * set the note that the line can play
     * @param int which staff it is
     */
    public void setStaffIdx(int staffIdx);
    
    /**
     * set currCol/startCol and endCol
     * @param the number of columns
     */
    public void setXlim(int numCols);
    
    /**
     * optional function to further restrict columns that are searched
     * @param start and end columns
     */
    public void setXlim(int startCol, int endCol);
        
    /**
     * Visualize the black lines for user
     */
    public void draw(); 
}