package utils;

import java.util.List;

public class Contract {
    private Contract() {
    }

    public static <T> T notNull(final T obj, final String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }

    public static <T> List<T> notEmpty(final List<T> list, final String message) {
        if (list.isEmpty())
            throw new IllegalArgumentException(message);
        return list;
    }
}
