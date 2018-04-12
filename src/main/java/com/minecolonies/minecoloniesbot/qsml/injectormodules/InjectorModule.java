package com.minecolonies.minecoloniesbot.qsml.injectormodules;

import com.google.inject.AbstractModule;
import com.minecolonies.minecoloniesbot.MinecoloniesBot;

/**
 * The class we use to define injected values.
 */
public class InjectorModule extends AbstractModule
{

    private final MinecoloniesBot minecoloniesBot;

    public InjectorModule(MinecoloniesBot minecoloniesBot)
    {
        this.minecoloniesBot = minecoloniesBot;
    }

    @Override
    protected void configure()
    {
        bind(MinecoloniesBot.class).toInstance(this.minecoloniesBot);
    }
}
