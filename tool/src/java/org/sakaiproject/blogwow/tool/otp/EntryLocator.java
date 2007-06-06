package org.sakaiproject.blogwow.tool.otp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.sakaiproject.site.api.Site;

import uk.org.ponder.beanutil.BeanLocator;

public class EntryLocator implements BeanLocator {
	private EntryLogic entryLogic;
	private BlogLogic blogLogic;
	private Site site;
	private String userid;

	private Map<String, BlogWowEntry> entrymap = new HashMap<String, BlogWowEntry>();

	public Object locateBean(String name) {
		BlogWowEntry togo = entrymap.get(name);
		if (togo == null && name.startsWith("NEW")) {
			BlogWowEntry entry = new BlogWowEntry();
			BlogWowBlog blog = blogLogic.getBlogByLocationAndUser(site.getReference(), userid);
			entry.setBlog(blog);
			entry.setText("");
			entry.setTitle("");
			entry.setPrivacySetting(BlogConstants.PRIVACY_PUBLIC);
			entrymap.put(name, entry);
			togo = entry;
		}
		else if (togo == null) {
			togo = entryLogic.getEntryById(new Long(name));
			entrymap.put(name, togo);
		}
		return togo;
	}

	public String publishAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entryLogic.saveEntry(entry);
		}
		return "published";
	}

	public String saveAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entry.setPrivacySetting(BlogConstants.PRIVACY_PRIVATE);
			entryLogic.saveEntry(entry);
		}
		return "saved";
	}

	public String removeAll() {
		Collection entries = entrymap.values();
		for (Iterator i = entries.iterator(); i.hasNext();) {
			BlogWowEntry entry = (BlogWowEntry) i.next();
			entryLogic.removeEntry(entry.getId());
		}
		return "removed";
	}

	public String cancelAll() {
		entrymap.clear();
		return "canceled";
	}

	public void setEntryLogic(EntryLogic entryLogic) {
		this.entryLogic = entryLogic;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setBlogLogic(BlogLogic blogLogic) {
		this.blogLogic = blogLogic;
	}

}
