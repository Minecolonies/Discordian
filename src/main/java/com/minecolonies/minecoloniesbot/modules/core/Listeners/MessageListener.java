package com.minecolonies.minecoloniesbot.modules.core.Listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.minecolonies.minecoloniesbot.MinecoloniesBot;
import com.minecolonies.minecoloniesbot.util.URLParameterStringBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageListener extends ListenerAdapter
{

    private final MinecoloniesBot minecoloniesBot;

    public MessageListener(final MinecoloniesBot minecoloniesBot)
    {
        this.minecoloniesBot = minecoloniesBot;
        this.minecoloniesBot.getLogger().info("MessageListener Started!");
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event)
    {
        minecoloniesBot.getLogger().info("Message Received: {}, {}", event.getAuthor().getId(), event.getMessage().getContentRaw());
        if (event.getAuthor().getId().equals("315757273676906497"))
        {

            final String[] splitMessage = event.getMessage().getContentStripped().split(" ");

            minecoloniesBot.getLogger().info("message split: " + Arrays.toString(splitMessage));

            if (splitMessage[0].equalsIgnoreCase("!test"))
            {
                final StringBuilder responseBuilder = new StringBuilder();

                for (final String string : Arrays.asList(splitMessage).subList(1, splitMessage.length))
                {
                    responseBuilder.append(string).append(" ");
                }

                event.getMessage().getChannel().sendMessage("Reacting to !test: " + responseBuilder.toString()).submit();
            }

            if (splitMessage[0].equalsIgnoreCase("!t"))
            {
                try
                {
                    final StringBuilder responseBuilder = new StringBuilder();

                    for (final String string : Arrays.asList(splitMessage).subList(2, splitMessage.length))
                    {
                        responseBuilder.append(string).append(" ");
                    }


                    final URL url = new URL("https://translate.googleapis.com/translate_a/single");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("client", "gtx");
                    parameters.put("sl", "auto");
                    parameters.put("tl", splitMessage[1]);
                    parameters.put("dt", "t");
                    parameters.put("q", responseBuilder.toString());

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
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = reader.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    reader.close();

                    final JsonParser jsonParser = new JsonParser();

                    final JsonArray jsonArray = jsonParser.parse(response.toString()).getAsJsonArray();

                    minecoloniesBot.getLogger().info(jsonArray.get(0).getAsJsonArray().toString());

                    StringBuilder translationStringBuilder = new StringBuilder();

                    for (final JsonElement element : jsonArray.get(0).getAsJsonArray())
                    {
                        if (element instanceof JsonArray)
                        {
                            final String string = element.getAsJsonArray().get(0).getAsJsonPrimitive().toString();
                            translationStringBuilder.append(string.substring(1, string.length() - 1));
                        }
                    }

                    event.getMessage().getChannel().sendMessage(translationStringBuilder.toString()).submit();
                }
                catch (Exception e)
                {
                    event.getMessage().getChannel().sendMessage("Bot Errored. Check Logs.").submit();
                    minecoloniesBot.getLogger().error("Translation Crash: ", e);
                }
            }
        }
    }
}
