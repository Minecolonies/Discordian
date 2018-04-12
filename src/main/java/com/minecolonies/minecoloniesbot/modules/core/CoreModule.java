package com.minecolonies.minecoloniesbot.modules.core;

import com.minecolonies.minecoloniesbot.modules.core.Listeners.MessageListener;
import com.minecolonies.minecoloniesbot.modules.core.config.CoreConfig;
import com.minecolonies.minecoloniesbot.modules.core.config.CoreConfigAdapter;
import com.minecolonies.minecoloniesbot.qsml.modulespec.ConfigurableModule;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

/**
 * Core module for our bot.
 */
@ModuleData(id = CoreModule.ID,
  name = "Core",
  isRequired = true)
public class CoreModule extends ConfigurableModule<CoreConfigAdapter>
{
    /**
     * This modules ID string.
     */
    public static final String ID = "CORE";

    @Getter
    private JDA jda;

    @Override
    public void enable()
    {
        super.enable();
        getMinecoloniesBot().getConfigUtils().put(ID, getAdapter().getNodeOrDefault());

        final CoreConfig coreConfig = (CoreConfig) getMinecoloniesBot().getConfigUtils().get(ID);

        if (coreConfig == null)
        {
            throw new NullPointerException();
        }

        try
        {
            jda = new JDABuilder(AccountType.BOT).setToken(coreConfig.botToken).buildBlocking();
        }
        catch (Exception e)
        {
            getLogger().error("JDA Building failed!", e);
            getLogger().error("Shutting Down!");
            System.exit(1);
        }

        jda.addEventListener(new MessageListener(getMinecoloniesBot()));

    }

    @Override
    protected CoreConfigAdapter createConfigAdapter()
    {
        return new CoreConfigAdapter();
    }
}
