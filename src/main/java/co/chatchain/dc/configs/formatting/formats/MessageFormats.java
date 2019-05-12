package co.chatchain.dc.configs.formatting.formats;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class MessageFormats
{
    @Getter
    @Setting("generic")
    private List<String> genericMessage = new ArrayList<>();

    @Getter
    @Setting("client-event")
    private Map<String, List<String>> clientEventMessages = new HashMap<>();

    @Getter
    @Setting("user-event")
    private Map<String, List<String>> userEventMessages = new HashMap<>();

}
