package com.minecolonies.chatchaindc.modules.core.config;

import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

public class CoreConfigAdapter extends TypedAbstractConfigAdapter.StandardWithSimpleDefault<CoreConfig>
{
    public CoreConfigAdapter()
    {
        // Required thanks to type erasure.
        super(CoreConfig.class);
    }
}
