package com.minecolonies.discordianbot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple util class that uses google translate API.
 */
public class GoogleTranslate
{

    public static String translate(final String input, final String resultLanguage) throws Exception
    {

        final URL url = new URL("https://translate.googleapis.com/translate_a/single");


        Map<String, String> parameters = new HashMap<>();
        parameters.put("client", "gtx");
        parameters.put("sl", "auto");
        parameters.put("tl", resultLanguage);
        parameters.put("dt", "t");
        parameters.put("q", input);

        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(URLParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream())
        );

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null)
        {
            response.append(inputLine);
        }
        reader.close();

        final JsonParser jsonParser = new JsonParser();

        final JsonArray jsonArray = jsonParser.parse(response.toString()).getAsJsonArray();

        StringBuilder translationStringBuilder = new StringBuilder();

        for (final JsonElement element : jsonArray.get(0).getAsJsonArray())
        {
            if (element instanceof JsonArray)
            {
                final String string = element.getAsJsonArray().get(0).getAsJsonPrimitive().toString();
                translationStringBuilder.append(string.substring(1, string.length() - 1));
            }
        }

        return translationStringBuilder.toString();
    }
}
