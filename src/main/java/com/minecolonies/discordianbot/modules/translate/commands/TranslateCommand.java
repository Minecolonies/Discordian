package com.minecolonies.discordianbot.modules.translate.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.minecolonies.discordianbot.internal.BotCommand;
import com.minecolonies.discordianbot.util.GoogleTranslate;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

public class TranslateCommand extends BotCommand
{

    private String wrongArgumentsString;

    @Override
    public void enable()
    {
        this.name = "translate";
        this.aliases = new String[] {"t", "tr"};
        this.help = "translate a piece of text into the chosen language";
        this.arguments = "<language> <text to translate...>";
        this.wrongArgumentsString = ("Please supply arguments following: " + getArguments());
        super.enable();
    }

    @Override
    protected void execute(final CommandEvent event)
    {

        if (!event.getGuild().getMembersWithRoles(event.getGuild().getRoleById("240824658335629312")).contains(event.getMember()))
        {
            event.replyError("Apologies, you may not use this command!");
            return;
        }

        if (event.getArgs().isEmpty())
        {
            event.replyWarning(wrongArgumentsString);
        }
        else
        {
            // split the choices on all whitespace
            final List<String> arguments = Arrays.asList(event.getArgs().split("\\s+"));

            final StringBuilder builder = new StringBuilder();

            for (final String string : arguments.subList(1, arguments.size()))
            {
                builder.append(string);
                builder.append(" ");
            }

            final String[] argumentsRefined = new String[] {arguments.get(0), builder.toString()};

            if (arguments.size() < 2)
            {
                event.replyWarning(wrongArgumentsString);
            }
            else
            {
                try
                {
                    final String message = GoogleTranslate.translate(argumentsRefined[1], argumentsRefined[0]);

                    try
                    {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
                        embedBuilder.addField("Original Message: ", argumentsRefined[1], false);

                        EmbedBuilder embedBuilder1 = new EmbedBuilder();
                        embedBuilder1.addField("Translated Message: ", message, false);

                        event.reply(embedBuilder.build());
                        event.reply(embedBuilder1.build());
                        event.getMessage().delete().submit();
                    }
                    catch (IllegalArgumentException e)
                    {
                        if (argumentsRefined[1].length() > 1024)
                        {
                            event.replyError("Translation message may not be more that 1024 characters long.");
                        }
                        else if (message.length() > 1024)
                        {
                            event.replyError("Sorry, translated message was over 1024 characters long!");
                        }
                        else
                        {
                            throw e;
                        }
                    }
                }
                catch (Exception e)
                {
                    event.replyError("Sorry, we encountered an error!");
                    getDiscordianBot().getLogger().info("Translation attempt Errored: ", e);
                }
            }
        }

    }
}
