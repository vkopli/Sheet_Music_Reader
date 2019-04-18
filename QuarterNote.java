/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 * 
 * Description: A quarter note that has already been read from the line
 */

public class QuarterNote implements Note{
    
    private static String[] string = {"G", "F", "E", "D", "C", "B",
         "A", "G", "F", "E", "D"};
    private static int[] pitch = {10, 8, 7, 5, 3, 2, 0, -2, -4, -5, -7};
    
    private static final int SPS = 44100; //samples per second
    private static final double DURATION = 0.5;
    private static final int SAMPLE_LENGTH = (int) (SPS * DURATION);
    
    private static final double[] blankArray = new double[(int) (SPS * 0.05)];
    
    private int x;
    private int[] y = new int[100];
    private int[] staffIdx = new int[100];
    private int N = 0; //number of notes in chord
    
    private double[] sampleArray;
       
    /**
     * make given note to that note plus a sharp (e.g. F --> F sharp)
     * @param note to be set to sharp version of note
     */
    public static void setSharp(String s) {
        
        boolean foundNote = false;
        for (int i = 0; i < string.length; i++) {
            if (string[i].equals(s)) {
                pitch[i] += 1;  
                foundNote = true;
            }
        }
        
        if (!foundNote) {
            throw new IllegalArgumentException("not a valid note string");
        }
    }
    
    /**
     * make given note to that note plus a sharp (e.g. F --> F flat)
     * @param note to be set to flat version of note
     */
    public static void setFlat(String f) {
        
        boolean foundNote = false;
        for (int i = 0; i < string.length; i++) {
            if (string[i].equals(f)) {
                pitch[i] -= 1;  
                foundNote = true;
            }
        }
        if (!foundNote) {
            throw new IllegalArgumentException("not a valid note string");
        }
    }
    
    /** 
     * Constructor: make QuarterNote
     * @param x position, y position, index of note in staff
     */
    public QuarterNote(int xPos, int yPos, int Idx) {
        
        x = xPos;
        y[N] = yPos;
        staffIdx[N++] = Idx;
        sampleArray = makeSampleArray();
    }
        
    /**
     * get string representation of note
     */
    public String getNoteString() {
        
        String s = string[staffIdx[0]];
        for (int i = 1; i < N; i++) {
            s += "/" + string[staffIdx[i]];
        }
        return s;
    }
     
    /**
     * play note
     */
    public void play() {
        
        StdAudio.play(sampleArray);
        StdAudio.play(blankArray);
    }
    
    /**
     * draw note
     */
    public void draw() {
        
        PennDraw.setPenColor(PennDraw.RED);
        for (int i = 0; i < N; i++) {
            PennDraw.filledCircle(x, y[i], 3);
        }
    }
    
    /**
     * get sample array for note
     */
    public double[] getSampleArray() {
        
        return sampleArray;
    }
    
    /**
     * Returns staff index of the first note in the chord
     * (if the note is part of a chord)
     * @return staff index of the first note of the chord
     */
    public int getStaffIdx() {
        
        return staffIdx[0];
    }
    
    /**
     * Returns x position of note (of first note if it is part of a chord)
     * @return x position of note
     */
    public int getX() {
        
        return x;
    }
    
    /**
     * Returns y position of note (of first note if it is part of a chord)
     * @return y position of note
     */
    public int getY() {
        
        return y[0];
    }
    
    /**
     * add another note over this note by combining sample arrays
     * @param note to be overlayed over this note
     */
    public void overlayNote(Note newNote) {
    //WARNING: assumes inputted note has shorter or same duration as this note
        
        double[] sampleArray2 = newNote.getSampleArray();
        y[N] = newNote.getY();
        staffIdx[N++] = newNote.getStaffIdx();
        for (int i = 0; i <= SAMPLE_LENGTH; i++) {
            sampleArray[i] += sampleArray2[i];
        }
    }
    
    /**
     * make sample array for note (used by StdAudio to play note)
     */
    private double[] makeSampleArray() {
        
        int p = pitch[staffIdx[0]];
        double hz = 440 * Math.pow(2, p / 12.0);
        double[] sampleArray = new double[SAMPLE_LENGTH + 1];
        for (int i = 0; i < SAMPLE_LENGTH; i++) {
            sampleArray[i] = Math.sin(2 * Math.PI * i * hz / SPS);
        }
        
        return sampleArray;
    }
}