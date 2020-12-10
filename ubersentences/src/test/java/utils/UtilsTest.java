package utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Utils.kanaToRomaji;

public class UtilsTest {

    @Test
    public void kanaToRomajiConversion() {
        assertThat(kanaToRomaji("かん"), is("kan"));
        assertThat(kanaToRomaji("カン"), is("kan"));
        assertThat(kanaToRomaji("べん"), is("kan"));
        assertThat(kanaToRomaji("x"), is("x"));
        assertThat(kanaToRomaji(""), is(""));
    }

}
