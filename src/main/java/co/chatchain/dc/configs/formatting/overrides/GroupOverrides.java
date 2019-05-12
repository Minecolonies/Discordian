package co.chatchain.dc.configs.formatting.overrides;

import co.chatchain.dc.configs.formatting.FormatAction;
import co.chatchain.dc.configs.formatting.formats.MessageFormats;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class GroupOverrides
{
    @Getter
    @Setting("formats")
    private MessageFormats formats = new MessageFormats();

    @Nullable
    public List<String> getUsernameOverride(final String clientId, final String userId, final FormatAction action)
    {
        final ClientOverrides clientOverride = clientOverrides.getOrDefault(clientId, null);
        final List<String> override = clientOverride == null ? null : clientOverride.getUsernameOverride(userId, action);

        if (override != null)
            return override;

        return userOverrides.getOrDefault(userId, null) == null ? null : action.invoke(userOverrides.getOrDefault(userId, null).getFormats());
    }

    @Nullable
    public List<String> getClientOverride(final String clientId, final FormatAction action)
    {
        return clientOverrides.getOrDefault(clientId, null) == null ? null : action.invoke(clientOverrides.getOrDefault(clientId, null).getFormats());
    }

    @Getter
    @Setting("user-overrides")
    private Map<String, UserOverrides> userOverrides = new HashMap<>();

    @Getter
    @Setting("client-overrides")
    private Map<String, ClientOverrides> clientOverrides = new HashMap<>();
}