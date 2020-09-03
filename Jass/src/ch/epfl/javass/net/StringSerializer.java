package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * StringSerializer : sérialisation et désérialisation des valeurs échangées
 * entre le client et le serveur
 * 
 * @author Amine Atallah (284592)
 * @author Mohamed Ali Dhraief (283509)
 */
public final class StringSerializer {

    // Constructeur privé
    private StringSerializer() {
    }

    /**
     * @param n:
     *            entier à sérializer
     * @return entier sérializé en base 16
     */
    public static String serializeInt(int n) {
        return Integer.toUnsignedString(n, 16);
    }

    /**
     * @param s
     *            : chaine à désérializer en entier
     * @return l'entier après déserialization
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    /**
     * @param n
     *            : long a sérializer
     * @return long sérializé en base 16
     */
    public static String serializeLong(long n) {
        return Long.toUnsignedString(n, 16);
    }

    /**
     * @param s
     *            : chaine à déserializer en long
     * @return long après déserialization
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }

    /**
     * @param s
     *            : chaine à sérializer en une autre chaine selon la convention UTF_8
     * @return chaine après sérialization de la chaine donnée en argument
     */
    public static String serializeString(String s) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param s
     *            : chaine à déserializer
     * @return une chaine après déserialization de la chaine donnée en argument
     */
    public static String deserializeString(String s) {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(s), StandardCharsets.UTF_8);
    }

    /**
     * @param ch
     *            : caractere de separation
     * @param strings
     *            : chaines à combiner
     * @return la chaîne composée des chaînes séparées par le séparateur
     */
    public static String combine(char ch, String... strings) {
        return String.join(ch + "", strings);
    }

    /**
     * @param ch
     *            : caractere de separation
     * @param s
     *            : chaine à séparer par le caractere de separation donné
     * @return tableau contenant les chaînes individuelles
     */
    public static String[] split(char ch, String s) {
        return s.split(ch + "");
    }
}
