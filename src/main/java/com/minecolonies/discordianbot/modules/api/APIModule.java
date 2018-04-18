package com.minecolonies.discordianbot.modules.api;

import com.minecolonies.discordianbot.modules.api.config.APIConfig;
import com.minecolonies.discordianbot.modules.api.config.APIConfigAdapter;
import com.minecolonies.discordianbot.modules.api.handlers.MinecraftHandlers;
import com.minecolonies.discordianbot.qsml.modulespec.ConfigurableModule;
import com.minecolonies.discordianconnect.DiscordianConnectAPI;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import com.minecolonies.discordianconnect.api.connection.auth.IDiscordianConnectAuthenticationBuilder;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.net.MalformedURLException;
import java.net.URL;

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

        APIConfig config = (APIConfig) getDiscordianBot().getConfigUtils().get(ID);

        URL apiURL = null;

        try
        {
            apiURL = new URL(config.apiUrl + config.apiHub);
            //web api url: http://discordian.orionminecraft.com/hubs/discordian
        }
        catch (MalformedURLException e)
        {
            getLogger().error("Unable to create url: ", e);
        }

        if (apiURL != null)
        {
            final URL finalApiURL = apiURL;

            final MinecraftHandlers minecraftHandlers = new MinecraftHandlers(getDiscordianBot(), getLogger());

            final IDiscordianConnectConnection connection = DiscordianConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IDiscordianConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> eventBuilder.registerMessageHandler("MinecraftGenericMessage", minecraftHandlers::genericMessage));

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));
            });

            connection.connect();
            getDiscordianBot().setConnection(connection);

            getLogger().info("past");

            getDiscordianBot().getJda().addEventListener(new MessageListener(getDiscordianBot()));
        }
    }

    /**
     * How we handle Exceptions from the API client.
     * Honestly, we just log them and move on for now.
     *
     * @param thrown The exception thrown!
     */
    private void errorHandler(Throwable thrown)
    {
        getLogger().error("API ERROR: ", thrown);
    }

    /**
     * Used to set our bot's API Auth token. This is retrieved from the config.
     *
     * @param builder the Authentication builder used.
     */
    private void setAuthenticationToken(IDiscordianConnectAuthenticationBuilder builder)
    {
        // Cannot use until Server side is implemented.
        APIConfig config = (APIConfig) getDiscordianBot().getConfigUtils().get(ID);

        builder.withBearerToken(config.apiToken);
    }

    @Override
    protected APIConfigAdapter createConfigAdapter()
    {
        return new APIConfigAdapter();
    }
}
