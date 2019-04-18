/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 *
 * Description: Note interface used by QuarterNote class
 */

public interface Note {
    
    /**
     * play note
     */
    public void play();
        
    /**
     * draw note
     */
    public void draw();
    
    /**
     * get string representation of note
     */
    public String getNoteString();

    /**
     * add another note over this note by combining sample arrays
     * @param note to be overlayed over this note
     */
    public void overlayNote(Note note);
    
    /**
     * get sample array for note
     */
    public double[] getSampleArray();
            
    /**
     * Returns staff index of the first note in the chord
     * (if the note is part of a chord)
     * @return staff index of the first note of the chord
     */
    public int getStaffIdx();
    
    /**
     * Returns x position of note (of first note if it is part of a chord)
     * @return x position of note
     */
    public int getX();
      
    /**
     * Returns y position of note (of first note if it is part of a chord)
     * @return y position of note
     */
    public int getY();
}