

# How To

1. If you have new kanji without readings use RunJishoKanjiDataScraper. This will read column 1 from kanjiList.csv an scrape 
   the results from Jisho. Copy output back to kanjiList.csv
2. To download the data for the kanji (i.e. words and their meanings) use RunJishoApiScraper. This will download the first
   100 words as JSON and save it in kanji/. This is again based on kanjiList.csv.
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
  There are 3006 kanji in kanjiList.csv. However, the amount of kanji which have a frequency (1-2500) is 2405, i.e. we're still missing 95. And with the kanji from 20k words it would add at least another 100. Shiiiiet.

# Encountered Problems

1. Make sure your CSV files are in UTF-8. LibreOffice likes to save kanji as UTF-16.
2. Make sure to create your import CSV with quotes around every cell, otherwise you can get problems with special chars.
3. With both voices ぬす is cut off, when there is no `<break>` at the end. With break the female voice works properly, male voice doesn't. So we're using the female one, although the male is clearer.
   Polly is shit. WaveNet is cooler. Problem doesn't occur with it.
4. Argh shieet... female is even worse on more cases. Using male voice again. Readings folder contains copy of female voice, if male fucks up, replace with female.



# TODO

- Rewrite this shit, JSON now, requires addon, etc
- Urm.. maybe it would have been better to create JSONs for the example tables? Well, fuck it.
- God damn it... You really need to use JSON. Also for kanji with furigana. For `<ruby>日本語<rt>asdf</rt></ruby>` 


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
