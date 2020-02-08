package co.chatchain.dc;

import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsRequestCase;
import co.chatchain.commons.interfaces.IConnectionConfig;
import co.chatchain.commons.interfaces.ILogger;
import co.chatchain.dc.cases.ReceiveGroupsCase;
import co.chatchain.dc.cases.stats.ReceiveStatsRequestCase;
import co.chatchain.dc.messages.handlers.MessageSender;
import com.google.inject.AbstractModule;

public class ChatChainDCModule extends AbstractModule
{
    private final ChatChainDC chatChainDC;

    public ChatChainDCModule(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    protected void configure()
    {
        bind(ChatChainDC.class).toInstance(chatChainDC);
        bind(IMessageSender.class).to(MessageSender.class);
        bind(IConnectionConfig.class).toInstance(chatChainDC.getMainConfig());
        bind(ILogger.class).to(Logger.class);
        bind(IReceiveGroupsCase.class).to(ReceiveGroupsCase.class);
        bind(IReceiveStatsRequestCase.class).to(ReceiveStatsRequestCase.class);
    }

    private static class Logger implements ILogger
    {
        @Override
        public void error(final String error, final Exception exception)
        {
            System.out.println(error);
            exception.printStackTrace();
        }
    }
}
