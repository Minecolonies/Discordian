package co.chatchain.dc.messages.objects;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class Group
{
    @Getter
    @Setting("group-name")
    private String groupName;

    @Getter
    @Setting("group-ID")
    private String groupId;

    @Setting("mapped-channels")
    @Getter
    private List<String> channelMapping = new ArrayList<>();

    public Group()
    {
    }

    public Group(final String groupId)
    {
        this.groupId = groupId;
    }
}
