package converters.meknow.run;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateRepeaterJson {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(final String[] args) throws Exception {

        final List<Row> list = new ArrayList<>(0);
        final AtomicInteger index = new AtomicInteger();
        Files.readAllLines(MeKnowConfig.MEKNOW_FOLDER.resolve("iknow-repeater.txt")).forEach(line -> {
            list.add(new Row(index.getAndIncrement(), line.split("\t")));
        });
        System.out.println(GSON.toJson(list));
    }


    private static class Row {
        int index;
        String japanese;
        String english;
        String fileName;

        public Row(final int index, final String[] columns) {
            this.index = index;
            this.japanese = columns[0];
            this.english = columns[1];
            this.fileName = columns[2];
        }
    }

}
