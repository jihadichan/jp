package scrapers.meknow.meknow;

import com.google.common.io.Files;
import com.google.gson.Gson;
import core.OutputCsvWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.System.out;

public class MeKnowScraper {

    private final List<Course> courses = loadCoursesList(new File("data/meknow-urls.json"));
    private final Map<String, String> uniqueSentenceCheck = new HashMap<>();
    private final OutputCsvWriter csvWriter = new OutputCsvWriter();
    private static final WebDriver driver;

    static {
        System.setProperty("webdriver.gecko.driver", "data/geckodriver");
        System.setProperty("webdriver.chrome.driver", "data/chromedriver");
        final FirefoxOptions capabilities = new FirefoxOptions();
        capabilities.setCapability("marionette", true);
        capabilities.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        driver = new FirefoxDriver(capabilities);
    }

    public void run() {

        this.courses.forEach(course -> {
            out.println("Getting: " + course.getName() + " - " + course.getUrl());

            driver.get(course.getUrl());
            final List<WebElement> items = driver.findElements(By.cssSelector(".items li"));
            items.forEach(item -> {
                final String focusWord = this.getFocusWord(item);
                final List<TempSentence> sentences = this.getSentenceData(item, focusWord);
                sentences.forEach(tempSentence -> {
                    final MeKnowSentence meKnowSentence = new MeKnowSentence(tempSentence, course);
                    if (!this.uniqueSentenceCheck.containsKey(meKnowSentence.getJapanese())) {
                        this.uniqueSentenceCheck.put(meKnowSentence.getJapanese(), course.getName());
                        this.csvWriter.append(new String[]{
                                meKnowSentence.getJapanese(),
                                meKnowSentence.getEnglish(),
                                meKnowSentence.getSource()
                        });
                    } else {
                        out.println("Duplicate: " + meKnowSentence.getJapanese() + " - Already found in: " + this.uniqueSentenceCheck.get(meKnowSentence.getJapanese()));
                    }
                });
            });
//            this.csvWriter.flush(); // todo mmmh... something is really wrong with this. Takes ages.
            out.println("Done.");
        });

        this.csvWriter.close();
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
                // NO OP
            }
            try {
                tempSentence.translit = webElement.findElement(By.cssSelector(".transliteration")).getText();
            } catch (final Exception e) {
                // NO OP
            }
            try {
                tempSentence.english = webElement.findElement(By.cssSelector(".translation")).getText();
            } catch (final Exception e) {
                // NO OP
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

    static class TempSentence {
        String japanese;
        String translit;
        String english;
        String focusWord;
    }

    private static List<Course> loadCoursesList(final File file) {
        if (!file.exists())
            throw new IllegalStateException("Couldn't open course list: " + file);

        try {
            return Arrays.asList(new Gson().fromJson(new String(Files.toByteArray(file)), Course[].class));
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load " + file);
        }
    }

}
