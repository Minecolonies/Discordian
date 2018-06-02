package com.minecolonies.chatchaindc.qsml;

import org.slf4j.Logger;
import uk.co.drnaylor.quickstart.LoggerProxy;

/**
 * Logger proxy for our modules to use.
 */
public class BotLoggerProxy implements LoggerProxy
{
    /**
     * Our {@link Logger} instance
     */
    private final Logger logger;

    /**
     * Our proxy constructor, setting our logger instance.
     *
     * @param logger our bot's logger instance.
     */
    public BotLoggerProxy(final Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void info(final String message)
    {
        this.logger.info(message);
    }

    @Override
    public void warn(final String message)
    {
        this.logger.warn(message);
    }

    @Override
    public void error(final String message)
    {
        this.logger.error(message);
    }

    /**
     * Like the error method above, but allows the usage of throwables for cleaner stack-traces.
     *
     * @param message   The message being sent.
     * @param throwable The throwable being thrown.
     */
    public void error(final String message, final Throwable throwable)
    {
        this.logger.error(message, throwable);
    }
}
