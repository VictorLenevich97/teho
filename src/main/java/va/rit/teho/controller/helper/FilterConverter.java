package va.rit.teho.controller.helper;

import java.util.List;

public class FilterConverter {
    private FilterConverter() {
    }

    public static <T> List<T> nullIfEmpty(List<T> collection) {
        return collection == null || collection.isEmpty() ? null : collection;
    }
}
