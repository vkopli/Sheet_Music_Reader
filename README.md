# Sheet_Music_Reader

Image processing algorithm in Java to read sheet music images and play them back.

## Description
Program that interprets sheet music that is downloaded from online. Recognizes quarter notes, half notes, and chords on treble clef with manual specification of sharps/flats. Instructions appear on image to click with mouse where to start and stop reading.

![demonstration](https://user-images.githubusercontent.com/26824976/78621661-2106cf80-7851-11ea-8e32-d863ba273a6d.png)

## Instructions to Run

Execution: java PlaySheetMusic \
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; java PlaySheetMusic imageFile.ext \
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; java PlaySheetMusic imageFile.ext Sharp note \
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; java PlaySheetMusic imageFile.ext Flat note \
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; java PlaySheetMusic imageFile.ext Sharp note1 Flat note2...


Recommended Executions: \
java PlaySheetMusic \
java PlaySheetMusic music_yankee.jpg Sharp F \
java PlaySheetMusic music_ode.jpg Flat B
