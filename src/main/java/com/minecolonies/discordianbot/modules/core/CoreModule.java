package com.minecolonies.discordianbot.modules.core;

import com.minecolonies.discordianbot.modules.core.config.CoreConfigAdapter;
import com.minecolonies.discordianbot.qsml.modulespec.ConfigurableModule;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
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
    public void onEnable()
    {
        super.onEnable();
        try
        {
            this.getDiscordianBot().jdaInit();
        }
        catch (Exception e)
        {
            getLogger().error("JDA Failed to initialise. Bot Stopping. ", e);
            System.exit(1);
        }
    }

    @Override
    public void enable()
    {
        super.enable();
        this.getDiscordianBot().getConfigUtils().put(ID, getAdapter().getNodeOrDefault());
    }

    @Override
    protected CoreConfigAdapter createConfigAdapter()
    {
        return new CoreConfigAdapter();
    }
}
