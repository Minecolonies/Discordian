package co.chatchain.dc.configs;

import co.chatchain.commons.messages.objects.Group;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class GroupConfig
{

    @Getter
    @Setter
    @Setting("group")
    private Group group;

    @Getter
    @Setting("send-chat")
    private boolean sendChat = true;

    @Setting("mapped-channels")
    @Getter
    private List<String> channelMapping = new ArrayList<>();

}
