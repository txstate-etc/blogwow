package org.sakaiproject.blogwow.tool.otp;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;

import uk.org.ponder.beanutil.WriteableBeanLocator;

public class EntryLocator implements WriteableBeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static String NEW_1 = NEW_PREFIX + "1";

    private ExternalLogic externalLogic;
    private EntryLogic entryLogic;
    private BlogLogic blogLogic;

    private Map<String, BlogWowEntry> delivered = new HashMap<String, BlogWowEntry>();

    public Object locateBean(String name) {
        String locationId = externalLogic.getCurrentLocationId();
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowEntry togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                BlogWowBlog blog = blogLogic.getBlogByLocationAndUser(locationId, currentUserId);
                if (blog == null) {
                    throw new IllegalStateException("Could not get blog for the current user ("+currentUserId+") and location ("+locationId+")");
                }
                togo = new BlogWowEntry(blog, currentUserId, null, null, BlogConstants.PRIVACY_PUBLIC, null);
            } else {
                togo = entryLogic.getEntryById(name, locationId);
            }
            delivered.put(name, togo);
        }
        return togo;
    }

    public String publishAll() {
    	for (String key : delivered.keySet()) {
            BlogWowEntry entry = delivered.get(key);
            if (key.startsWith(NEW_PREFIX)) {
                // could do stuff here
            }
            entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
        }
        return "published";
    }

    public String saveAll() {
        for (BlogWowEntry entry : delivered.values()) {
            entry.setPrivacySetting(BlogConstants.PRIVACY_PRIVATE);
            entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
        }
        return "saved";
    }

    public String removeAll() {
    	for (BlogWowEntry entry : delivered.values()) {
            entryLogic.removeEntry(entry.getId(), externalLogic.getCurrentLocationId());
        }
        return "removed";
    }

    public boolean remove(String beanname) {
        entryLogic.removeEntry(beanname, externalLogic.getCurrentLocationId());
        delivered.remove(beanname);
        return true;
    }

    public void set(String beanname, Object toset) {
        throw new UnsupportedOperationException("Not implemented");
    }



    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
