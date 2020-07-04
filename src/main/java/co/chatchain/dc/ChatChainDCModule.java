package co.chatchain.dc;

import co.chatchain.commons.core.cases.ReceiveClientCase;
import co.chatchain.commons.core.cases.ReceiveGenericMessageCase;
import co.chatchain.commons.core.cases.events.ReceiveClientEventCase;
import co.chatchain.commons.core.cases.events.ReceiveUserEventCase;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.commons.core.interfaces.cases.IReceiveClientCase;
import co.chatchain.commons.core.interfaces.cases.IReceiveGenericMessageCase;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.commons.core.interfaces.cases.events.IReceiveClientEventCase;
import co.chatchain.commons.core.interfaces.cases.events.IReceiveUserEventCase;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsRequestCase;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsResponseCase;
import co.chatchain.commons.interfaces.IConnectionConfig;
import co.chatchain.commons.interfaces.ILogger;
import co.chatchain.dc.cases.ReceiveGroupsCase;
import co.chatchain.dc.cases.stats.ReceiveStatsRequestCase;
import co.chatchain.dc.cases.stats.ReceiveStatsResponseCase;
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

        bind(IReceiveClientCase.class).to(ReceiveClientCase.class);
        bind(IReceiveClientEventCase.class).to(ReceiveClientEventCase.class);
        bind(IReceiveGenericMessageCase.class).to(ReceiveGenericMessageCase.class);
        bind(IReceiveUserEventCase.class).to(ReceiveUserEventCase.class);
        bind(IReceiveStatsResponseCase.class).to(ReceiveStatsResponseCase.class);


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
