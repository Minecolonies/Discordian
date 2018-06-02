package com.minecolonies.chatchaindc.qsml;

import com.minecolonies.chatchaindc.ChatChainDC;
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
    private final ChatChainDC chatChainDC;

    /**
     * The constructor for our {@link BotModuleConstructor}.
     *
     * @param chatChainDC our bot's instance.
     */
    public BotModuleConstructor(ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    public Module constructModule(Class<? extends Module> moduleClass)
    {
        return chatChainDC.getInjector().getInstance(moduleClass);
    }
}
