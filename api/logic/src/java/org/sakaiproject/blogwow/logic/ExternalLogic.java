/******************************************************************************
 * ExternalLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;


/**
 * This is the interface for logic which is external to our app logic
 * @author Sakai App Builder -AZ
 */
public interface ExternalLogic {

	// permissions
	public final static String BLOG_CREATE = "blogwow.create";
	public final static String BLOG_ENTRY_WRITE = "blogwow.entry.write";
	public final static String BLOG_ENTRY_WRITE_ANY = "blogwow.entry.write.any";
	public final static String BLOG_ENTRY_READ_ANY = "blogwow.entry.read.any";
	public final static String BLOG_COMMENTS_ADD = "blogwow.comments.add";

	/**
	 * Get the display name for a user by their unique id
	 * @param userId the current sakai user id (not username)
	 * @return display name (probably firstname lastname)
	 */
	public String getUserDisplayName(String userId);

	/**
	 * @return the current sakai user id (not username)
	 */
	public String getCurrentUserId();

	/**
	 * @return the current sakai context
	 */
	public String getCurrentContext();

	/**
	 * Check if this user has super admin access
	 * @param userId the internal user id (not username)
	 * @return true if the user has admin access, false otherwise
	 */
	public boolean isUserAdmin(String userId);

	/**
	 * Check if a user has a specified permission within a context, primarily
	 * a convenience method and passthrough
	 * 
	 * @param userId the internal user id (not username)
	 * @param permission a permission string constant
	 * @param context the internal unique sakai context (represents a site or group)
	 * @return true if allowed, false otherwise
	 */
	public boolean isUserAllowedInContext(String userId, String permission, String context);

}
