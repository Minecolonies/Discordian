package co.chatchain.dc.configs;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class GroupsConfig extends AbstractConfig
{

    @Getter
    @Setting(value = "client-events-groups")
    private Map<String, List<String>> clientEventGroups = new HashMap<>();

    @Getter
    @Setting(value = "user-events-groups")
    private Map<String, List<String>> userEventGroups = new HashMap<>();

    @Setting(value = "group-storage")
    @Getter
    private Map<String, GroupConfig> groupStorage = new HashMap<>();

}
