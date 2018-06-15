package com.minecolonies.chatchaindc.util;

import org.slf4j.Logger;

import java.io.*;
import java.util.Base64;

public final class SerializeUtils
{

    private SerializeUtils() {/* Private constructor to hide the implicit */}

    public static Object deserialize(final String object, final Logger logger)
    {
        try
        {
            byte[] data = Base64.getDecoder().decode(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object newObject = ois.readObject();
            ois.close();
            return newObject;
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.error("Unable to deserialize: ", e);
        }
        return new Object();
    }

    public static String serialize(final Serializable object, final Logger logger)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
        catch (IOException e)
        {
            logger.error("Unable to serialize: ", e);
        }
        return "";
    }
}
