package com.minecolonies.minecoloniesbot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.minecolonies.minecoloniesbot.modules.core.CoreModule;
import com.minecolonies.minecoloniesbot.modules.core.config.CoreConfig;
import com.minecolonies.minecoloniesbot.qsml.BaseConfig;
import com.minecolonies.minecoloniesbot.qsml.BotLoggerProxy;
import com.minecolonies.minecoloniesbot.qsml.BotModuleConstructor;
import com.minecolonies.minecoloniesbot.qsml.injectormodules.InjectorModule;
import com.minecolonies.minecoloniesbot.qsml.injectormodules.SubInjectorModule;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.drnaylor.quickstart.modulecontainers.DiscoveryModuleContainer;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Our bot's main class. Used for init of QSML modules, and main entry point.
 */
public class MinecoloniesBot
{

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
     * The start point for our bot.
     *
     * @param args the arguments fed to the JVM on init.
     */
    public static void main(String[] args)
    {
        new MinecoloniesBot();
    }

    /**
     * The constructor for our bot.
     */
    private MinecoloniesBot()
    {
        logger = LoggerFactory.getLogger(MinecoloniesBot.class);

        try
        {
            modulesInit();
        }
        catch (Exception e)
        {
            logger.error("Modules Initialisation failed. Bot stopping. ", e);
            System.exit(1);
        }

        /*try
        {
            jdaInit();
        }
        catch (Exception e)
        {
            logger.error("JDA Failed to initialise. Bot Stopping. ", e);
            System.exit(1);
        }*/

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

    /**
     * Initialisation method for our QSML modules.
     */
    private void modulesInit() throws Exception
    {
        logger.info("Modules: Creating Config Loader");

        final File file = new File(System.getProperty("user.dir") + "/config/config.conf");

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

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(file.toPath()).build();

        logger.info("Modules: Build phase.");
        this.moduleContainer = DiscoveryModuleContainer.builder()
                                 .setPackageToScan("com.minecolonies.minecoloniesbot.modules") // Where are modules are held.
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
