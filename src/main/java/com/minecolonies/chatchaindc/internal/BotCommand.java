package com.minecolonies.chatchaindc.internal;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.minecolonies.chatchaindc.ChatChainDC;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class BotCommand extends Command
{
    /**
     * Our injected value of the {@link ChatChainDC} instance.
     */
    @Inject
    @Getter(AccessLevel.PROTECTED)
    private ChatChainDC chatChainDC;

    public void enable()
    {
        chatChainDC.getLogger().info("Command Working {} ", this.name);
    }
}
