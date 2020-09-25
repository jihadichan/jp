# Mnemonics 

- Split element from left to upper right, then down. E.g. for 選 : road, 2 snakes, together

- Determine proper keyword as concept, which is applicable on as many words as possible.  "Too much" is probably also ok, although 2 words.

- Try to build a mnemonic with as few words as possible, which also encompasses as many elements as possible. Optimally with the most the common reading in it. E.g. for 選 :

  ```
  せん - On the road, 2 snakes together let you select something sensational
  えら - to which era you want to travel back in time
  ```

  Also ok:

  ```
  ！ - On the road, 2 snakes together let you select
  せん - to which sensational 
  えら - era in history you want to travel back
  ```

  The goal of the card is learn the mnemonic. The words and confusions are complementary, i.e. click on "hard" instead of failing the card.

- Phonetic mnemonics must be recorded and reused for all occurrences.
- If there's a main reading with has an equivalent with dakuten (e.g. かい - がい), then don't create another mnemonic and just put it into other forms.



# Create new CSV data to import

1. If you have new kanji without readings use RunJishoKanjiDataScraper. This will read column 1 from kanjiDataStatics.csv an scrape 
   the results from Jisho. Copy output back to kanjiDataStatics.csv
2. To download the data for the kanji (i.e. words and their meanings) use RunJishoApiScraper. This will download the first
   100 words as JSON and save it in kanji/. This is again based on kanjiDataStatics.csv.
3. To create the CSV data for Anki use RunWordAnalyzer. This will create a CSV in output/. Copy
   into your deck CSV. Check Config.KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE for the required columns.
4. To create MP3 TTS files use RunWaveNetTtsScraper. This is based on ttsDataList.csv. Copy the compound readings columns from RunWordAnalyzer output. 
   Copy complete folder readings/ into Anki collection.
5. To analyze the frequencies of readings use RunReadingStats. This uses ttsDataList.csv and gives you stats on how often a reading occurs. See stats below.



# Install deck

- Install https://ankiweb.net/shared/info/1280253613 to use external JS libs
- AnkiDroid enable in Settings > Advanced > Type answer into card. Otherwise focus on input field doesn't work.



# Notes

- 09.08.2019
  There are 3006 kanji in kanjiDataStatics.csv. However, the amount of kanji which have a frequency (1-2500) is 2405, i.e. we're still missing 95. And with the kanji from 20k words it would add at least another 100. Shiiiiet.



# Encountered Problems

1. Make sure your CSV files are in UTF-8. LibreOffice likes to save kanji as UTF-16.
2. Make sure to create your import CSV with quotes around every cell, otherwise you can get problems with special chars.
3. With both voices ぬす is cut off, when there is no `<break>` at the end. With break the female voice works properly, male voice doesn't. So we're using the female one, although the male is clearer.
   Polly is shit. WaveNet is cooler. Problem doesn't occur with it.
4. Argh shieet... female is even worse on more cases. Using male voice again. Readings folder contains copy of female voice, if male fucks up, replace with female.



# TODO

- all good




# Stats

```
Total unique readings:    1341
Readings with freq >= 10:   67
Readings with freq >= 4:   246
Readings with freq == 3:    87
Readings with freq == 2:   218
Readings with freq == 1:   795
```

Checked manually, total with the stats in 1346, so there's some error. Anyway. Nice to know: Frequency 1 also applies to single hiragana chars like ぞ, れ, へ, づ, while しょう occurs 38 times, 42x こう, 23x きょう. Interesting.





# Scoring

- Scores:
  - Common word: 2
  - In JLPT vocab: 1
  - All other: 0
  
  


# Determine readings for TTS

- If scores available, take up to 3 readings (except score == 0)
- If totalScore == 0, take first of most common readings
- If no qualified words at all, e.g. 厖, take first onyomi 
- And some others, see CsvHelpers.getReadingsForTts()
