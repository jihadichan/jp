package models;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NormalizedReadingTest {

    @Test
    public void create() {

        assertThat(new NormalizedReading("しりぞ.ける").toString(), is("しりぞ.ける -> しりぞ"));
        assertThat(new NormalizedReading("とど.まる").toString(), is("とど.まる -> とど"));
        assertThat(new NormalizedReading("-づら.い").toString(), is("-づら.い -> づら"));
        assertThat(new NormalizedReading("はや-").toString(), is("はや- -> はや"));
        assertThat(new NormalizedReading("-べ").toString(), is("-べ -> べ"));
        assertThat(new NormalizedReading("シュウ").toString(), is("シュウ -> しゅう"));
    }

    @Test
    public void sort() {

        final List<String> onReadings = Arrays.asList("キュウ");
        final List<String> kunReadings = Arrays.asList("あだ", "あた", "かたき");

        final List<NormalizedReading> normalizedReadings = NormalizedReading.createList(onReadings, kunReadings);
        normalizedReadings.forEach(reading -> {
            out.println(reading);
        });

    }
}
