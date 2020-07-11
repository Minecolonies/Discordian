package co.chatchain.dc.cases;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.entities.messages.GetGroupsMessage;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import co.chatchain.dc.configs.GroupsConfig;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class ReceiveGroupsCase implements IReceiveGroupsCase
{
    private final ChatChainDC chatChainDC;

    @Inject
    public ReceiveGroupsCase(final ChatChainDC chatChainDC)
    {
        this.chatChainDC = chatChainDC;
    }

    @Override
    public boolean handle(final GetGroupsMessage message)
    {
        AddGroupToConfig(chatChainDC, message.getGroups().toArray(new Group[0]));
        return true;
    }

    public static void AddGroupToConfig(ChatChainDC chatChainDC, Group... groups)
    {
        final GroupsConfig groupsConfig = chatChainDC.getGroupsConfig();
        for (final Group group : groups)
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getId()))
            {
                Channel channel = null;
                if (chatChainDC.getMainConfig().getCreateChannels())
                {
                    Guild guild = chatChainDC.getJda().getGuildById(chatChainDC.getMainConfig().getNewChannelServerId());
                    if (chatChainDC.getMainConfig().getNewChannelCategory() != 100000000000000000L)
                    {
                        Category category = guild.getCategoryById(chatChainDC.getMainConfig().getNewChannelCategory());
                        try
                        {
                            channel = category.createTextChannel(group.getName())
                                        .setTopic(group.getDescription())
                                        .submit().get();
                        }
                        catch (InterruptedException | ExecutionException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try
                        {
                            channel = guild.getController().createTextChannel(group.getName() == null ? "chatchain" : group.getName())
                                        .setTopic(group.getDescription())
                                        .submit().get();
                        }
                        catch (InterruptedException | ExecutionException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                if (channel != null)
                {
                    config.getChannelMapping().add(channel.getId());
                }
                groupsConfig.getGroupStorage().put(group.getId(), config);
            }
        }
        groupsConfig.save();
    }
}
