package com.ratik.uttam.utils;

import com.ratik.uttam.Constants;
import com.ratik.uttam.model.Photo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Ratik on 08/09/17.
 */

public class FetchUtils {
    public static Photo parsePhoto(String jsonData) throws JSONException {
        Photo p = new Photo();
        JSONObject object = new JSONObject(jsonData);

        JSONObject urls = object.getJSONObject(Constants.CONST_URLS);
        p.setUrlFull(urls.getString(Constants.CONST_URL_FULL));

        JSONObject user = object.getJSONObject(Constants.CONST_USER);
        p.setPhotographer(user.getString(Constants.CONST_NAME));
        p.setUserProf(user.getJSONObject(Constants.CONST_LINKS).getString("html"));

        JSONObject links = object.getJSONObject(Constants.CONST_LINKS);
        p.setHtmlUrl(links.getString(Constants.CONST_HTML));
        p.setDownloadUrl(links.getString(Constants.CONST_DOWNLOAD));
        return p;
    }

    public static String getRandomCategory() {
        // 2 == Buildings, 4 == Nature
        int[] categories = {2, 4};
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return "" + categories[index];
    }
}
