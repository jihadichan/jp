# Audio 

Anki has some quirks when it comes to audio. When you use a compiled UberSentences deck then use 

```
<!-- When you use a compiled UberSentences deck and not a Yomichan deck,
then do this to enable audio: https://github.com/jihadichan/jp/blob/master/ubersentences/audio.md -->
<div id="mp3" class="anki-field">{{mp3}}</div>
<div class="anki-field">[sound:ubersentences/mp3/{{mp3}}]</div>
```

to inject the MP3 into the template. If you use a Yomichan deck then use

```
<!-- When you use a compiled UberSentences deck and not a Yomichan deck,
then do this to enable audio: https://github.com/jihadichan/jp/blob/master/ubersentences/audio.md -->
<div class="anki-field">{{mp3}}</div>
```



**Notes**

- Anki ignores comments, i.e. don't just comment out the lines, replace them. 
- If in a compiled UberSentences deck the MP3 field is empty then Anki loads every single MP3 file in `ubersentences/mp3/`. You can only abort the playback by shutting down the program. 
