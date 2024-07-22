package nl.sudsandbuds.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeUtilities {
    /**
     * Utility common method that returns an encoded url
     * @param toBeEncoded url to be encoded
     */
    @SuppressWarnings({"unused","unchecked"})
    public static String encodeURI(String toBeEncoded) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(toBeEncoded, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            encoded = toBeEncoded;
        }
        return encoded;
    }
}
