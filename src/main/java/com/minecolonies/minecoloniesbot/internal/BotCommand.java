package com.minecolonies.minecoloniesbot.internal;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.minecolonies.minecoloniesbot.MinecoloniesBot;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class BotCommand extends Command
{
    /**
     * Our injected value of the {@link MinecoloniesBot} instance.
     */
    @Inject
    @Getter(AccessLevel.PROTECTED)
    private MinecoloniesBot minecoloniesBot;

    public void enable()
    {
        minecoloniesBot.getLogger().info("Command Working {} ", this.name);
    }
}
