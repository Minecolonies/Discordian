package com.minecolonies.discordianbot.modules.api;

import com.minecolonies.discordianbot.modules.api.config.APIConfigAdapter;
import com.minecolonies.discordianbot.qsml.modulespec.ConfigurableModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = APIModule.ID, name = "API")
public class APIModule extends ConfigurableModule<APIConfigAdapter>
{
    /**
     * This modules ID string.
     */
    public static final String ID = "API";

    @Override
    public void enable()
    {
        getLogger().info("API Module Started");
    }

    @Override
    protected APIConfigAdapter createConfigAdapter()
    {
        return new APIConfigAdapter();
    }
}
