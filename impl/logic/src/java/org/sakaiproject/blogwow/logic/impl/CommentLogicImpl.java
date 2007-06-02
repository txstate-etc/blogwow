/******************************************************************************
 * CommentLogicImpl.java - created by aaronz on Jun 2, 2007
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

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;

/**
 * Implementation
 * @author Sakai App Builder -AZ
 */
public class CommentLogicImpl implements CommentLogic {

	private static Log log = LogFactory.getLog(CommentLogicImpl.class);

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private BlogWowDao dao;
	public void setDao(BlogWowDao dao) {
		this.dao = dao;
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#getCommentById(java.lang.Long)
	 */
	public BlogWowComment getCommentById(Long commentId) {
		return (BlogWowComment) dao.findById(BlogWowComment.class, commentId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#getComments(java.lang.Long, java.lang.String, boolean, int, int)
	 */
	public List getComments(Long entryId, String sortProperty, boolean ascending, int start, int limit) {
		if (sortProperty == null) {
			sortProperty = "dateCreated";
			ascending = false;
		}

		if (! ascending) {
			sortProperty += ByPropsFinder.DESC;
		}

		List l = dao.findByProperties(BlogWowComment.class, 
				new String[] {"entry.id"}, 
				new Object[] {entryId},
				new int[] {ByPropsFinder.EQUALS},
				new String[] {sortProperty},
				start, limit);
		return l;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#removeComment(java.lang.Long)
	 */
	public void removeComment(Long commentId) {
		BlogWowComment comment = getCommentById(commentId);
		dao.delete(comment);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#saveComment(org.sakaiproject.blogwow.model.BlogWowComment)
	 */
	public void saveComment(BlogWowComment comment) {
		comment.setDateCreated( new Date() );
		// set the owner to current if not set
		if (comment.getOwnerId() == null) {
			comment.setOwnerId( externalLogic.getCurrentUserId() );
		}
		// save comment if new only
		if ( comment.getId() == null ) {
			dao.save(comment);
			log.info("Saving comment: " + comment.getId() + ":" + comment.getText());
		} else {
			throw new IllegalStateException("Current comment cannot be saved, comments cannot be changed after they are saved");
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#canRemoveComment(java.lang.Long, java.lang.String)
	 */
	public boolean canRemoveComment(Long commentId, String userId) {
		log.debug("commentId: " + commentId + ", userId: " + userId );
		if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can remove comments
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.CommentLogic#canAddComment(java.lang.Long, java.lang.String)
	 */
	public boolean canAddComment(Long entryId, String userId) {
		log.debug("entryId: " + entryId + ", userId: " + userId );
		if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can write
			return true;
		}

		BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);;
		BlogWowBlog blog = entry.getBlog();
		if (blog.getOwnerId().equals( userId ) ||
				entry.getOwnerId().equals( userId ) ) {
			// blog and entry owner can add comments
			return true;
		} else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_COMMENTS_ADD, blog.getLocation()) ) {
			// users with permission in the specified location can add comments for that location
			return true;
		}
		return false;
	}

}
