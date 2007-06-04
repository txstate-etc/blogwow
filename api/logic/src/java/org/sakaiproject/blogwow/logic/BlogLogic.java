/******************************************************************************
 * BlogLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2007 Sakai Project/Sakai Foundation
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
	 * Get a blog based on its id
	 * @param blogId the id of a {@link BlogWowBlog} object
	 * @return a blog or null if not found
	 */
	public BlogWowBlog getBlogById(Long blogId);

	/**
	 * Get a blog based on a location and user,
	 * if none found then one is created for the user (if they have appropriate permissions)
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @param userId the internal user id (not username)
	 * @return a blog or null if cannot create one and none exists
	 */
	public BlogWowBlog getBlogByLocationAndUser(String locationId, String userId);

	/**
	 * Save (Create or Update) a blog (uses the current site)
	 * @param blog the BlogWowBlog to create or update
	 */
	public void saveBlog(BlogWowBlog blog);

	/**
	 * This returns a List of blogs for a specified site
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @param sortProperty the name of the {@link BlogWowBlog} property to sort on
	 * or null to sort by default property (title asc)
	 * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
	 * @param start the entry number to start on (based on current sort rules), first entry is 0
	 * @param limit the maximum number of entries to return, 0 returns as many entries as possible
	 * @return a List of {@link BlogWowBlog} objects
	 */
	public List getAllVisibleBlogs(String locationId, String sortProperty, boolean ascending, int start, int limit);

	/**
	 * Check if a specified user can write this blog in a specified site
	 * @param blogId the id of a {@link BlogWowBlog} object
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @param userId the internal user id (not username)
	 * @return true if blog can be modified, false otherwise
	 */
	public boolean canWriteBlog(Long blogId, String locationId, String userId);

}
