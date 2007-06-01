/******************************************************************************
 * BlogLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;

/**
 * This is the interface for the blog app Logic
 * @author Sakai App Builder -AZ
 */
public interface BlogLogic {

	/**
	 * This returns a blog based on an id
	 * @param id the id of the blog to fetch
	 * @return a BlogWowBlog or null if none found
	 */
	public BlogWowBlog getBlogById(Long id);

	/**
	 * Save (Create or Update) a blog (uses the current site)
	 * @param blog the BlogWowBlog to create or update
	 */
	public void saveBlog(BlogWowBlog blog);

	/**
	 * Check if a specified user can write this blog in a specified site
	 * @param blog to be modified or removed
	 * @param siteId the Sakai id of the site
	 * @param userId the Sakai id of the user
	 * @return true if blog can be modified, false otherwise
	 */
	public boolean canWriteBlog(BlogWowBlog blog, String siteId, String userId);

	/**
	 * This returns a List of blogs for a specified site
	 * @param siteId siteId of the site
	 * @return a List of BlogWowBlog objects
	 */
	public List getAllVisibleBlogs(String siteId);

}
