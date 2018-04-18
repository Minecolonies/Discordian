package com.minecolonies.discordianbot.modules.api.handlers;

import com.minecolonies.discordianbot.DiscordianBot;
import com.minecolonies.discordianbot.modules.api.APIModule;
import com.minecolonies.discordianbot.modules.api.config.APIConfig;
import com.minecolonies.discordianconnect.api.message.IDiscordianConnectMessage;
import org.slf4j.Logger;

/**
 * How we handle Chat messages from minecraft.
 */
public class MinecraftHandlers
{

    private final DiscordianBot discordianBot;
    private final Logger        logger;

    public MinecraftHandlers(final DiscordianBot discordianBot, final Logger logger)
    {
        this.discordianBot = discordianBot;
        this.logger = logger;
    }

    /**
     * What we do when there's a new Minecraft chat event.
     *
     * @param message The JSON message that was sent via API.
     */
    public void genericMessage(IDiscordianConnectMessage message)
    {
        APIConfig config = (APIConfig) discordianBot.getConfigUtils().get(APIModule.ID);

        if (config != null && message != null)
        {
            final String channelID = message.getArguments()[0].getAsString();
            final String displayMessage = message.getArguments()[2].getAsString();

            logger.info(displayMessage);
            discordianBot.getJda().getTextChannelById(channelID).sendMessage(displayMessage).submit();
        }
    }
}
