/******************************************************************************
 * BlogLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;

/**
 * This is the implementation of the blog business logic interface
 * @author Sakai App Builder -AZ
 */
/**
 * 
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class BlogLogicImpl implements BlogLogic {

	private static Log log = LogFactory.getLog(BlogLogicImpl.class);

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private BlogWowDao dao;
	public void setDao(BlogWowDao dao) {
		this.dao = dao;
	}


	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#canWriteBlog(org.sakaiproject.blogwow.model.BlogWowBlog, java.lang.String, java.lang.String)
	 */
	public boolean canWriteBlog(BlogWowBlog blog, String locationId, String userId) {
		log.warn("checking if can write for: " + userId + ", " + locationId + ": and blog=" + blog.getId() );
		if (! locationId.equals(blog.getLocation()) ) {
			// the siteId must match with the one in the blog
			return false;
		} else if (blog.getOwnerId().equals( userId ) ) {
			// owner can always modify blog
			return true;
		} else if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can modify blog
			return true;
		} else if ( externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE_ANY, locationId) ) {
			// users with permission in the specified site can modify blog from that site
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#getAllVisibleBlogs(java.lang.String)
	 */
	public List getAllVisibleBlogs(String locationId) {
		log.debug("Fetching visible blogs for site: " + locationId);
		// for now this is just all blogs
		List l = dao.findByProperties(BlogWowBlog.class, 
				new String[] {"location"}, new Object[] {locationId});
		return l;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogById(java.lang.Long)
	 */
	public BlogWowBlog getBlogById(Long id) {
		return (BlogWowBlog) dao.findById(BlogWowBlog.class, id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog)
	 */
	public void saveBlog(BlogWowBlog blog) {
		// set the owner and site to current if they are not set
		if (blog.getOwnerId() == null) {
			blog.setOwnerId( externalLogic.getCurrentUserId() );
		}
		if (blog.getLocation() == null) {
			blog.setLocation( externalLogic.getCurrentLocationId() );
		}
		if (blog.getDateCreated() == null) {
			blog.setDateCreated( new Date() );
		}
		// save item if new OR check if the current user can update the existing item
		if ( (blog.getId() == null) || 
				canWriteBlog(blog, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
			dao.save(blog);
			log.info("Saving blog: " + blog.getId() + ":" + blog.getProfile());
		} else {
			throw new SecurityException("Current user cannot save blog " + 
					blog.getId() + " because they do not have permission");
		}
	}

}
