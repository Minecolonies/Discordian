package com.minecolonies.discordianbot.qsml.injectormodules;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;

import java.util.Map;
import java.util.function.Supplier;

/**
 * This is the class where we define our subInjectors.
 */
public class SubInjectorModule extends AbstractModule
{
    /**
     * The bindings we are using.
     */
    private final Map<Class<?>, Supplier<?>> bindings = Maps.newHashMap();

    /**
     * our constructor.
     *
     * @param clazz    The class we're injecting into.
     * @param supplier Where we get the data.
     * @param <T>      the class type.
     * @return whether we successfully added the binding.
     */
    public <T> boolean addBinding(Class<T> clazz, Supplier<T> supplier)
    {
        if (!bindings.containsKey(clazz))
        {
            bindings.put(clazz, supplier);
            return true;
        }
        return false;
    }

    @Override
    protected void configure()
    {
        bindings.keySet().forEach(this::get);
    }

    /**
     * Resets the bindings.
     */
    public void reset()
    {
        bindings.clear();
    }

    /**
     * This is where we figure out if it's the castes type.
     *
     * @param key class we're checking.
     * @param <T> class type we're checking.
     */
    @SuppressWarnings("unchecked")
    private <T> void get(Class<T> key)
    {
        bind(key).toProvider(() -> (T) bindings.get(key));
    }
}
