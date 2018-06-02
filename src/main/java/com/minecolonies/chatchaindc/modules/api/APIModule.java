package com.minecolonies.chatchaindc.modules.api;

import com.minecolonies.chatchaindc.modules.api.config.APIConfigAdapter;
import com.minecolonies.chatchaindc.qsml.modulespec.ConfigurableModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = APIModule.ID, name = "API", isRequired = true)
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
        this.getChatChainDC().getConfigUtils().put(ID, getAdapter().getNodeOrDefault());
    }

    @Override
    protected APIConfigAdapter createConfigAdapter()
    {
        return new APIConfigAdapter();
    }
}
