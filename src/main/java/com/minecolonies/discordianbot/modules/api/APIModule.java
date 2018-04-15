package com.minecolonies.discordianbot.modules.api;

import com.minecolonies.discordianbot.modules.api.config.APIConfig;
import com.minecolonies.discordianbot.modules.api.config.APIConfigAdapter;
import com.minecolonies.discordianbot.qsml.modulespec.ConfigurableModule;
import com.minecolonies.discordianconnect.DiscordianConnectAPI;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import com.minecolonies.discordianconnect.api.connection.auth.IDiscordianConnectAuthenticationBuilder;
import com.minecolonies.discordianconnect.api.message.IDiscordianConnectMessage;
import lombok.Getter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@ModuleData(id = APIModule.ID, name = "API")
public class APIModule extends ConfigurableModule<APIConfigAdapter>
{
    /**
     * This modules ID string.
     */
    public static final String ID = "API";

    @Override
    public void enable()
    {
        getLogger().info("API Module Started");
        this.getDiscordianBot().getConfigUtils().put(ID, getAdapter().getNodeOrDefault());

        URL apiURL = null;

        try
        {
            apiURL = new URL("http://localhost/hubs/discordian");
        }
        catch (MalformedURLException e)
        {
            getLogger().error("Unable to create url: ", e);
        }

        if (apiURL != null)
        {
            final URL finalApiURL = apiURL;
            final IDiscordianConnectConnection connection = DiscordianConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IDiscordianConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> {
                    eventBuilder.registerMessageHandler("MinecraftChatMessage", this::onMinecraftChatMessageEvent);
                    eventBuilder.registerMessageHandler("DiscordChatMessage", this::onDiscordChatMessageEvent);
                });

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));
            });

            connection.connect();
            getDiscordianBot().setConnection(connection);
            connection.send("MinecraftChatMessage",  123, "abc", "Door Ray Meeee", apiURL);

            getDiscordianBot().getJda().addEventListener(new MessageListener(getDiscordianBot()));
        }
    }

    private void messageHandler(IDiscordianConnectMessage message)
    {
        getLogger().info("message! {}", Arrays.toString(message.getArguments()));
    }

    private void errorHandler(Throwable thrown)
    {
        getLogger().error("API ERROR: ", thrown);
    }

    private void setAuthenticationToken(IDiscordianConnectAuthenticationBuilder builder)
    {
        APIConfig config = (APIConfig) getDiscordianBot().getConfigUtils().get(ID);

        builder.withBearerToken(config.apiToken);
    }

    private void onMinecraftChatMessageEvent(IDiscordianConnectMessage message)
    {
        if (message != null)
        {
            getLogger().info("Minecraft Chat Message: {}", Arrays.toString(message.getArguments()));
        }
    }

    private void onDiscordChatMessageEvent(IDiscordianConnectMessage message)
    {
        if (message != null)
        {
            getLogger().info("Discord Chat Message: {}", Arrays.toString(message.getArguments()));
        }
    }

    @Override
    protected APIConfigAdapter createConfigAdapter()
    {
        return new APIConfigAdapter();
    }
}
