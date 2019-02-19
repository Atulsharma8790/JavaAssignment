package com.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtil {
    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR);
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        boolean first = true;
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }

            sb.append(value);

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());

    }
}
