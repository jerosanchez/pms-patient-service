package com.jerosanchez.pms_patient_service.test_helpers;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTestData {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    public static String randomString(int length) {
        String chars = ALPHABET + ALPHABET.toUpperCase() + DIGITS;
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String randomName() {
        // Generate a random first and last name (capitalized)
        String first = capitalize(randomString(5 + ThreadLocalRandom.current().nextInt(5)));
        String last = capitalize(randomString(5 + ThreadLocalRandom.current().nextInt(5)));
        return first + " " + last;
    }

    public static String randomEmail() {
        // Generate a unique-like email: random string + timestamp + domain
        String local = randomString(8) + System.nanoTime();
        String domain = randomString(5) + ".test";
        return local.toLowerCase() + "@" + domain.toLowerCase();
    }

    public static String randomAddress() {
        // Generate a random address: number + street name + type
        int number = 1 + ThreadLocalRandom.current().nextInt(9999);
        String street = capitalize(randomString(6 + ThreadLocalRandom.current().nextInt(6)));
        String type = new String[] { "St", "Ave", "Rd", "Blvd", "Ln" }[ThreadLocalRandom.current().nextInt(5)];
        return number + " " + street + " " + type;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static LocalDate randomDate(int startYear, int endYear) {
        int year = ThreadLocalRandom.current().nextInt(startYear, endYear + 1);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 28); // keep it simple
        return LocalDate.of(year, month, day);
    }
}
