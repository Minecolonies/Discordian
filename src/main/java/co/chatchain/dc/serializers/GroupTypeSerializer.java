package co.chatchain.dc.serializers;

import co.chatchain.commons.objects.Group;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class GroupTypeSerializer implements TypeSerializer<Group>
{
    @Override
    public Group deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException
    {
        final String id = value.getNode("id").getValue(TypeToken.of(String.class));
        final String name = value.getNode("name").getValue(TypeToken.of(String.class));
        final String description = value.getNode("description").getValue(TypeToken.of(String.class));
        final String ownerId = value.getNode("owner-id").getValue(TypeToken.of(String.class));
        return new Group(id, ownerId, name, description, null);
    }

    @Override
    public void serialize(TypeToken<?> type, Group obj, ConfigurationNode value) throws ObjectMappingException
    {
        value.getNode("id").setValue(obj.getId());
        value.getNode("name").setValue(obj.getName());
        value.getNode("description").setValue(obj.getDescription());
        value.getNode("owner-id").setValue(obj.getOwnerId());
    }
}
