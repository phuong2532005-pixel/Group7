/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 31/03/2026
 * Description : Ham tien ich ma hoa/ hash (md5, sha) va doi chuoi.
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HashUtil {

    private HashUtil() {
    }

    public static String md5(String input) {
        return md5(input, StandardCharsets.UTF_16LE);
    }

    public static String md5Utf8(String input) {
        return md5(input, StandardCharsets.UTF_8);
    }

    public static String md5Windows1258(String input) {
        return md5(input, Charset.forName("windows-1258"));
    }

    private static String md5(String input, Charset charset) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // SQL Server HASHBYTES on NVARCHAR uses UTF-16LE encoding.
            byte[] digest = md.digest(input.getBytes(charset));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 not available", e);
        }
    }
}
