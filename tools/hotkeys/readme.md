# Hotkeys

## Notes

- Drag the bookmarklet from [this page](https://jihadichan.github.io/bookmarklet.html) into your browser toolbar.

- There are no IDs on the elements. If the script doesn't work then it's most likely due to some HTML changes. Copy the CSS selectors from the dev tools, replace in code, [minify](https://www.minifier.org/) and replace the HERE:

  ```javascript
  javascript:(function(){HERE})()
  ```



## Code

```javascript
var checkBoxEnglish = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(5) > input');
var checkBoxKanji = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(3) > input');
var checkBoxHirgana = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(1) > input');
document.onkeyup = function(e) {
  if (e.which == 69) {
    checkBoxEnglish.click();
  }
  if (e.which == 87) {
    checkBoxKanji.click();
  }
  if (e.which == 81) {
    checkBoxHirgana.click();
  }
}
```

