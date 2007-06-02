/******************************************************************************
 * CommentLogic.java - created by Sakai App Builder -AZ
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

import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;

/**
 * Logic for handling blog comments
 * @author Sakai App Builder -AZ
 */
public interface CommentLogic {

	/**
	 * Get a blog comment by its unique id
	 * @param commentId a unique id for a {@link BlogWowComment}
	 * @return a blog comment or null if not found
	 */
	public BlogWowComment getCommentById(Long commentId);

	/**
	 * Create a comment
	 * @param comment a blog comment
	 */
	public void saveComment(BlogWowComment comment);

	/**
	 * Remove a blog comment
	 * @param commentId a unique id for a {@link BlogWowComment}
	 */
	public void removeComment(Long commentId);

	/**
	 * @param entryId a unique id for a {@link BlogWowEntry}
	 * @param sortProperty the name of the {@link BlogWowEntry} property to sort on
	 * or null to sort by default property (dateCreated desc)
	 * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
	 * @param start the entry number to start on (based on current sort rules), first entry is 0
	 * @param limit the maximum number of entries to return, 0 returns as many entries as possible
	 * @return a list of {@link BlogWowComment} objects
	 */
	public List getComments(Long entryId, String sortProperty, boolean ascending, int start, int limit);

	/**
	 * Check if a user can remove a blog comment
	 * @param commentId a unique id for a {@link BlogWowComment}
	 * @param userId the internal user id (not username)
	 * @return true if the user can remove this comment, false otherwise
	 */
	public boolean canRemoveComment(Long commentId, String userId);

	/**
	 * Check if a user can add a comment to a blog
	 * @param entryId a unique id for a {@link BlogWowEntry}
	 * @param userId the internal user id (not username)
	 * @return true if they can add a comment, false otherwise
	 */
	public boolean canAddComment(Long entryId, String userId);

}
