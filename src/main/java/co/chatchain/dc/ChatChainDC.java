package co.chatchain.dc;

import co.chatchain.dc.configs.AbstractConfig;
import co.chatchain.dc.configs.FormattingConfig;
import co.chatchain.dc.configs.GroupsConfig;
import co.chatchain.dc.configs.MainConfig;
import co.chatchain.dc.messages.handlers.APIMessages;
import co.chatchain.dc.messages.handlers.JDAMessages;
import co.chatchain.dc.messages.objects.GenericMessage;
import com.google.common.reflect.TypeToken;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import io.reactivex.Single;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Our bot's main class. Used for init of QSML modules, and main entry point.
 */
public class ChatChainDC
{

    private String accessToken = "";

    @Getter
    private HubConnection connection = null;

    @Getter
    private JDA jda;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private FormattingConfig formattingConfig;

    private File configDir;

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
        } catch (LoginException | InterruptedException e)
        {
            System.out.println("Could not initiate discord connection");
            e.printStackTrace();
            return;
        }

        jda.addEventListener(new JDAMessages(this));

        try
        {
            accessToken = getAccessToken();
        } catch (Exception e)
        {
            System.out.println("Exception while attempting to get ChatChain Access Token from IdentityServer: " + e);
        }

        connection = HubConnectionBuilder.create(mainConfig.getApiUrl())
                .withAccessTokenProvider(Single.defer(() -> Single.just(accessToken)))
                .build();
        connection.start().blockingAwait();

        System.out.println("Connection Established: " + connection.getConnectionState());

        final APIMessages apiHandler = new APIMessages(this);

        connection.on("ReceiveGenericMessage", apiHandler::ReceiveGenericMessage, GenericMessage.class);
    }

    private String getAccessToken() throws MalformedURLException, IOException
    {
        System.out.println("Ran Here");

        URL url = new URL(mainConfig.getIdentityUrl());

        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        final String clientId = mainConfig.getClientId();
        final String clientSecret = mainConfig.getClientSecret();

        Map<String, String> arguments = new HashMap<>();
        arguments.put("client_id", clientId);
        arguments.put("client_secret", clientSecret);
        arguments.put("grant_type", "client_credentials");
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";

        JSONObject jsonObject = new JSONObject(output);

        return jsonObject.getString("access_token");
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

    public void reloadConfigs()
    {
        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        mainConfig = getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());

        final Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        formattingConfig = getConfig(formattingConfigPath, FormattingConfig.class,
                GsonConfigurationLoader.builder().setPath(formattingConfigPath).build());
    }
}
