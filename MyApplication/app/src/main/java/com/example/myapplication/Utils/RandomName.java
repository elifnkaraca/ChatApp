package com.example.myapplication.Utils;

import java.util.Random;

public class RandomName {

    public static String getSaltString() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rand = new Random();

        while (salt.length() < 18) {
            int index = (int) (rand.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String salsStr = salt.toString();
        return salsStr;
    }
}

