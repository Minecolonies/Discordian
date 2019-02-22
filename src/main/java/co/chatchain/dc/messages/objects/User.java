package co.chatchain.dc.messages.objects;

import lombok.Getter;

public class User
{
    @Getter
    private String name;

    public User(final String name)
    {
        this.name = name;
    }

}
