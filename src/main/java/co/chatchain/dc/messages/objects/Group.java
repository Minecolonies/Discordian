package co.chatchain.dc.messages.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.dv8tion.jda.core.entities.Channel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Group() {}

    public Group(final String groupId)
    {
        this.groupId = groupId;
    }
}
