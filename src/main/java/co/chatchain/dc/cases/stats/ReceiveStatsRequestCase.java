package co.chatchain.dc.cases.stats;

import co.chatchain.commons.core.entities.StatsObject;
import co.chatchain.commons.core.entities.messages.stats.StatsRequestMessage;
import co.chatchain.commons.core.entities.requests.stats.StatsResponseRequest;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsRequestCase;
import co.chatchain.commons.interfaces.IChatChainHubConnection;
import co.chatchain.dc.ChatChainDC;

import javax.inject.Inject;
public class ReceiveStatsRequestCase implements IReceiveStatsRequestCase
{

    private IChatChainHubConnection chatChainHubConnection;
    private ChatChainDC chatChainDC;

    @Inject
    public ReceiveStatsRequestCase(final IChatChainHubConnection chatChainHubConnection, final ChatChainDC chatChainDC)
    {
        this.chatChainHubConnection = chatChainHubConnection;
        this.chatChainDC = chatChainDC;
    }

    @Override
    public boolean handle(final StatsRequestMessage message)
    {
        final StatsObject statsObject = new StatsObject();

        //Online-users is not implemented until fixes regarding very large online-user lists are fixed.

        if (message.getStatsSection() == null || message.getStatsSection().equals("performance"))
        {
            statsObject.setPerformance(Long.toString(chatChainDC.getJda().getGatewayPing()));
            statsObject.setPerformanceName("ping");
        }

        chatChainHubConnection.sendStatsResponseMessage(new StatsResponseRequest(message.getRequestId(), statsObject));
        return true;
    }
}
