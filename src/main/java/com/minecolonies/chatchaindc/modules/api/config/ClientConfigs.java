package com.minecolonies.chatchaindc.modules.api.config;

import com.minecolonies.chatchaindc.qsml.BaseConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ClientConfigs extends BaseConfig
{

    @Setting(comment = "Put overriding ignore on clientTypes. E.G. ChatChainMC=false will ignore all other MC messages")
    public Map<String, Boolean> clientTypesConfig = new HashMap<>();

    @Setting
    public Map<String, ClientConfig> clientConfigs = new HashMap<>();

    @ConfigSerializable
    public static class ClientConfig
    {
        @Setting(value = "display", comment = "\nDo i display messages from this client?")
        public Boolean display = true;

        @Setting(value = "channels", comment = "\nlocalChannelName: [channelName]. E.G. \"435017246830755841\": \"[main, staff]\" \n "
                                                 + "See client's wiki for list of their channelNames")
        public Map<String, List<String>> channels = new HashMap<>();
    }

}
