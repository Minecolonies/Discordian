package co.chatchain.dc.commands;

import co.chatchain.commons.core.entities.messages.stats.StatsRequestMessage;
import co.chatchain.commons.core.entities.requests.stats.StatsRequestRequest;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.reactivex.Single;

import java.util.Map;

public class StatsCommand extends Command
{
    private final ChatChainDC chatChainDC;

    public StatsCommand(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
        this.name = "stats";
        this.help = "Gets the stats of a given client";
        this.arguments = "<section>";
    }

    @Override
    protected void execute(final CommandEvent event)
    {
        event.getArgs();

        String statsSection = null;

        if (event.getArgs() != null && !event.getArgs().isEmpty())
        {
            if (event.getArgs().equals("online-users") || event.getArgs().equals("online"))
            {
                statsSection = "online-users";
            }
            else if (event.getArgs().equals("performance") || event.getArgs().equals("perf"))
            {
                statsSection = "performance";
            }
            else
            {
                event.replyError("Valid stats section arguments are: [online(-users), perf(ormance)]");
                return;
            }
        }

        for (final Map.Entry<String, GroupConfig> groupEntry : chatChainDC.getGroupsConfig().getGroupStorage().entrySet())
        {
            if (!groupEntry.getValue().isSendChat())
                continue;

            if (groupEntry.getValue().getChannelMapping().contains(event.getChannel().getId()))
            {
                Single<StatsRequestMessage> response = chatChainDC.getConnection().sendStatsRequestMessage(new StatsRequestRequest(null, groupEntry.getKey(), statsSection));

                response.doOnError(throwable ->
                {
                    event.replyError("Failed to get Stats Request Response, check logs for stacktrace");
                    throwable.printStackTrace();
                }).doOnSuccess(message ->
                {
                    for (String requestId : message.getRequestIds())
                    {
                        chatChainDC.getConnection().addStatsRequest(requestId, event.getChannel().getId());
                    }
                }).subscribe();
            }
        }
    }
}
