package org.sakaiproject.blogwow.tool.commands;

import org.sakaiproject.blogwow.logic.EntryLogic;

public class RemoveEntryCommand {
  private EntryLogic entryLogic;
  
  public String entryId;
  
  public String execute() {
    Long id = new Long(entryId);
    entryLogic.removeEntry(id);
    return "removed";
  }

  public void setEntryLogic(EntryLogic entryLogic) {
    this.entryLogic = entryLogic;
  }
}
