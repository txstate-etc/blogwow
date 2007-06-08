package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class EntryParams extends SimpleViewParameters {
    public String entryid;
    public boolean showcomments;

    public EntryParams() {}

    public EntryParams(String viewid) {
        this.viewID = viewid;
    }

    public EntryParams(String viewid, String entryid) {
        this.viewID = viewid;
        this.entryid = entryid;
        this.showcomments = false;
    }

    public EntryParams(String viewid, String entryid, boolean showcomments) {
        this.viewID = viewid;
        this.entryid = entryid;
        this.showcomments = showcomments;
    }

    public String getParseSpec() {
        return super.getParseSpec() + ",entryid,showcomments";
    }
}
