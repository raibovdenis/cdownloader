package com.ecwid.cdownloader.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FormatterUtil
 */
public class FormatterUtil {
    /**
     * Set suffix map
     */
    private static final Map<String, Long> suffixMap = new LinkedHashMap<String, Long>() {{
        put("b", 1L);
        put("k", 1024L);
        put("m", 1024L * 1024);
        put("g", 1024L * 1024 * 1024);
    }};

    private static final String SECOND_CODE = "s";

    /**
     * Count millisecond in second
     */
    private static final int MILLISECOND_IN_SECOND = 1000;

    /**
     * Get suffix list string
     *
     * @return
     */
    private static String getSuffixListString() {
        return suffixMap.keySet().stream().collect(Collectors.joining(""));
    }

    /**
     * Get suffix value
     *
     * @param key
     * @return
     */
    private static long getSuffixValue(String key) {
        return suffixMap.get(key).longValue();
    }

    /**
     * Parse byte size
     *
     * @param byteSize
     * @return
     */
    public static long parseByteSize(String byteSize) {
        long result;

        /** Prepare string */
        String str = byteSize.trim().toLowerCase();

        /** Get suffix list string */
        String suffixListString = getSuffixListString();

        /** Check string */
        if (!Pattern.matches("(?i)^[0-9]+[" + suffixListString + "]?$", str)) {
            throw new IllegalArgumentException("Can not parse string " + "\"" + str + "\" to byte size");
        }

        /** Get last char */
        String lastChar = String.valueOf(str.charAt(str.length() - 1));

        /** Try parse */
        if (suffixMap.containsKey(lastChar)) {
            long suffixValue = getSuffixValue(lastChar);
            String tempStr = str.substring(0, str.length() - 1);
            result = Long.parseLong(tempStr) * suffixValue;
        } else {
            result = Long.parseLong(str);
        }

        return result;
    }

    /**
     * Format time
     *
     * @param time
     * @return
     */
    public static double formatTime(long time) {
        return (double) time / MILLISECOND_IN_SECOND;
    }

    /**
     * Format time
     *
     * @param time
     * @param toString
     * @return
     */
    public static String formatTime(long time, boolean toString) {
        return formatTime(time) + SECOND_CODE;
    }

    /**
     * Format byte size
     *
     * @param byteSize
     * @return
     */
    public static String formatByteSize(double byteSize) {
        if (byteSize < 0) {
            throw new IllegalArgumentException("Byte size must be greater than 0");
        }

        if (byteSize == 0) {
            return "" + 0;
        }

        String result = "";

        /** Get list keys */
        List<String> keys = new ArrayList<String>(suffixMap.keySet());

        /** Get list values */
        List<Long> values = new ArrayList<Long>(suffixMap.values());

        String formatType = "%.2f";

        for (int currentIndex = 0, nextIterationIndex = 1; currentIndex < values.size(); currentIndex++, nextIterationIndex++) {
            long lowLimit = values.get(currentIndex);

            /** Not last iteration */
            if (nextIterationIndex != values.size()) {
                long highLimit = values.get(nextIterationIndex);

                if (byteSize >= lowLimit && byteSize < highLimit) {
                    result = String.format(formatType, byteSize / lowLimit) + keys.get(currentIndex).toUpperCase();
                    break;
                }
            } else {
                /** Last iteration */
                if (byteSize >= lowLimit) {
                    result = String.format(formatType, byteSize / lowLimit) + keys.get(currentIndex).toUpperCase();
                    break;
                }
            }
        }

        return result;
    }

}
