import json
import re
from pathlib import Path
from typing import List


def createTop10kDict(path: Path) -> dict:
    try:
        with open(path, 'r') as file:
            rawJson = json.load(file)
    except Exception as e:
        raise e

    asDict = {}
    rank = 1
    for word in rawJson:
        asDict.update({word[0]: rank})
        rank += 1
    return asDict


def createKnownWordsList(path: Path) -> dict:
    try:
        with open(path, 'r') as file:
            rawList = file.readlines()
    except Exception as e:
        raise e

    knownWords = {}
    for line in rawList:
        line = line.strip()
        line = re.sub("<rt>.*?</rt>", "", line)
        line = re.sub("</?ruby>", "", line)
        knownWords.update({line.strip(): line})

    return knownWords


def writeToFile(path: Path, content: str):
    try:
        with open(path, "w") as file:
            file.write(content)
    except Exception as e:
        raise e


if __name__ == '__main__':
    top10k = createTop10kDict(Path("top10k.json"))
    knownWords = createKnownWordsList(Path("known_words.txt"))
    knownTop10k = 0
    knownTop5k = 0
    knownTop3k = 0
    knownTop2k = 0
    knownTop1k = 0
    unknown = 0
    unknownDict = {}
    results = ""
    for word, freq in top10k.items():

        if not knownWords.get(word):
            unknown += 1
            unknownDict.update({word: freq})

            if freq <= 1000:
                knownTop1k += 1

            if freq <= 2000:
                knownTop2k += 1

            if freq <= 3000:
                knownTop3k += 1

            if freq <= 5000:
                results += f"- <a href='https://jisho.org/search/{word}' target='_blank'>{word}</a><br>\n"
                knownTop5k += 1
        else:
            knownTop10k += 1

    print(f"\ntotal known: {len(knownWords)}")
    print(f"top10k: {knownTop10k}")
    print(f"top5k: {knownTop5k}")
    print(f"top3k: {knownTop3k}")
    print(f"top2k: {knownTop2k}")
    print(f"top1k: {knownTop1k}")
    print(f"unknown: {unknown}")

    writeToFile(Path("results.md"), results)

    pass
