# Top Words Analysis

In total, nearly 226 million words consisting of just over 163,000 lemmas were counted. The 10,000 most frequent words accounted for 92% of all occurrences, the top 5000 accounted for 86%, the top 2000 accounted for 76%, and the top 1000 accounted for 68%.

[https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Japanese](https://en.wiktionary.org/wiki/Wiktionary:Frequency_lists/Japanese)

Notes:

- 737 words do not have a corresponding Wiktionary page. They are therefore probably uncommon terms. (See HTML for class="new")
- There are also totally useless words like キン肉マン ("Kinnikuman is a manga ...")


## Top2k Stats

| Word Types          | Occurrences |
| ------------------- | ----------- |
| Words without kanji | 275         |
| 1 kanji             | 711         |
| 2 kanji             | 994         |
| Rest                | 20          |

| Kanji Set          | Uniques |
| ------------------ | ------- |
| Jouyou             | 950     |
| Jinmeiyou          | 8       |
| None               | 3       |
| Unique kanji total | 961     |


## Top10k Stats

| Word Types          | Occurrences |
| ------------------- | ----------- |
| Words without kanji | 2059        |
| 1 kanji             | 2110        |
| 2 kanji             | 5616        |
| Rest                | 215         |

| Kanji Set          | Uniques |
| ------------------ | ------- |
| Jouyou             | 1885    |
| Jinmeiyou          | 200     |
| None               | 78      |
| Unique kanji total | 2163    |



## Top20k Stats

| Word Types          | Occurrences |
| ------------------- | ----------- |
| Words without kanji | 5273        |
| 1 kanji             | 2886        |
| 2 kanji             | 11136       |
| Rest                | 705         |

| Kanji Set          | Uniques |
| ------------------ | ------- |
| Jouyou             | 2075    |
| Jinmeiyou          | 406     |
| None               | 327     |
| Unique kanji total | 2808    |



## Unknown Kanji

| TopList | Unkown kanji |
| ------- | ------------ |
| Top 2k  | 0            |
| Top 10k | 42           |
| Top 20k | 230          |



## Random Knowledge

- Kun-reading is used in majority of case when the kanji does NOT stand next to another kanji.
- On-reading is used in majority of case when the kanji DOES stand next to another kanji.
- JLPT N1 expected study hours:
  - With prior kanji knowledge: 1700~2600 hours
  - Without prior knowledge: 3000~4800 hours



## Problem Definition

- Main problem is reading Kanji. You must:
  - Recognize the character
  - Identify its meaning
  - Choose between multiple possible readings
- Each kanji has 2 or more readings, depending on the context



## Goal

- Get a basis vocabulary to be able to start passive immersion (Top ~2000 words)
- From there the best way would probably be sentence mining and pick up new words as they occur. 
- iKnow 6000 sentences could also be a good source, parallel to sentence mining through immersion. 



## Readings problem

Problem:
Most kanji have multiple readings. When you start learning sentences it's f-ing confusing. For example, one of the first words you probably learn is 食べる (taberu, to eat), reading of 食 is "ta" in this case. You memorize that and then later come across 食事 (shokuji, meal), reading is "shoku". But chances are that you intuitively read it as "taji", which is wrong. Later you come across 朝食 (choushoku, breakfast) and again you're confused, is it "ta" or "shoku" and you have to look it up and still have the risk to confuse it, because 食べる is one of the most frequent words so the reading will be engraved into your memory as nr. 1 reading, which is also wrong because there are 38 common words which use "shoku", but only 2 common words which use "ta". And your struggle doesn't end there. Because now you come across 餌食 (ejiki, prey) and you're thrown off again. And then there's a special case: 食器 (shokki, tableware). And these are all common words. And that's not even one of the harder kanji. 

Solution:
So 4 months ago I came up with a concept which I call "compound reading". You take the top 100 words from Jisho for a kanji and count the occurrences of each provided reading, but only the common words, with that you get the "scored reading". For 食 that would be "shoku.ku.geki.ta", so it's like giving the kanji a name. So when you now encounter a new word, you can be sure that you at least already know the reading, which makes memorization much easier. 
But that's still a shitty solution, as I had to find out the hard way, because although a reading can be frequently used in many words, it is often the case that it's bound to a "context", which is "followed by kanji", "followed by kana" or "special case, mostly with some specific kanji" (see also note below). Also the "scored reading" yields sometimes big strings, which are f-ing hard to memorize. Everything above 2 readings is f-ing hard, at least for my brain.

Note: Japanese learning books will tell you stuff about on-readings (Chinese reading) and kun-readings (Japanese reading), but that's utter bullshit, because in reality Japanese mixes it all up, all the time, in thousands of cases. So you can completely ignore it.

A better solution:
So you need "contextualized compound readings" which make everything MUCH easier and clearer and better. Instead of learning all readings you split them in the 3 mentioned categories. In the first phase you learn all fbKJ readings (red). After you mastered that you learn all fbKN readings (green), but with the context of the following hiragana (never alone). The frontpage of my Anki cards lists all words (extracted from mnemonic) and and you just make sure that you can read these words. So it's like learning English words, not much effort. And after that you learn the special cases (blue), which aren't automatically classified, again in context. And boom, the likelihood that you encounter a constallation that you haven't seen before is like 0,01% and you don't need to create memories, but rather you just attach new information to an existing memory, which is much easier. 