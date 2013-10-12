package me.sablednah.legendquest.events;

import me.sablednah.legendquest.listeners.SkillListener;

import org.bukkit.command.CommandSender;


public class SkillCommandEvent extends SkillEvent
{
  private final CommandSender sender;
  private final String[] args;

  public SkillCommandEvent(CommandSender sender, String[] args)
  {
    this.sender = sender;
    this.args = args;
  }

  public void dispatch(SkillListener eventListener)
  {
    eventListener.onSkillCommand(this);
  }

  public CommandSender getSender() {
    return this.sender;
  }

  public String[] getArgs() {
    return this.args;
  }
}