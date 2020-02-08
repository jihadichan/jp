package core;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FrequencyDictionaryTest {

    @Test
    public void getFreqNF() {
        assertThat(FrequencyDictionary.freqNF.size(), is(18681)); // 1190 duplicates + illegal chars
        FrequencyDictionary.freqNF.forEach((key, value) -> System.out.println(value + ". " + "\"" + key + "\""));
    }

    @Test
    public void getFreqWK() {
        assertThat(FrequencyDictionary.freqWK.size(), is(19996)); // 3 duplicates + illegal chars
        FrequencyDictionary.freqWK.forEach((key, value) -> System.out.println(value + ". " + "\"" + key + "\""));
    }


    @Test
    public void loadList() {
        final Map<String, Integer> map = new LinkedHashMap<>();

        final AtomicInteger freqCounter = new AtomicInteger(1);
        try {
            for (final String line : Files.readLines(new File("freqNF"), Charset.defaultCharset())) {
                final String word = line.trim();
                if (!map.containsKey(word)) {
                    map.put(word, freqCounter.getAndIncrement());
                }
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load ");
        }

        System.out.println("Unique terms: " + map.size());
    }
}
