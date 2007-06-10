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

    private ExternalLogic externalLogic;
	private EntryLogic entryLogic;
	private BlogLogic blogLogic;

	private Map<String, BlogWowEntry> entrymap = new HashMap<String, BlogWowEntry>();

	public Object locateBean(String name) {
        String locationId = externalLogic.getCurrentLocationId();
		BlogWowEntry togo = entrymap.get(name);
		if (togo == null && name.startsWith("NEW")) {
			BlogWowEntry entry = new BlogWowEntry();
			BlogWowBlog blog = blogLogic.getBlogByLocationAndUser(locationId, externalLogic.getCurrentUserId());
			entry.setBlog(blog);
			entry.setText("");
			entry.setTitle("");
			entry.setPrivacySetting(BlogConstants.PRIVACY_PUBLIC);
			entrymap.put(name, entry);
			togo = entry;
		}
		else if (togo == null) {
			togo = entryLogic.getEntryById(new Long(name), locationId);
			entrymap.put(name, togo);
		}
		return togo;
	}

	public String publishAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
		}
		return "published";
	}

	public String saveAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entry.setPrivacySetting(BlogConstants.PRIVACY_PRIVATE);
			entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
		}
		return "saved";
	}

	public String removeAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entryLogic.removeEntry(entry.getId(), externalLogic.getCurrentLocationId());
		}
		return "removed";
	}

	public String cancelAll() {
		entrymap.clear();
		return "canceled";
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
