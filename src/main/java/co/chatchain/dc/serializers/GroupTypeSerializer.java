package co.chatchain.dc.serializers;

import co.chatchain.commons.messages.objects.Group;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class GroupTypeSerializer implements TypeSerializer<Group>
{
    @Override
    public Group deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        final String groupId = value.getNode("group-id").getValue(TypeToken.of(String.class));
        final String groupName = value.getNode("group-name").getValue(TypeToken.of(String.class));
        final String ownerId = value.getNode("owner-id").getValue(TypeToken.of(String.class));
        return new Group(groupId, groupName, ownerId);
    }

    @Override
    public void serialize(TypeToken<?> type, Group obj, ConfigurationNode value) throws ObjectMappingException
    {
        value.getNode("group-id").setValue(obj.getGroupId());
        value.getNode("group-name").setValue(obj.getGroupName());
        value.getNode("owner-id").setValue(obj.getOwnerId());
    }
}
