package co.chatchain.dc;

import co.chatchain.commons.AccessTokenResolver;
import co.chatchain.commons.ChatChainHubConnection;
import co.chatchain.commons.messages.objects.Client;
import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.dc.configs.AbstractConfig;
import co.chatchain.dc.configs.FormattingConfig;
import co.chatchain.dc.configs.GroupsConfig;
import co.chatchain.dc.configs.MainConfig;
import co.chatchain.dc.messages.handlers.APIMessages;
import co.chatchain.dc.messages.handlers.JDAMessages;
import co.chatchain.dc.serializers.GroupTypeSerializer;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Our bot's main class. Used for init of QSML modules, and main entry point.
 */
public class ChatChainDC
{

    private AccessTokenResolver accessToken = null;

    @Getter
    private ChatChainHubConnection connection = null;

    @Getter
    private JDA jda;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private FormattingConfig formattingConfig;

    private File configDir;

    @Getter
    @Setter
    private Client client;

    /**
     * The constructor for our bot.
     */
    private ChatChainDC()
    {

        configDir = new File(System.getProperty("user.dir") + "/configs/");

        if (!configDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();

            if (!configDir.getParentFile().exists())
            {
                System.out.println("Couldn't create config directory!");
                new IOException().printStackTrace();
            }
        }

        final Path mainConfigPath = configDir.toPath().resolve("main.json");

        System.out.println(mainConfigPath);

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Group.class), new GroupTypeSerializer());

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());

        final Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        formattingConfig = getConfig(formattingConfigPath, FormattingConfig.class,
                GsonConfigurationLoader.builder().setPath(formattingConfigPath).build());

        try
        {
            jda = new JDABuilder(AccountType.BOT).setToken(mainConfig.getDiscordClientId()).buildBlocking();
        }
        catch (LoginException | InterruptedException e)
        {
            System.out.println("Could not initiate discord connection");
            e.printStackTrace();
            return;
        }

        jda.addEventListener(new JDAMessages(this));

        try
        {
            accessToken = new AccessTokenResolver(mainConfig.getClientId(), mainConfig.getClientSecret(), mainConfig.getIdentityUrl());
        } catch (Exception e)
        {
            System.out.println("Exception while attempting to get ChatChain Access Token from IdentityServer: " + e);
            return;
        }

        connection = new ChatChainHubConnection(mainConfig.getApiUrl(), accessToken);
        connection.connect();

        System.out.println("Connection Established: " + connection.getConnectionState());

        final APIMessages apiHandler = new APIMessages(this);

        connection.onConnection(hub -> {
            hub.onGenericMessage(apiHandler::ReceiveGenericMessage, GenericMessage.class);
            hub.onClientEventMessage(apiHandler::ReceiveClientEvent, ClientEventMessage.class);
            hub.onUserEventMessage(apiHandler::ReceiveUserEvent, UserEventMessage.class);
            hub.onGetGroupsResponse(apiHandler::ReceiveGroups, GetGroupsResponse.class);
            hub.onGetClientResponse(apiHandler::ReceiveClient, GetClientResponse.class);

            hub.sendGetGroups();
            hub.sendGetClient();
            hub.sendClientEventMessage(new ClientEventMessage("START"));
        });
    }

    /**
     * The start point for our bot.
     *
     * @param args the arguments fed to the JVM on init.
     */
    public static void main(String[] args)
    {

        new ChatChainDC();
    }

    @SuppressWarnings("unchecked")
    private <M extends AbstractConfig> M getConfig(Path file, Class<M> clazz, ConfigurationLoader loader)
    {
        try
        {
            if (!file.toFile().exists())
            {
                Files.createFile(file);
            }

            TypeToken token = TypeToken.of(clazz);
            ConfigurationNode node = loader.load(ConfigurationOptions.defaults());
            M config = (M) node.getValue(token, clazz.newInstance());
            config.init(loader, node, token);
            config.save();
            return config;
        } catch (IOException | ObjectMappingException | IllegalAccessException | InstantiationException e)
        {
            System.out.println("Getting the config failed");
            e.printStackTrace();
            return null;
        }
    }
}
