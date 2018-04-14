package com.minecolonies.discordianbot.qsml;

import com.minecolonies.discordianbot.DiscordianBot;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.loaders.ModuleConstructor;

/**
 * The constructor we use for our modules.
 */
public class BotModuleConstructor implements ModuleConstructor
{
    /**
     * Our bot's instance.
     */
    private final DiscordianBot discordianBot;

    /**
     * The constructor for our {@link BotModuleConstructor}.
     *
     * @param discordianBot our bot's instance.
     */
    public BotModuleConstructor(DiscordianBot discordianBot)
    {
        this.discordianBot = discordianBot;
    }

    @Override
    public Module constructModule(Class<? extends Module> moduleClass)
    {
        return discordianBot.getInjector().getInstance(moduleClass);
    }
}
