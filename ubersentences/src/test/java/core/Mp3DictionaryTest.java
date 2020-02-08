package core;

import models.Sentence;
import org.junit.Test;

import static org.junit.Assert.*;

public class Mp3DictionaryTest {

    @Test
    public void create() {
        System.out.println(Mp3Dictionary.get("新しいアニメは面白いです"));
    }

}
