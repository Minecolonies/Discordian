package com.minecolonies.chatchaindc.util;

import com.minecolonies.chatchaindc.ChatChainDC;

import static com.minecolonies.chatchaindc.modules.api.config.APIConfigReplacements.*;

public final class ReplacementsUtil
{
    private final ChatChainDC chatChainDC;

    public ReplacementsUtil(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    public String genericConnection(final String clientType, final String clientName, final String channelName)
    {
        return chatChainDC.getReplacementsConfig().genericConnection
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public String genericDisconnection(final String clientType, final String clientName, final String channelName)
    {
        return chatChainDC.getReplacementsConfig().genericDisconnection
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public String genericMessage(final String clientType, final String clientName, final String channelName, final String user, final String message)
    {
        return chatChainDC.getReplacementsConfig().genericMessage
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user)
                 .replace(USER_MESSAGE, message);
    }

    public String genericJoin(final String clientType, final String clientName, final String channelName, final String user)
    {
        return chatChainDC.getReplacementsConfig().genericJoin
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }

    public String genericLeave(final String clientType, final String clientName, final String channelName, final String user)
    {
        return chatChainDC.getReplacementsConfig().genericLeave
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }

}
