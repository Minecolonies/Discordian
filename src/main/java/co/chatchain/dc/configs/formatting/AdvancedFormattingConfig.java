package co.chatchain.dc.configs.formatting;

import co.chatchain.commons.objects.Client;
import co.chatchain.commons.objects.ClientUser;
import co.chatchain.commons.objects.Group;
import co.chatchain.dc.configs.AbstractConfig;
import co.chatchain.dc.configs.formatting.formats.DefaultFormats;
import co.chatchain.dc.configs.formatting.formats.MessageFormats;
import co.chatchain.dc.configs.formatting.overrides.ClientOverrides;
import co.chatchain.dc.configs.formatting.overrides.GroupOverrides;
import co.chatchain.dc.configs.formatting.overrides.UserOverrides;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class AdvancedFormattingConfig extends AbstractConfig
{

    @Getter
    @Setting("default-formats")
    private MessageFormats formats = new DefaultFormats();

    @Getter
    @Setting("user-overrides")
    private Map<String, UserOverrides> userOverrides = new HashMap<>();

    @Getter
    @Setting("client-overrides")
    private Map<String, ClientOverrides> clientOverrides = new HashMap<>();

    @Getter
    @Setting("group-overrides")
    private Map<String, GroupOverrides> groupOverrides = new HashMap<>();

    @NotNull
    public List<String> getOverride(final Group group, final Client client, final ClientUser user, final FormatAction action)
    {
        if (group != null && client != null && user != null)
            return getOverride(group.getId(), client.getId(), user.getUniqueId(), action);

        if (group != null && client != null)
            return getOverride(group.getId(), client.getId(), action);

        if (group != null)
            return getOverride(group.getId(), action);

        return action.invoke(formats);
    }

    @NotNull
    public List<String> getOverride(final String groupId, final String clientId, final String userId, final FormatAction action)
    {
        List<String> override = getUsernameOverride(groupId, clientId, userId, action);

        if (override != null)
            return override;

        return getOverride(groupId, clientId, action);
    }

    @NotNull
    public List<String> getOverride(final String groupId, final String clientId, final FormatAction action)
    {
        List<String> override = getClientOverride(groupId, clientId, action);

        if (override != null)
            return override;

        return getOverride(groupId, action);
    }

    @NotNull
    public List<String> getOverride(final String groupId, final FormatAction action)
    {
        List<String> override = getGroupOverride(groupId, action);

        if (override != null)
            return override;

        return action.invoke(formats);
    }

    @Nullable
    public List<String> getUsernameOverride(final String groupId, final String clientId, final String userId, final FormatAction action)
    {
        final GroupOverrides groupOverride = groupOverrides.getOrDefault(groupId, null);
        List<String> override = groupOverride == null ? null : groupOverride.getUsernameOverride(clientId, userId, action);

        if (override != null)
            return override;

        final ClientOverrides clientOverride = clientOverrides.getOrDefault(clientId, null);
        override = clientOverride == null ? null : clientOverride.getUsernameOverride(userId, action);

        if (override != null)
            return override;

        return userOverrides.getOrDefault(userId, null) == null ? null : action.invoke(userOverrides.getOrDefault(userId, null).getFormats());
    }

    @Nullable
    public List<String> getClientOverride(final String groupId, final String clientId, final FormatAction action)
    {
        final GroupOverrides groupOverride = groupOverrides.getOrDefault(groupId, null);
        final List<String> override = groupOverride == null ? null : groupOverride.getClientOverride(clientId, action);

        if (override != null)
            return override;

        return clientOverrides.getOrDefault(clientId, null) == null ? null : action.invoke(clientOverrides.getOrDefault(clientId, null).getFormats());
    }

    @Nullable
    public List<String> getGroupOverride(final String groupId, final FormatAction action)
    {
        return groupOverrides.getOrDefault(groupId, null) == null ? null : action.invoke(groupOverrides.getOrDefault(groupId, null).getFormats());
    }
}
