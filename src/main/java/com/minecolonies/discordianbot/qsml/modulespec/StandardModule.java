package com.minecolonies.discordianbot.qsml.modulespec;

import com.google.inject.Inject;
import com.minecolonies.discordianbot.DiscordianBot;
import com.minecolonies.discordianbot.internal.BotCommand;
import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A module that has no configuration
 */
public abstract class StandardModule implements Module
{
    /**
     * Our injected value of the {@link DiscordianBot} instance.
     */
    @Inject
    @Getter(AccessLevel.PROTECTED)
    private DiscordianBot discordianBot;

    /**
     * This module's ID.
     */
    @Getter(AccessLevel.PROTECTED)
    private final String moduleId;

    /**
     * This module's name.
     */
    @Getter(AccessLevel.PROTECTED)
    private final String moduleName;

    /**
     * Our logger instance.
     */
    @Getter
    private Logger logger;

    /**
     * This module's package.
     */
    private final String modulePackage;

    /**
     * Our constructor for a standard Module.
     */
    public StandardModule()
    {
        ModuleData data = this.getClass().getAnnotation(ModuleData.class);
        this.moduleId = data.id();
        this.moduleName = data.name();
        this.modulePackage = this.getClass().getPackage().getName() + ".";
    }

    @Override
    public void onEnable()
    {
        //because i can:

        this.logger = this.getDiscordianBot().getLogger();

        discordianBot.getLogger().info("Discord Listener classes in {}: {}",
          modulePackage,
          getStreamForModule(ListenerAdapter.class).collect(Collectors.toList()));

        enable();
    }

    @Override
    public void postEnable()
    {
        List<Class<? extends BotCommand>> commandClasses = getStreamForModule(BotCommand.class).collect(Collectors.toList());

        //Instantiate all the command classes.
        commandClasses.forEach(clazz ->
          {
              try
              {
                  BotCommand command = discordianBot.getInjector().getInstance(clazz);
                  command.enable();
                  discordianBot.getClient().addCommand(command);
              }
              catch (Exception e)
              {
                  logger.error("Command init failed for: {} ", clazz.getPackage());
                  logger.error("StackTrace: ", e);
              }
          }
        );
    }

    /**
     * Method used by child classes to signify the appropriate time for initialising.
     */
    public void enable() {}

    /**
     * This method searches a module's class path for any classes implementing the assignableClass
     *
     * @param assignableClass The class of which's children we are looking for.
     * @param <T>             the assignableClass.
     * @return the stream result of all the classes searched for and found.
     */
    @SuppressWarnings("unchecked")
    private <T> Stream<Class<? extends T>> getStreamForModule(Class<T> assignableClass)
    {
        return discordianBot.getModuleContainer().getLoadedClasses().stream()
                 .filter(assignableClass::isAssignableFrom)
                 .filter(x -> x.getPackage().getName().startsWith(modulePackage))
                 .filter(x -> !Modifier.isAbstract(x.getModifiers()) && !Modifier.isInterface(x.getModifiers()))
                 .map(x -> (Class<? extends T>) x);
    }
}
