package br.com.brunogodoif.projectmanagement.domain.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[\\w\\-\\.]+@[\\w\\-\\.]+\\.[a-zA-Z]{2,7}",
            Pattern.CASE_INSENSITIVE
                                                                );

    private static final int MAX_EMAIL_LENGTH = 254;

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        if (email.length() > MAX_EMAIL_LENGTH || email.isEmpty()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }
}