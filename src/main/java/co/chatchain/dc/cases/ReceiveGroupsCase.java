package co.chatchain.dc.cases;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.entities.messages.GetGroupsMessage;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.dc.ChatChainDC;
import co.chatchain.dc.configs.GroupConfig;
import co.chatchain.dc.configs.GroupsConfig;

import javax.inject.Inject;

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
        final GroupsConfig groupsConfig = chatChainDC.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getId(), config);
            }
        }
        groupsConfig.save();
        return true;
    }
}
