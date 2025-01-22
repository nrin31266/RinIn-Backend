package com.linkedin.backend.features.authentication.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OneTimePasswordGenerator {
    private final String NUMERIC_STRING = "0123456789";
    private final int OTP_LENGTH = 6;

    public String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(NUMERIC_STRING.length());
            otp.append(NUMERIC_STRING.charAt(index));
        }
        return otp.toString();
    }
}
