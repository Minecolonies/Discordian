package com.minecolonies.discordianbot.qsml.injectormodules;

import com.google.inject.AbstractModule;
import com.minecolonies.discordianbot.DiscordianBot;

/**
 * The class we use to define injected values.
 */
public class InjectorModule extends AbstractModule
{

    private final DiscordianBot discordianBot;

    public InjectorModule(DiscordianBot discordianBot)
    {
        this.discordianBot = discordianBot;
    }

    @Override
    protected void configure()
    {
        bind(DiscordianBot.class).toInstance(this.discordianBot);
    }
}
