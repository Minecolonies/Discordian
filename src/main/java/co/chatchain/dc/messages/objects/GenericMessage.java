package co.chatchain.dc.messages.objects;

import lombok.Getter;
import lombok.Setter;

public class GenericMessage
{

    /**
     * Which group the client has sent or received the message in.
     */
    @Getter
    @Setter
    private Group group;

    /**
     * The user object tied to the message.
     */
    @Getter
    @Setter
    private User user;

    /**
     * The message sent.
     */
    @Getter
    @Setter
    private String message;

    /**
     * The client who sent the message (Only used when receiving)
     */
    @Getter
    private Client sendingClient;

    /**
     * Whether to send the message to the client again.
     */
    @Getter
    private boolean sendToSelf;

    /**
     * Initializes a new GenericMessage
     *
     * @param group to which the message belongs.
     * @param user which is sending the message
     * @param message that is being sent
     */
    public GenericMessage(final Group group, final User user, final String message, final boolean sendToSelf)
    {
        this.group = group;
        this.user = user;
        this.message = message;
        //this.sendingClient = sendingClient;
        this.sendToSelf = sendToSelf;
    }

    public GenericMessage(final Group group, final User user, final String message)
    {
        this(group, user, message, false);
    }

}
