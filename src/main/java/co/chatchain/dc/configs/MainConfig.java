package co.chatchain.dc.configs;

import co.chatchain.commons.configuration.AbstractConfig;
import co.chatchain.commons.interfaces.IConnectionConfig;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings({"CanBeFinal", "SpellCheckingInspection"})
@ConfigSerializable
public class MainConfig extends AbstractConfig implements IConnectionConfig
{

    //####### Discord Client ID #######\\

    @Setting(value = "discord-client-id")
    private String discordClientId = "DiscordClientId";

    public String getDiscordClientId()
    {
        return getSystemValueOrConfigValue("CHATCHAIN_DISCORD_CLIENT_ID", discordClientId);
    }

    //####### Client ID #######\\

    @Setting(value = "client-id", comment = "ChatChainIdentity User ID for this client")
    private String clientId = "ClientId";

    @Override
    public String getClientId()
    {
        return getSystemValueOrConfigValue("CHATCHAIN_CLIENT_ID", clientId);
    }

    //####### Client Secret #######\\

    @Setting(value = "client-secret", comment = "ChatChainIdentity User password for this client")
    private String clientSecret = "ClientSecret";

    @Override
    public String getClientSecret()
    {
        return getSystemValueOrConfigValue("CHATCHAIN_CLIENT_SECRET", clientSecret);
    }

    //####### Api URL #######\\

    @Setting(value = "api-url", comment = "API URL Your client is connecting to")
    private String apiUrl = "https://api.chatchain.co/hubs/chatchain";

    public URL getHubUrl()
    {
        try
        {
            return new URL(getSystemValueOrConfigValue("CHATCHAIN_API_URL", apiUrl));
        }
        catch (MalformedURLException e)
        {
            System.out.println("ChatChain Hub URL is malformed!");
            e.printStackTrace();
        }
        return null;
    }

    //####### Identity URL #######\\

    @Setting(value = "identity-url", comment = "Identity URL Your client is authenticating against")
    private String identityUrl = "https://identity.chatchain.co/connect/token";

    @Override
    public URL getIdentityUrl()
    {
        try
        {
            return new URL(getSystemValueOrConfigValue("CHATCHAIN_IDENTITY_URL", identityUrl));
        }
        catch (MalformedURLException e)
        {
            System.out.println("ChatChain Identity URL is malformed!");
            e.printStackTrace();
        }
        return null;
    }

    //####### Use Advanced Formatting #######\\

    @Getter
    @Setting("advanced-formatting")
    private Boolean advancedFormatting = false;

    private String getSystemValueOrConfigValue(final String systemValue, String configValue)
    {
        final String value = System.getenv(systemValue);
        if (value == null || value.equals(""))
        {
            return configValue;
        }
        return value;
    }

}
