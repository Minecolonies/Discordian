package com.minecolonies.discordianbot.internal;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.minecolonies.discordianbot.DiscordianBot;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class BotCommand extends Command
{
    /**
     * Our injected value of the {@link DiscordianBot} instance.
     */
    @Inject
    @Getter(AccessLevel.PROTECTED)
    private DiscordianBot discordianBot;

    public void enable()
    {
        discordianBot.getLogger().info("Command Working {} ", this.name);
    }
}
