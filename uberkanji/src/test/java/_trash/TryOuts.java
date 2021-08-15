package _trash;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import org.junit.Test;
import utils.RomajiConverter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class TryOuts {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");

    @Test
    public void asdf() {
        System.out.println(this.simpleDateFormat.format(new Date()));
    }

    @Test
    public void yxvxcv() {
        final Map<String, Integer> mostCommonReadings = new HashMap<>();
        mostCommonReadings.put("a", 10);
        mostCommonReadings.put("b", 20);
        mostCommonReadings.put("c", 15);
        mostCommonReadings.put("d", 12);

        final Map<String, Integer> scoredReadings = new LinkedHashMap<>();
        scoredReadings.put("a", 4);
        scoredReadings.put("b", 2);
        scoredReadings.put("c", 4);
        scoredReadings.put("d", 4);

        final Map<String, Integer> sortedMap = new LinkedHashMap<>();
        scoredReadings.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .sorted((o1, o2) -> {
                    if(!o1.getValue().equals(o2.getValue())) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                    return mostCommonReadings.get(o2.getKey()).compareTo(mostCommonReadings.get(o1.getKey()));
                })
                .forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        sortedMap.forEach((key, value) -> System.out.println(key + " - " + value));
    }

    @Test
    public void sdfsdf() {
        System.out.println(RomajiConverter.fromKatakana("コンニチハ"));
        System.out.println(RomajiConverter.fromHiragana("こんにちは"));
        System.out.println(RomajiConverter.fromHiragana("ジュウ.ジ.トオ"));
    }

}
