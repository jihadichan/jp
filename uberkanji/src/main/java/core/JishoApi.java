package core;

import models.KanjiData;
import models.jisho.JishoResult;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.JpHelpers.GSON;

public class JishoApi {
    private static final String API_BASE_URL = "https://jisho.org/api/v1/search/words?keyword=";
    private static final String WEBSITE_BASE_URL = "https://jisho.org/search/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
    private final OkHttpClient client = new OkHttpClient();

    /** Lookup for a single kanji. Note: This encapsulates kanji as *朝*.　 */
    public JishoResult getWordsForSingleKanji(final KanjiData kanjiData, final int pageDepth) throws Exception {

        JishoResult jishoResult = null;
        for (int page = 1; page <= pageDepth; page++) {
            final ResponseBody responseBody = this.getResponseBodyFromWordApi(kanjiData, page);

            if (jishoResult == null) {
                jishoResult = GSON.fromJson(responseBody.string(), JishoResult.class);
            } else {
                final JishoResult tempResult = GSON.fromJson(responseBody.string(), JishoResult.class);
                if (tempResult.getData().isEmpty()) {
                    break;
                }
                jishoResult.getData().addAll(tempResult.getData());
            }
        }

        return jishoResult;
    }

    public KanjiData getDataForKanji(final KanjiData kanjiData) throws Exception {
        final ResponseBody responseBody = this.getResponseBodyFromKanjiPage(kanjiData.getKanji());
        final Document doc = Jsoup.parse(responseBody.string());


        final List<String> kunReadingsList = new ArrayList<>();
        final Elements kunReadings = doc.select(".kanji-details__main-readings .kun_yomi a");
        for (final Element reading : kunReadings) {
            kunReadingsList.add(reading.text().trim());
        }

        final List<String> onReadingsList = new ArrayList<>();
        final Elements onReadings = doc.select(".kanji-details__main-readings .on_yomi a");
        for (final Element reading : onReadings) {
            onReadingsList.add(reading.text().trim());
        }

        int frequency = 9999;
        final Element frequencyElem = doc.select(".frequency strong").first();
        if (frequencyElem != null) {
            frequency = Integer.valueOf(frequencyElem.text());
        }

        String meanings = null;
        final Element meaningsElem = doc.select(".kanji-details__main-meanings").first();
        if (meaningsElem != null) {
            meanings = meaningsElem.text();
        }

        int strokes = 0;
        final Element strokesElem = doc.select(".kanji-details__stroke_count strong").first();
        if (strokesElem != null) {
            strokes = Integer.valueOf(strokesElem.text());
        }

        return new KanjiData(
                kanjiData.getKanji(),
                kanjiData.getId(),
                meanings,
                frequency,
                onReadingsList,
                kunReadingsList,
                strokes,
                kanjiData.getComponents(),
                kanjiData.getRtkKeyword(),
                kanjiData.getRtkIndex());
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    @NotNull
    private ResponseBody getResponseBodyFromWordApi(final KanjiData kanjiReading, final int page) throws IOException {
        final Request request = new Request.Builder()
                .url(API_BASE_URL + "*" + kanjiReading.getKanji() + "*&page=" + page)
                .build();
        final Call call = this.client.newCall(request);
        final Response response = call.execute();
        if (response.code() != 200) {
            throw new IllegalStateException("Response came back as " + response.code());
        }

        final ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IllegalStateException("Response body must not be NULL. ");
        }
        return responseBody;
    }

    @NotNull
    private ResponseBody getResponseBodyFromKanjiPage(final char kanji) throws IOException {
        final Request request = new Request.Builder()
                .url(WEBSITE_BASE_URL + kanji + "%20%23kanji")
                .header("User-Agent", USER_AGENT)
                .build();
        final Call call = this.client.newCall(request);
        final Response response = call.execute();
        if (response.code() != 200) {
            throw new IllegalStateException("Response came back as " + response.code());
        }

        final ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IllegalStateException("Response body must not be NULL. ");
        }
        return responseBody;
    }

}
