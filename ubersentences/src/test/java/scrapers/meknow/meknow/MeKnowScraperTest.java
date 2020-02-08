package scrapers.meknow.meknow;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.out;

public class MeKnowScraperTest {

    private final Set<String> uniqueSentenceCheck = new HashSet<>();
    private final List<MeKnowSentence> outputSentences = new ArrayList<>();

    @Test
    public void asdf() {
        System.setProperty("webdriver.gecko.driver", "data/geckodriver");
        System.setProperty("webdriver.chrome.driver", "data/chromedriver");
        final FirefoxOptions capabilities = new FirefoxOptions();
        capabilities.setCapability("marionette", true);
        capabilities.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        final Course course = new Course("Japanese Core 1000: Step 1", "https://iknow.jp/courses/566921");
        final WebDriver driver = new FirefoxDriver(capabilities);
        driver.get(course.getUrl());
        final List<WebElement> items = driver.findElements(By.cssSelector(".items li"));
        items.forEach(item -> {
            final String focusWord = this.getFocusWord(item);
            final List<TempSentence> sentences = this.getSentenceData(item, focusWord);
            sentences.forEach(tempSentence -> {
//                MeKnowSentence meKnowSentence = new MeKnowSentence(tempSentence, course);
//                if (!this.uniqueSentenceCheck.contains(meKnowSentence.getJapanese())) {
//
//                    outputSentences.add();
//                }
            });
        });

        driver.close();
        driver.quit();
    }

    private List<TempSentence> getSentenceData(final WebElement item, final String focusWord) {
        final List<TempSentence> sentenceList = new ArrayList<>();
        final List<WebElement> webElements = item.findElements(By.cssSelector(".sentence-text"));
        webElements.forEach(webElement -> {
            final TempSentence tempSentence = new TempSentence();

            try {
                tempSentence.japanese = webElement.findElement(By.cssSelector(".text")).getText();
            } catch (final Exception e) {
            }
            try {
                tempSentence.translit = webElement.findElement(By.cssSelector(".transliteration")).getText();
            } catch (final Exception e) {
            }
            try {
                tempSentence.english = webElement.findElement(By.cssSelector(".translation")).getText();
            } catch (final Exception e) {
            }
            tempSentence.focusWord = focusWord;

            if (tempSentence.japanese != null && tempSentence.english != null) {
                sentenceList.add(tempSentence);
            }
        });
        return sentenceList;
    }

    private String getFocusWord(final WebElement item) {
        final WebElement cueResponse;
        final String word;
        String translit;
        String wordTransl;
        try {
            cueResponse = item.findElement(By.cssSelector(".cue-response"));
        } catch (final Exception e) {
            return null;
        }
        try {
            word = cueResponse.findElement(By.cssSelector("a")).getText();
        } catch (final Exception e) {
            return null;
        }
        try {
            translit = cueResponse.findElement(By.cssSelector(".transliteration")).getText();
        } catch (final Exception e) {
            translit = "";
        }
        try {
            wordTransl = " - " + cueResponse.findElement(By.cssSelector(".response")).getText();
        } catch (final Exception e) {
            wordTransl = "";
        }

        return word + translit + wordTransl;
    }

    private class TempSentence {
        String japanese;
        String translit;
        String english;
        String focusWord;
    }
}
