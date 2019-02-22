package co.chatchain.dc.messages.objects;

import lombok.Getter;

import java.util.List;

public class GetGroupsResponseMessage
{

    @Getter
    private List<Group> groups;

}
