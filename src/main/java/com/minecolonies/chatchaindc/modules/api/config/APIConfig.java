package com.minecolonies.chatchaindc.modules.api.config;

import com.minecolonies.chatchaindc.qsml.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The API section of the main config file.
 */
@ConfigSerializable
public class APIConfig extends BaseConfig
{
    @Setting(value = "api-token", comment = "\nAPI Token for communicating with ChatChainServer API.")
    public String apiToken = "insert-token-here";

    @Setting(value = "api-url", comment = "\nURL for connecting to ChatChainNetwork")
    public String apiUrl = "http://localhost:5000";

    @Setting(value = "api-hub", comment = "\nDO NOT TOUCH UNLESS YOU KNOW WHAT YOU'RE DOING \n "
                                            + "API hub url. please leave proceeding \"/\"")
    public String apiHub = "/hubs/discordian";

    @Setting(value = "client-name", comment = "\nName of this client, used in API")
    public String clientName = "ChatChainDC Client";
}
