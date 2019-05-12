package co.chatchain.dc.configs.formatting.overrides;

import co.chatchain.dc.configs.formatting.formats.MessageFormats;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class UserOverrides
{
    @Getter
    @Setting("formats")
    private MessageFormats formats = new MessageFormats();
}