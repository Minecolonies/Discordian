package co.chatchain.dc.configs;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig<T extends ConfigurationLoader<K>, K extends ConfigurationNode>
{
    private T loader;
    private K node;
    @SuppressWarnings("UnstableApiUsage")
    private TypeToken<AbstractConfig> token;

    @SuppressWarnings("UnstableApiUsage")
    protected void init(T loader, K node, TypeToken<AbstractConfig> token)
    {
        this.loader = loader;
        this.node = node;
        this.token = token;
    }

    public void save()
    {
        try
        {
            this.node.setValue(this.token, this);
            this.loader.save(this.node);
        } catch (IOException | ObjectMappingException e)
        {
            System.out.println("Error saving config");
            e.printStackTrace();
        }
    }

    public void load()
    {
        try
        {
            this.node = this.loader.load();
        } catch (IOException e)
        {
            System.out.println("Error loading config");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <M extends AbstractConfig> M getConfig(Path file, Class<M> clazz, ConfigurationLoader loader)
    {
        try
        {
            if (!file.toFile().exists())
            {
                Files.createFile(file);
            }

            @SuppressWarnings("UnstableApiUsage")
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
