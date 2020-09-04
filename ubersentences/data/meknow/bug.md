# Bugs

## Wrong audio files

Bug is caused because we downloaded the steps of sections from MeKnow into 1 folder, but in 45 cases MeKnow uses the same filename for a MP3. So in the resulting URL list the filename occurs twice, which caused `wget` to rename it to `asdf.mp3` to `asdf.1.mp3`. And our mapping does not recognize the suffix. 

Easiest way is to modify the filenames in the step.json files and recompile the mapper. 

1. Find all files with `.1` suffix.
2. Use search tool to search in step.json files to find the corresponding entries. Find the correct entry in the 2 files (can also be in the same).
3. Replace filename with filename with suffix. 
4. Play audio file to check if it matches the sentence.

... yeah... there's another problem with duplicate sentences in other steps. Just mark and suspend the card. Delete from Repeater.

... or not... suspend when you come across them.
