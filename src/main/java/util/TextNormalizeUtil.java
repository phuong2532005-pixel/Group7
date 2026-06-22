/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 01/04/2026
 * Description : Ham chuan hoa van ban (loai dau tieng viet, chuan hoa khoang trang, to lower).
 */
package util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizeUtil {
    private TextNormalizeUtil() {}

    public static String normalizeCity(String city) {
        if (city == null) {
            return null;
        }

        String normalized = normalizeNoAccent(city);
        if (normalized == null) {
            return null;
        }

        normalized = normalized
                .replaceFirst("^(thanh pho|tp\\.?|tinh) ", "")
                .trim();

        if (normalized.equals("tp.hcm") || normalized.equals("tphcm") || normalized.equals("ho chi minh")
                || normalized.equals("tp ho chi minh") || normalized.equals("thanh pho ho chi minh")) {
            return "ho chi minh";
        }

        return normalized;
    }

    public static String normalizeDistrictOrStreet(String value) {
        String normalized = normalizeNoAccent(value);
        if (normalized == null) {
            return null;
        }

        normalized = normalized.replaceFirst("^(quan|huyen|thi xa|thanh pho|tp|phuong|xa) ", "").trim();
        return normalized;
    }

    public static String normalizeNoAccent(String value) {
        if (value == null) {
            return null;
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized;
    }
}
