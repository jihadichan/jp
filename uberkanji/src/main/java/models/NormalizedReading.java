package models;

import utils.JpHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class NormalizedReading implements Comparable<NormalizedReading> {

    private final String raw;
    private final String rawCleaned;
    private final String normalized;
    private final Integer priority;
    private final Pattern afterDot = Pattern.compile("\\..*");
    private final Pattern dash = Pattern.compile("-");

    public NormalizedReading(final String raw) {
        this.raw = raw;
        this.rawCleaned = this.normalize(raw);
        this.normalized = this.normalize(JpHelpers.kataToHira(raw));
        this.priority = this.normalized.length();
    }

    /** Creates list with unique reading. Duplicates sorted out */
    public static List<NormalizedReading> createList(final List<String> onReadings, final List<String> kunReadings) {
        final List<NormalizedReading> list = new ArrayList<>();
        final Set<String> existingReadings = new HashSet<>();

        onReadings.forEach(reading -> {
            addToList(list, existingReadings, reading);
        });
        kunReadings.forEach(reading -> {
            addToList(list, existingReadings, reading);
        });
        Collections.sort(list);

        return list;
    }

    private static void addToList(final List<NormalizedReading> list, final Set<String> existingReadings, final String reading) {
        final NormalizedReading normalizedReading = new NormalizedReading(reading);
        if (!existingReadings.contains(normalizedReading.normalized)) {
            list.add(normalizedReading);
            existingReadings.add(normalizedReading.normalized);
        }
    }

    private String normalize(final String raw) {
        String temp = raw;
        temp = this.afterDot.matcher(temp).replaceAll("");
        temp = this.dash.matcher(temp).replaceAll("");
        return temp;
    }

    public String getRaw() {
        return this.raw;
    }

    public String getRawCleaned() {
        return this.rawCleaned;
    }

    public String getNormalized() {
        return this.normalized;
    }

    @Override
    public String toString() {
        return this.raw + " -> " + this.normalized;
    }

    @Override
    public int compareTo(@NotNull final NormalizedReading o) {
        return o.priority.compareTo(this.priority);
    }
}
