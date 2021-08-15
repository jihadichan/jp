# Notes

## TODO

- Check if escaped quotes work properly with Anki.

  ```
  grammarSummary='A phrase which expresses the idea ””not necessarily””.', equivalent='Not always; not necessarily', sentences=[DjtSentence{japanese='体の弱い人が早く死ぬとは限らない。'
  ```




## Grammar Dict Deck

**Raw CSV**

- Every row has multiple sentences, i.e. every sentence must have its own row in your output CSV.

- File names in GitHub may contain Japanese. So you can keep them as it is

**Sentences**

- 0 - grammatical concept (grammarItem: String)

- 1 - usage (grammarSummary: String)

- 2 - equivalent (englishEquivalent: String)

- 3-34 - sentences (sentences: List\<DjtSentence\>)
  - See specifics below

**Grammar Page**

- 35 - notes (grammarBookImage: String)
  - Extract from \<img>
- 36 - textnotes (grammarBookHtml: String)
- 37 - formation (syntaxHtml: String)
  - Uses CSS classes. Doesn't work on GitHub but looks fine anyway.
- 38 - part of speech (partOfSpeech: String)
  - Highlight. Add to sentence cards.
- 39 - related expression (relatedExpression: String) 
- 40 - antonymExpression (antonymExpression: String)









## Sentence specifics - // DONE

- Columns are (Jap / Eng): 

  - 3,4
  - 5,6
  - 7,8
  - 9,10
  - 11,12
  - 13,14
  - 15,16
  - 17,18
  - 19,20
  - 21,22
  - 23,24
  - 25,26
  - 27,28
  - 29,30
  - 31,32
  - 33,34

- English

  - has double spaces.

- Japanese uses Anki stuff. Must be cleaned. Replace one after another:

  ```
  <span.*?\(\w+\).*?span>
  <span.*?>
  </span>
  ```

  See sentences.png for how it looks

- Some sentences have no Japanese? Maybe also the other way around? Discard these.

- Check for Latin whitespaces



