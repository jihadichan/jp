package scrapers.tanos;

import okhttp3.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TanosUtils {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
    private static final OkHttpClient httpClient = new OkHttpClient();
    static final String TANOS_BASE_URL = "http://www.tanos.co.uk";
    static final List<String> jlptLevelUrls = new ArrayList<>();

    static {
        jlptLevelUrls.add(TANOS_BASE_URL + "/jlpt/jlpt5/grammar/");
        jlptLevelUrls.add(TANOS_BASE_URL + "/jlpt/jlpt4/grammar/");
        jlptLevelUrls.add(TANOS_BASE_URL + "/jlpt/jlpt3/grammar/");
        jlptLevelUrls.add(TANOS_BASE_URL + "/jlpt/jlpt2/grammar/");
        jlptLevelUrls.add(TANOS_BASE_URL + "/jlpt/jlpt1/grammar/");
    }

    static ResponseBody getContentFromUrl(final String url) throws Exception {
        final Request request = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .url(url)
                .build();
        final Call call = httpClient.newCall(request);
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
