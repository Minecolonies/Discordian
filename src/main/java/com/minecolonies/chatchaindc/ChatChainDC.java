package com.minecolonies.chatchaindc;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.minecolonies.chatchainconnect.ChatChainConnectAPI;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.api.connection.auth.IChatChainConnectAuthenticationBuilder;
import com.minecolonies.chatchaindc.api.handlers.GenericHandlers;
import com.minecolonies.chatchaindc.modules.api.APIModule;
import com.minecolonies.chatchaindc.modules.api.MessageListener;
import com.minecolonies.chatchaindc.modules.api.config.APIConfig;
import com.minecolonies.chatchaindc.modules.api.config.ClientConfigs;
import com.minecolonies.chatchaindc.modules.api.config.TemplatesConfig;
import com.minecolonies.chatchaindc.modules.core.CoreModule;
import com.minecolonies.chatchaindc.modules.core.config.CoreConfig;
import com.minecolonies.chatchaindc.qsml.BaseConfig;
import com.minecolonies.chatchaindc.qsml.BotLoggerProxy;
import com.minecolonies.chatchaindc.qsml.BotModuleConstructor;
import com.minecolonies.chatchaindc.qsml.injectormodules.InjectorModule;
import com.minecolonies.chatchaindc.qsml.injectormodules.SubInjectorModule;
import com.minecolonies.chatchaindc.util.TemplateMessages;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.drnaylor.quickstart.modulecontainers.DiscoveryModuleContainer;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Our bot's main class. Used for init of QSML modules, and main entry point.
 */
public class ChatChainDC
{

    public static final String CLIENT_TYPE = "ChatChainDC";

    /**
     * All our module's individual configs.
     */
    @Getter
    private Map<String, BaseConfig> configUtils = new HashMap<>();

    /**
     * Our bot's logger instance.
     */
    @Getter
    @Setter
    private Logger logger;

    /**
     * Our bots Module Container for QSML.
     */
    @Getter
    @Setter
    private DiscoveryModuleContainer moduleContainer;

    /**
     * Our bot's Guice injector.
     */
    @Getter
    private Injector injector;

    /**
     * The JDA instance for our bot.
     */
    @Getter
    private JDA jda;

    /**
     * Our command client for JDA.
     */
    @Getter
    private CommandClient client;

    /**
     * Our SubInjectorModule.
     */
    private final SubInjectorModule subInjector = new SubInjectorModule();

    /**
     * Our API Connection (Set by the {@link com.minecolonies.chatchaindc.modules.api.APIModule})
     */
    @Nullable
    @Getter
    @Setter
    private IChatChainConnectConnection connection = null;

    /**
     * Our TemplatesConfig instance.
     */
    @Getter
    private final TemplatesConfig templatesConfig;

    /**
     * Our ClientConfigs instance.
     */
    @Getter
    private ClientConfigs clientConfigs;

    /**
     * Our TemplateMessages instance.
     */
    @Getter
    private final TemplateMessages templateMessages;

    /**
     * The start point for our bot.
     *
     * @param args the arguments fed to the JVM on init.
     */
    public static void main(String[] args)
    {
        new ChatChainDC();
    }

    /**
     * The constructor for our bot.
     */
    private ChatChainDC()
    {
        logger = LoggerFactory.getLogger(ChatChainDC.class);

        templateMessages = new TemplateMessages(this);

        final File configDir = new File(System.getProperty("user.dir") + "/config/");

        if (!configDir.getParentFile().exists())
        {
            configDir.getParentFile().mkdirs();

            if (!configDir.getParentFile().exists())
            {
                getLogger().error("Cannot create Replacements.json!", new IOException());
            }
        }

        final Path replacementsConfigPath = configDir.toPath().resolve("templates.json");
        final Path clientConfigsPath = configDir.toPath().resolve("clients.json");

        templatesConfig = getConfig(replacementsConfigPath, TemplatesConfig.class,
          GsonConfigurationLoader.builder().setPath(replacementsConfigPath).build());

        clientConfigs = getConfig(clientConfigsPath, ClientConfigs.class,
          GsonConfigurationLoader.builder().setPath(clientConfigsPath).build());

        try
        {
            modulesInit();
            apiInit();
        }
        catch (Exception e)
        {
            logger.error("Modules Initialisation failed. Bot stopping. ", e);
            System.exit(1);
        }
    }

    /**
     * Initialises our Discord bot.
     */
    public void jdaInit() throws LoginException, InterruptedException
    {
        logger.info("JDA: getting CoreConfig.");

        final CoreConfig coreConfig = (CoreConfig) configUtils.get(CoreModule.ID);

        if (coreConfig == null)
        {
            throw new NullPointerException();
        }

        logger.info("JDA: Creating JDA instance.");

        client = new CommandClientBuilder().setPrefix(coreConfig.commandPrefix)
                   .setAlternativePrefix("MB~")
                   .setOwnerId(coreConfig.ownerID)
                   .build();

        jda = new JDABuilder(AccountType.BOT).setToken(coreConfig.botToken).buildBlocking();

        jda.addEventListener(client);
    }

    private synchronized void apiInit()
    {
        APIConfig config = (APIConfig) configUtils.get(APIModule.ID);

        URL apiURL = null;

        try
        {
            apiURL = new URL(config.apiUrl + config.apiHub);
        }
        catch (MalformedURLException e)
        {
            getLogger().error("Unable to create url: ", e);
        }

        if (apiURL != null)
        {
            final URL finalApiURL = apiURL;

            final GenericHandlers handlers = new GenericHandlers(this);

            connection = ChatChainConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IChatChainConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> {
                    eventBuilder.registerMessageHandler("GenericConnectionEvent", handlers::genericConnectionEvent);
                    eventBuilder.registerMessageHandler("GenericDisconnectionEvent", handlers::genericDisconnectionEvent);
                    eventBuilder.registerMessageHandler("GenericMessageEvent", handlers::genericMessageEvent);
                    eventBuilder.registerMessageHandler("GenericJoinEvent", handlers::genericJoinEvent);
                    eventBuilder.registerMessageHandler("GenericLeaveEvent", handlers::genericLeaveEvent);
                });

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));
            });

            final APIConfig apiConfig = (APIConfig) getConfigUtils().get(APIModule.ID);

            connection.connect(() -> connection.send("GenericConnectionEvent", CLIENT_TYPE, apiConfig.clientName, "discord"));
            setConnection(connection);

            jda.addEventListener(new MessageListener(this));
        }
    }

    /**
     * How we handle Exceptions from the API client.
     * Honestly, we just log them and move on for now.
     *
     * @param thrown The exception thrown!
     */
    public void errorHandler(Throwable thrown)
    {
        getLogger().error("API ERROR: ", thrown);
    }

    /**
     * Used to set our bot's API Auth token. This is retrieved from the config.
     *
     * @param builder the Authentication builder used.
     */
    private void setAuthenticationToken(IChatChainConnectAuthenticationBuilder builder)
    {
        // Cannot use until Server side is implemented.
        APIConfig config = (APIConfig) configUtils.get(APIModule.ID);

        builder.withBearerToken(config.apiToken);
    }

    /**
     * Initialisation method for our QSML modules.
     */
    private void modulesInit() throws Exception
    {
        logger.info("Modules: Creating Config Loader");

        final File file = new File(System.getProperty("user.dir") + "/config/config.json");

        logger.info("Modules: Creating New InjectorModule");

        this.injector = Guice.createInjector(new InjectorModule(this));

        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();

            if (!file.getParentFile().exists())
            {
                throw new IOException();
            }
        }

        ConfigurationLoader<ConfigurationNode> loader = GsonConfigurationLoader.builder().setPath(file.toPath()).build();

        logger.info("Modules: Build phase.");
        this.moduleContainer = DiscoveryModuleContainer.builder()
                                 .setPackageToScan("com.minecolonies.chatchaindc.modules") // Where are modules are held.
                                 .setLoggerProxy(new BotLoggerProxy(logger)) // Proxy for modules to use our bot's logger.
                                 .setConstructor(new BotModuleConstructor(this)) // How we construct modules.
                                 .setConfigurationLoader(loader) // Our configuration loader for our module's configs.
                                 .setOnEnable(this::updateInjector) // Before the enable phase, update the Guice injector.
                                 .setNoMergeIfPresent(true) // means that any @NoMergeIfPresent config values will not be added to config if not already there. (Secret values)
                                 .build(true);

        logger.info("Modules: Load phase.");
        this.moduleContainer.loadModules(true);

        logger.info("Modules Initialisation Succeeded.");
    }

    @SuppressWarnings("unchecked")
    public <M extends BaseConfig> M getConfig(Path path, Class<M> clazz, ConfigurationLoader loader)
    {
        try
        {
            if (!path.toFile().exists())
            {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            TypeToken token = TypeToken.of(clazz);
            ConfigurationNode node = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(new DefaultObjectMapperFactory()));
            M config = (M) node.getValue(token, clazz.newInstance());
            config.init(loader, node, token);
            config.save();
            return config;
        }
        catch (IOException | ObjectMappingException | IllegalAccessException | InstantiationException e)
        {
            logger.warn("Get Config failed", e);
            return null;
        }
    }

    /**
     * Updates our Guice injectors.
     */
    private void updateInjector()
    {
        this.injector = this.injector.createChildInjector(this.subInjector);
        this.subInjector.reset();
    }

    /**
     * Stages a class for addition to the Guice injector
     *
     * @param key    The {@link Class} to add.
     * @param getter The {@link Supplier} that gets the class.
     * @param <T>    The type.
     */
    public <T> void addToSubInjectorModule(Class<T> key, Supplier<T> getter)
    {
        this.subInjector.addBinding(key, getter);
    }
}
