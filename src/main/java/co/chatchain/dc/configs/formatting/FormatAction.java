package co.chatchain.dc.configs.formatting;

import co.chatchain.dc.configs.formatting.formats.MessageFormats;

import java.util.List;

public interface FormatAction
{
    List<String> invoke(final MessageFormats formats);
}
