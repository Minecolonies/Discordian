package com.minecolonies.chatchaindc.modules.api.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

public class APIConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<APIConfig>
{
    public APIConfigAdapter()
    {
        super(APIConfig.class);
    }
}
