package scrapers.tanos;

import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scrapers.tanos.TanosUtils.TANOS_BASE_URL;

public class TanosUtilsTest {

    @Test
    public void asdf() {
        // shits got a fucking tab!!!
        final Pattern pattern = Pattern.compile("(\\p{L})+.*");
        final String sentence = "お座席のベルトをお締<b style=\"color:red\">めく</b>ださい。\tPlease fasten your seat belt.";
        final Matcher matcher = pattern.matcher(sentence);

        System.out.println(matcher.group(0));
        System.out.println(pattern.matcher(sentence).group(1));
    }

    @Test
    public void levelPage() throws Exception {
        final ResponseBody body = TanosUtils.getContentFromUrl(TANOS_BASE_URL + "/jlpt/jlpt5/grammar/");
        final Document doc = Jsoup.parse(body.string());
        for (final Element elem : doc.select(".nounderline")) {
            System.out.println(elem.text() + " - " + elem.attr("href"));
        }
    }

    @Test
    public void grammarItem() throws Exception {
        final ResponseBody body = TanosUtils.getContentFromUrl(TANOS_BASE_URL + "/jlpt/skills/grammar/sentences/?grammarid=535");
        final Document doc = Jsoup.parse(body.string());
        final List<TanosSentence> list = new ArrayList<>();
        for (final Element elem : doc.select("#contentright li")) {
            final TanosSentence sentence = new TanosSentence(elem.text(), null, null);
            System.out.println(sentence.getJapanese() + " - " + sentence.getEnglish());
            list.add(sentence);
        }
        System.out.println("-------------------------------------------------------");
        Collections.sort(list);
        if (list.size() > 10) {
            final List<TanosSentence> shortenedList = new ArrayList<>(list.subList(0, 10));
            list.clear();
            list.addAll(shortenedList);
        }
        list.forEach(sentence -> {
            System.out.println(sentence.getJapanese() + " - " + sentence.getEnglish());
        });
    }

    @Test
    public void yxcv() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("a", null);
        map.put("b", null);
        map.put("c", null);
        map.put("d", null);
        map.put("e", null);
        map.remove("b");
        map.put("b", null);

        map.forEach((key, value) -> {
            System.out.println(key);
        });

    }

    @Test
    public void sdfg() {
        Pattern pattern = Pattern.compile("～");
        String asdf = "～として";
        System.out.println(pattern.matcher(asdf).replaceAll(""));
    }

}
