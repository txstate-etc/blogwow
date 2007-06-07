package org.sakaiproject.blogwow.tool.commands;

import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.site.api.Site;

public class RemoveEntryCommand {
  private EntryLogic entryLogic;
  private Site site;
  
  public String entryId;
  
  
  public String execute() {
    Long id = new Long(entryId);
    entryLogic.removeEntry(id, site.getReference());
    return "removed";
  }

  public void setEntryLogic(EntryLogic entryLogic) {
    this.entryLogic = entryLogic;
  }
  
  public void setSite(Site site) {
      this.site = site;
  }
}
