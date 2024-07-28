package de.arvato.stratego.util;

import java.security.SecureRandom;

public class RandomKeyGenerator {

    private static final String UPPERCASE_AND_DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int KEY_LENGTH = 4;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomKey(int length) {
        StringBuilder keyBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(UPPERCASE_AND_DIGITS.length());
            keyBuilder.append(UPPERCASE_AND_DIGITS.charAt(randomIndex));
        }
        return keyBuilder.toString();
    }
}