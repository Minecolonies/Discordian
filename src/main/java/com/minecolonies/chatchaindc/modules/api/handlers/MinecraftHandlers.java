package com.minecolonies.chatchaindc.modules.api.handlers;

import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.modules.api.APIModule;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import org.slf4j.Logger;

/**
 * How we handle Chat messages from minecraft.
 */
public class MinecraftHandlers
{

    private final ChatChainDC chatChainDC;
    private final Logger      logger;

    public MinecraftHandlers(final ChatChainDC chatChainDC, final Logger logger)
    {
        this.chatChainDC = chatChainDC;
        this.logger = logger;
    }

    /**
     * What we do when there's a new Minecraft chat event.
     *
     * @param message The JSON message that was sent via API.
     */
    public void genericMessage(IChatChainConnectMessage message)
    {
        APIConfig config = (APIConfig) chatChainDC.getConfigUtils().get(APIModule.ID);

        if (config != null && message != null)
        {
            final String channelID = message.getArguments()[0].getAsString();
            final String displayMessage = message.getArguments()[2].getAsString();

            logger.info(displayMessage);
            chatChainDC.getJda().getTextChannelById(channelID).sendMessage(displayMessage).submit();
        }
    }
}
