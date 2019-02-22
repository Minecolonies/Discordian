package co.chatchain.dc.configs;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public abstract class AbstractConfig<T extends ConfigurationLoader<K>, K extends ConfigurationNode>
{

    private T loader;
    @Getter
    private K node;
    private TypeToken<AbstractConfig> token;

    public void init(T loader, K node, TypeToken<AbstractConfig> token)
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
        }
        catch (IOException | ObjectMappingException e)
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
        }
        catch (IOException e)
        {
            System.out.println("Error loading config");
            e.printStackTrace();
        }
    }


}
