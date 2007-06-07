/******************************************************************************
 * EntryLogicImpl.java - created by aaronz on Jun 2, 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;

/**
 * Implementation
 * @author Sakai App Builder -AZ
 */
public class EntryLogicImpl implements EntryLogic {

	private static Log log = LogFactory.getLog(EntryLogicImpl.class);

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private BlogWowDao dao;
	public void setDao(BlogWowDao dao) {
		this.dao = dao;
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#getAllVisibleEntries(java.lang.Long, java.lang.String, java.lang.String, boolean, int, int)
	 */
	public List<BlogWowEntry> getAllVisibleEntries(Long blogId, String userId, String sortProperty, boolean ascending, int start,
			int limit) {
		return getAllVisibleEntries(new Long[] {blogId}, userId, sortProperty, ascending, start, limit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#getAllVisibleEntries(java.lang.Long[], java.lang.String, java.lang.String, boolean, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<BlogWowEntry> getAllVisibleEntries(Long[] blogIds, String userId, String sortProperty, boolean ascending, int start,
			int limit) {

		if (sortProperty == null) {
			sortProperty = "dateCreated";
			ascending = false;
		}

		if (! ascending) {
			sortProperty += ByPropsFinder.DESC;
		}

		List l = new ArrayList();
		if ( externalLogic.isUserAdmin(userId) ) {
			// get all entries for a set of blogs
			l = dao.findByProperties(BlogWowEntry.class, 
					new String[] {"blog.id"}, 
					new Object[] {blogIds},
					new int[] {ByPropsFinder.EQUALS},
					new String[] {sortProperty},
					start, limit);
		} else {
			List locations = dao.getLocationsForBlogsIds(blogIds);
			// check current user perms on these locations to form lists of locations related to this users access
			List<String> readLocations = new ArrayList<String>(); // holds the locations where user has read perms
			List<String> readAnyLocations = new ArrayList<String>(); // holds the locations where user has read any perms
			for (Iterator iter = locations.iterator(); iter.hasNext();) {
				String location = (String) iter.next();
				if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ_ANY, location)) {
					readAnyLocations.add(location);
					readLocations.add(location);
				} else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ, location)) {
					readLocations.add(location);
				}
			}
			String[] readLocsArray = (String[]) readLocations.toArray(new String[] {});
			String[] readAnyLocsArray = (String[]) readAnyLocations.toArray(new String[] {});

			l = dao.getBlogPermEntries(blogIds, userId, readLocsArray, readAnyLocsArray, sortProperty, ascending, start, limit);
		}

		return l;
	}

    /* (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.EntryLogic#getEntryById(java.lang.Long, java.lang.String)
     */
    public BlogWowEntry getEntryById(Long entryId, String locationId) {
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
        if (entry == null) {
            // entry not found
            return null;
        } else if (entry.getOwnerId().equals(currentUserId)) {
            // owner can access from anywhere
            return entry;
        } else if ( locationId.equals(entry.getBlog().getLocation()) ) {
            if ( BlogConstants.PRIVACY_GROUP.equals(entry.getPrivacySetting()) &&
                    externalLogic.isUserAllowedInLocation(currentUserId, ExternalLogic.BLOG_ENTRY_READ, locationId) ) {
                return entry;
            } else if ( (BlogConstants.PRIVACY_GROUP.equals(entry.getPrivacySetting()) ||
                    BlogConstants.PRIVACY_GROUP_LEADER.equals(entry.getPrivacySetting()) ) &&
                        externalLogic.isUserAllowedInLocation(currentUserId, ExternalLogic.BLOG_ENTRY_READ_ANY, locationId) ) {
                return entry;
            }
        }
        throw new SecurityException("User ("+currentUserId+") cannot access this entry ("+entryId+") in this location ("+locationId+")");
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.EntryLogic#removeEntry(java.lang.Long, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public void removeEntry(Long entryId, String locationId) {
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowEntry entry = getEntryById(entryId, locationId);
        if (canWriteEntry(entryId, currentUserId)) {
            List l = dao.findByProperties(BlogWowComment.class, 
                    new String[] {"entry.id"}, 
                    new Object[] {entryId});
            if (l.size() == 0) {
                dao.delete(entry);
            } else {
                Set[] entitySets = new HashSet[2];
                entitySets[0] = new HashSet<BlogWowComment>();
                for (Iterator iter = l.iterator(); iter.hasNext();) {
                    BlogWowComment comment = (BlogWowComment) iter.next();
                    entitySets[0].add(comment);
                }

                entitySets[1] = new HashSet<BlogWowEntry>();
                entitySets[1].add(entry);

                dao.deleteMixedSet(entitySets);         
            }
        }
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.EntryLogic#saveEntry(org.sakaiproject.blogwow.model.BlogWowEntry, java.lang.String)
     */
    public void saveEntry(BlogWowEntry entry, String locationId) {
        entry.setDateModified(new Date());
        // set the owner to current if not set
        if (entry.getOwnerId() == null) {
            entry.setOwnerId( externalLogic.getCurrentUserId() );
        }
        if (entry.getDateCreated() == null) {
            entry.setDateCreated( new Date() );
        }
        // save entry if new OR check if the current user can update the existing item
        if ( canWriteEntry(entry.getId(), externalLogic.getCurrentUserId()) ) {
            dao.save(entry);
            log.info("Saving entry: " + entry.getId() + ":" + entry.getText());
        } else {
            throw new SecurityException("Current user cannot save entry " + 
                    entry.getId() + " because they do not have permission");
        }
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.EntryLogic#canWriteEntry(java.lang.Long, java.lang.String)
     */
    public boolean canWriteEntry(Long entryId, String userId) {
        BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
        if (entry == null) {
            throw new IllegalArgumentException("blog entry id is invalid: " + entryId);
        }

        if ( externalLogic.isUserAdmin(userId) ) {
            // the system super user can write
            return true;
        }

        BlogWowBlog blog = entry.getBlog();
        if (blog.getOwnerId().equals( userId ) &&
                externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation()) ) {
            // blog owner can write
            return true;
        } else if (entry.getOwnerId().equals( userId ) &&
                externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation()) ) {
            // entry owner can write
            return true;
        } else if ( externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE_ANY, blog.getLocation()) ) {
            // users with permission in the specified location can write for that location
            return true;
        }
        return false;
    }

}
