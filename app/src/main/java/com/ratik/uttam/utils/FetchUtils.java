package com.ratik.uttam.utils;

import java.util.Random;

/**
 * Created by Ratik on 08/09/17.
 */

public class FetchUtils {
    public static String getRandomCategory() {
        // 2 == Buildings, 4 == Nature
        int[] categories = {2, 4};
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return "" + categories[index];
    }
}
