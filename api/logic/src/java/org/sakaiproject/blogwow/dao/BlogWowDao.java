/******************************************************************************
 * BlogWowDao.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.dao;

import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.CompleteGenericDao;

/**
 * This is a specialized DAO that allows the developer to extend
 * the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public interface BlogWowDao extends CompleteGenericDao {

	/**
	 * Get a list of unique locations from an array of blogIds
	 * @param blogIds an array of unique ids of {@link BlogWowBlog}
	 * @return a list of unique {@link String}s which represent locations
	 */
	public List getLocationsForBlogsIds(Long[] blogIds);

	/**
	 * Get blog entries efficiently
	 * @param blogIds an array of unique ids of {@link BlogWowBlog} which we will return entries from
	 * @param userId the internal user id (not username), if null then return public entries
	 * @param locations an array of locationIds which we will return all entries for
	 * @param sortProperty the name of the {@link BlogWowEntry} property to sort on
	 * or null to sort by default property (dateCreated desc)
	 * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
	 * @param start the entry number to start on (based on current sort rules), first entry is 0
	 * @param limit the maximum number of entries to return, 0 returns as many entries as possible
	 * @return a list of {@link BlogWowEntry} objects
	 */
	public List getBlogEntries(Long[] blogIds, String userId, String[] locations, 
			String sortProperty, boolean ascending, int start, int limit);

}
