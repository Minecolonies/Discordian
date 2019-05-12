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
public class ClientOverrides
{
    @Getter
    @Setting("formats")
    private MessageFormats formats = new MessageFormats();

    @Nullable
    public List<String> getUsernameOverride(final String userId, final FormatAction action)
    {
        return userOverrides.getOrDefault(userId, null) == null ? null : action.invoke(userOverrides.getOrDefault(userId, null).getFormats());
    }

    @Getter
    @Setting("user-overrides")
    private Map<String, UserOverrides> userOverrides = new HashMap<>();
}
