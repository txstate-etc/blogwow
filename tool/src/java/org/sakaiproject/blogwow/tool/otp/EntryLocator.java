package org.sakaiproject.blogwow.tool.otp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;

import uk.org.ponder.beanutil.BeanLocator;

public class EntryLocator implements BeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static String NEW_1 = NEW_PREFIX + "1";

    private ExternalLogic externalLogic;
	private EntryLogic entryLogic;
	private BlogLogic blogLogic;

	private Map<String, BlogWowEntry> delivered = new HashMap<String, BlogWowEntry>();

	public Object locateBean(String name) {
        String locationId = externalLogic.getCurrentLocationId();
        String currentUserId = externalLogic.getCurrentLocationId();
        BlogWowEntry togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                BlogWowBlog blog = blogLogic.getBlogByLocationAndUser(locationId, currentUserId);
                togo = new BlogWowEntry(blog, currentUserId, null, null, BlogConstants.PRIVACY_PUBLIC, null);
            } else {
                togo = entryLogic.getEntryById(name, locationId);
            }
            delivered.put(name, togo);
        }
        return togo;
	}

	public String publishAll() {
        for (Iterator it = delivered.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            BlogWowEntry entry = delivered.get(key);
            if (key.startsWith(NEW_PREFIX)) {
                // could do stuff here
            }
            entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
        }
        return "published";
	}

	public String saveAll() {
		Collection entries = delivered.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entry.setPrivacySetting(BlogConstants.PRIVACY_PRIVATE);
			entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
		}
		return "saved";
	}

	public String removeAll() {
		Collection entries = delivered.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entryLogic.removeEntry(entry.getId(), externalLogic.getCurrentLocationId());
		}
		return "removed";
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
