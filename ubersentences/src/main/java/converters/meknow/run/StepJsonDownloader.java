package converters.meknow.run;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static converters.meknow.run.MeKnowConfig.MEKNOW_PAGES;
import static converters.meknow.run.MeKnowConfig.MEKNOW_STEPS_FOLDER;

/**
 * Download all API data responses which they use to build the pages
 * These: https://iknow.jp/api/v2/goals/566922?
 */
public class StepJsonDownloader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36";

    public static void main(final String[] args) throws Exception {
        final String pages = Files.readString(MEKNOW_PAGES);

        final PageData[] pageDataList = GSON.fromJson(pages, PageData[].class);
        Stream.of(pageDataList).forEach(pageData -> {
            final SectionData sectionData = convert(pageData);
            System.out.println(sectionData.apiUrl + " - Level: " + sectionData.level + " - " + sectionData.step);
            final String json = getUrl(sectionData.apiUrl);
            final String fileName = sectionData.level + "" + sectionData.step;
            writeSectionJson(MEKNOW_STEPS_FOLDER.resolve(fileName), json);
        });
    }

    private static void writeSectionJson(final Path path, final String content) {
        try (final Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.write(content);
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getUrl(final String url) {
        try {
            return Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT).execute().body();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static SectionData convert(final PageData pageData) {
        final SectionData sectionData = new SectionData();
        sectionData.apiUrl = pageData.path.replaceAll("https://iknow.jp/courses/", "https://iknow.jp/api/v2/goals/") + "?";
        final String[] order = pageData.name.split(":");
        sectionData.level = Integer.parseInt(order[0].replaceAll("[^1-9]", ""));
        sectionData.step = Integer.parseInt(order[1].replaceAll("[^\\d]", "")) - 1;
        return sectionData;
    }

    static class PageData {
        String path;
        String name;
    }

    static class SectionData {
        String apiUrl;
        int level;
        int step;
    }

}
