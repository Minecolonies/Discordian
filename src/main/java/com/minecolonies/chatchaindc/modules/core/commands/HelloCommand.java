package com.minecolonies.chatchaindc.modules.core.commands;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.minecolonies.chatchaindc.ChatChainDC;
import com.minecolonies.chatchaindc.internal.BotCommand;

public class HelloCommand extends BotCommand
{

    /**
     * Our injected value of the {@link ChatChainDC} instance.
     */
    @Inject
    private ChatChainDC chatChainDC;

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
