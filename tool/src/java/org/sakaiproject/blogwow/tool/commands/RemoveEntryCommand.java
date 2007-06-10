package org.sakaiproject.blogwow.tool.commands;

import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.site.api.Site;

public class RemoveEntryCommand {
  private EntryLogic entryLogic;
  private Site site;
  
  public String entryId;
  
  public String execute() {
    entryLogic.removeEntry(entryId, site.getReference());
    return "removed";
  }

  public void setEntryLogic(EntryLogic entryLogic) {
    this.entryLogic = entryLogic;
  }
  
  public void setSite(Site site) {
      this.site = site;
  }
}
