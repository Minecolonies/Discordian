package com.minecolonies.discordianbot.modules.core.commands;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.minecolonies.discordianbot.DiscordianBot;
import com.minecolonies.discordianbot.internal.BotCommand;

public class HelloCommand extends BotCommand
{

    /**
     * Our injected value of the {@link DiscordianBot} instance.
     */
    @Inject
    private DiscordianBot discordianBot;

    @Override
    public void enable()
    {
        this.name = "hello";
        this.aliases = new String[] {"hi"};
        this.help = "says hello!";
        super.enable();
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reply("Hello " + event.getAuthor().getName() + "!");
    }
}
