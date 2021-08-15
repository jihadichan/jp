package scrapers.tanos;

import core.OutputCsvWriter;
import models.Sentence;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;
import static scrapers.tanos.TanosUtils.TANOS_BASE_URL;
import static scrapers.tanos.TanosUtils.jlptLevelUrls;

public class TanosScraper {

    private final OutputCsvWriter csvWriter = new OutputCsvWriter();
    final static Map<String, TanosSentence> outputSentences = new LinkedHashMap<>();

    public void run() {

        final AtomicInteger progressCounter = new AtomicInteger(1);

        // Get grammar item URLs
        final List<GrammarItem> grammarItems = this.getGrammarItems();

        // Get TanosSentences from each grammar point
        for (final GrammarItem grammarItem : grammarItems) {
            this.getJSoupDocument(grammarItem.url).ifPresent(doc -> {
                final List<TanosSentence> itemSentences = new ArrayList<>();
                for (final Element elem : doc.select("#contentright li")) {
                    try {
                        itemSentences.add(new TanosSentence(elem.text(), grammarItem.url, grammarItem.name));
                    } catch (final Exception e) {
                        out.println("++++++++++++++ ERROR: " + e.getMessage());
                    }
                }
                // Add to output sentences
                List<TanosSentence> shortenedList = this.shortenList(itemSentences);
                this.shortenList(itemSentences).forEach(sentence -> {
                    if(outputSentences.containsKey(sentence.getJapanese())) {
                        outputSentences.remove(sentence.getJapanese());
                    }
                    outputSentences.put(sentence.getJapanese(), sentence);
                });
                out.println(progressCounter.getAndIncrement() + ". Finished " + grammarItem.name + " - Sentences: " + itemSentences.size());
            });
//            break; // todo delete
        }

        // Write to CSV
        outputSentences.forEach((key, sentence) -> this.csvWriter.append(new String[]{
                sentence.getJapanese(),
                sentence.getEnglish(),
                sentence.getSource()
        }));
        this.csvWriter.close();

    }

    private List<TanosSentence> shortenList(final List<TanosSentence> itemSentences) {
        Collections.sort(itemSentences);
        if (itemSentences.size() > 10) {
            final List<TanosSentence> shortenedList = new ArrayList<>(itemSentences.subList(0, 10));
            itemSentences.clear();
            itemSentences.addAll(shortenedList);
        }
        return itemSentences;
    }

    private List<GrammarItem> getGrammarItems() {
        final List<GrammarItem> grammarItems = new ArrayList<>();
        for (final String levelUrl : jlptLevelUrls) {
            this.getJSoupDocument(levelUrl).ifPresent(doc -> {
                for (final Element elem : doc.select(".nounderline")) {
                    grammarItems.add(new GrammarItem(elem.text(), elem.attr("href")));
                }
            });
//            break; // todo delete
        }
        return grammarItems;
    }

    private Optional<Document> getJSoupDocument(final String url) {
        try {
            final ResponseBody body = TanosUtils.getContentFromUrl(url);
            return Optional.of(Jsoup.parse(body.string()));
        } catch (final Exception e) {
            out.println("++++++++++++++ ERROR: Couldn't get " + url);
            return Optional.empty();
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // INNER CLASSES
    // ------------------------------------------------------------------------------------------ //

    class GrammarItem {
        final String name;
        final String url;

        public GrammarItem(final String name, final String url) {
            this.name = name;
            this.url = TANOS_BASE_URL + url;
        }

        public String getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }
    }

}
