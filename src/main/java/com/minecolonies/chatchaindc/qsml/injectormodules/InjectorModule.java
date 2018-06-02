package com.minecolonies.chatchaindc.qsml.injectormodules;

import com.google.inject.AbstractModule;
import com.minecolonies.chatchaindc.ChatChainDC;

/**
 * The class we use to define injected values.
 */
public class InjectorModule extends AbstractModule
{

    private final ChatChainDC chatChainDC;

    public InjectorModule(ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    protected void configure()
    {
        bind(ChatChainDC.class).toInstance(this.chatChainDC);
    }
}
