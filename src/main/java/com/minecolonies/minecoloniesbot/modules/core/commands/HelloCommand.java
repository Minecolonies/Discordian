package com.minecolonies.minecoloniesbot.modules.core.commands;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.minecolonies.minecoloniesbot.MinecoloniesBot;
import com.minecolonies.minecoloniesbot.internal.BotCommand;

public class HelloCommand extends BotCommand
{

    /**
     * Our injected value of the {@link MinecoloniesBot} instance.
     */
    @Inject
    private MinecoloniesBot minecoloniesBot;

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
