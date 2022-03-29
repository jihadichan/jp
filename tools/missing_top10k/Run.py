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
    for word in rawJson:
        asDict.update({word[0]: word[2]})
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
        knownWords.update({line: line})

    return knownWords


if __name__ == '__main__':
    top10k = createTop10kDict(Path("top10k.json"))
    knownWords = createKnownWordsList(Path("known_words.txt"))
    known = 0
    unknown = 0
    unknownDict = {}
    for word, freq in top10k.items():
        if knownWords.get(word):
            known += 1
        else:
            unknown += 1
            unknownDict.update({word: freq})

    print(f"known: {known}")
    print(f"unknown: {unknown}")

    # for word in unknownDict:
    #     print(word)

    pass
