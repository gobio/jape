package eu.gobio.jape.utils;

import java.util.Objects;

public class Utils {
    public static String objectCode(Object o) {
        return o != null ? o.getClass().getSimpleName() + "@" + Integer.toHexString(Objects.hashCode(o)) : "null";
    }
}
