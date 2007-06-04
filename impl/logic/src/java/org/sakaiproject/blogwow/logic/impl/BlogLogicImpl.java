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
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;

/**
 * This is the implementation of the blog business logic interface
 * @author Sakai App Builder -AZ
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
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogById(java.lang.Long)
	 */
	public BlogWowBlog getBlogById(Long entryId) {
		return (BlogWowBlog) dao.findById(BlogWowBlog.class, entryId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogByLocationAndUser(java.lang.String, java.lang.String)
	 */
	public BlogWowBlog getBlogByLocationAndUser(String locationId, String userId) {

		List l = dao.findByProperties(BlogWowBlog.class, 
				new String[] {"location", "ownerId"}, 
				new Object[] {locationId, userId}
			);

		if (l.size() <= 0) {
			// no blog found, create a new one
			if (checkCanWriteBlog(locationId, userId)) {
				String title = externalLogic.getUserDisplayName(userId);
				BlogWowBlog blog = new BlogWowBlog(userId, locationId, title, null, null, new Date());
				dao.save(blog);
				return blog;
			}
			return null;
		} else if (l.size() == 1) {
			// found existing blog
			return (BlogWowBlog) l.get(0);
		} else {
			throw new IllegalStateException("Found more than one blog for user ("+userId+") in location ("+locationId+"), only one is allowed");
		}


//		BlogWowBlog blog = new BlogWowBlog();
//	    blog.setLocation(groupref);
//	    blog.setOwnerId(userid);
//	    List<BlogWowBlog> blogs = blogWowDao.findByExample(blog);
//	    
//	    if (blogs.size() == 0) {
//	      blog.setDateCreated(new Date());
//	      blog.setProfile("");
//	      String title = "";
//	      try {
//	        title = userDirectoryService.getUser(userid).getDisplayName();
//	      } catch (UserNotDefinedException e) {
//	        e.printStackTrace();
//	      }
//	      blog.setTitle(title);
//	      blog.setImageUrl("");
//	      blogWowDao.save(blog);
//	      return blog.getId().toString();
//	    }
//	    else {
//	      return blogs.get(0).getId().toString();
//	    }
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#getAllVisibleBlogs(java.lang.String, java.lang.String, int, int)
	 */
	public List getAllVisibleBlogs(String locationId, String sortProperty, boolean ascending, int start, int limit) {
		if (sortProperty == null) {
			sortProperty = "title";
			ascending = true;
		}

		if (! ascending) {
			sortProperty += ByPropsFinder.DESC;
		}

		List l = dao.findByProperties(BlogWowBlog.class, 
				new String[] {"location"}, 
				new Object[] {locationId},
				new int[] {ByPropsFinder.EQUALS},
				new String[] {sortProperty},
				start, limit);
		return l;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog)
	 */
	public void saveBlog(BlogWowBlog blog) {
		// set the owner and location to current if they are not set
		if (blog.getOwnerId() == null) {
			blog.setOwnerId( externalLogic.getCurrentUserId() );
		}
		if (blog.getLocation() == null) {
			blog.setLocation( externalLogic.getCurrentLocationId() );
		}
		if (blog.getDateCreated() == null) {
			blog.setDateCreated( new Date() );
		}
		// save blog if new OR check if the current user can update the existing item
		if ( (blog.getId() == null) || 
				canWriteBlog(blog.getId(), externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
			dao.save(blog);
			log.info("Saving blog: " + blog.getId() + ":" + blog.getProfile());
		} else {
			throw new SecurityException("Current user cannot save blog " + 
					blog.getId() + " because they do not have permission");
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogLogic#canWriteBlog(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public boolean canWriteBlog(Long blogId, String locationId, String userId) {
		BlogWowBlog blog = getBlogById(blogId);
		if (! locationId.equals(blog.getLocation()) ) {
			// the location must match with the one in the blog
			return false;
		} else if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can write
			return true;
		} else if ( blog.getOwnerId().equals( userId ) &&
				externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_CREATE, locationId) ) {
			// users with permission in the specified location can write for that location
			return true;
		}
		return false;
	}

	/**
	 * Break out the security check to use it in more than one place
	 * @param locationId
	 * @param userId
	 * @return
	 */
	private boolean checkCanWriteBlog(String locationId, String userId) {
		if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can write
			return true;
		} else if ( externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_CREATE, locationId) ) {
			// users with permission in the specified location can write for that location
			return true;
		}
		return false;
	}

}
