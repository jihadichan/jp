package core;

import org.junit.Test;

public class ConfusionDirectoryTest {

    @Test
    public void asdf() {
        ConfusionDirectory.dict.forEach((key, value) -> {
            System.out.println(key + " - " + value);
        });
    }

}
