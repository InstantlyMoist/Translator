package me.kyllian.translator.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationHandler {

    public String translate(String message, Language from, Language to) throws Exception {
        System.setProperty("http.agent", "Chrome");
        String link = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%from%&tl=%to%&dt=t&q=%text%";
        URL url = new URL(link.replace("%from%", from == Language.unknown ? "auto" :  from.toString()).replace("%to%", to.toString()).replace("%text%", URLEncoder.encode(message, "UTF-8")));
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
        String input = reader.readLine();
        reader.close();

        input = input.substring(1, input.lastIndexOf("]]") + 2);
        String[][] output = new Gson().fromJson(input, new TypeToken<String[][]>() {}.getType());
        StringBuilder builder = new StringBuilder();
        for (String[] outer : output) {
            builder.append(outer[0]).append(" ");
        }
        return builder.toString().trim();
    }
}
