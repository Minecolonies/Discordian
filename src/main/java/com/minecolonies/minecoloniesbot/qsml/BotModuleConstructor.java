package com.minecolonies.minecoloniesbot.qsml;

import com.minecolonies.minecoloniesbot.MinecoloniesBot;
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
    private final MinecoloniesBot minecoloniesBot;

    /**
     * The constructor for our {@link BotModuleConstructor}.
     *
     * @param minecoloniesBot our bot's instance.
     */
    public BotModuleConstructor(MinecoloniesBot minecoloniesBot)
    {
        this.minecoloniesBot = minecoloniesBot;
    }

    @Override
    public Module constructModule(Class<? extends Module> moduleClass)
    {
        return minecoloniesBot.getInjector().getInstance(moduleClass);
    }
}
