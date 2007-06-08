/******************************************************************************
 * ExternalLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2007 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import org.sakaiproject.blogwow.model.BlogWowBlog;

/**
 * This is the interface for logic which is external to our app logic
 * 
 * @author Sakai App Builder -AZ
 */
public interface ExternalLogic {

    public final static String NO_LOCATION = "noLocationAvailable";

    public final static String ANON_USER_PREFIX = "anon_";

    // permissions
    public final static String BLOG_CREATE = "blogwow.create";

    public final static String BLOG_ENTRY_WRITE = "blogwow.entry.write";

    public final static String BLOG_ENTRY_WRITE_ANY = "blogwow.entry.write.any";

    public final static String BLOG_ENTRY_READ = "blogwow.entry.read";

    public final static String BLOG_ENTRY_READ_ANY = "blogwow.entry.read.any";

    public final static String BLOG_COMMENTS_ADD = "blogwow.comments.add";

    public final static String BLOG_COMMENTS_REMOVE_ANY = "blogwow.comments.remove.any";

    /**
     * @return the current sakai user id (not username)
     */
    public String getCurrentUserId();

    /**
     * Get the display name for a user by their unique id
     * 
     * @param userId
     *            the current sakai user id (not username)
     * @return display name (probably firstname lastname) or "----------" (10 hyphens) if none found
     */
    public String getUserDisplayName(String userId);

    /**
     * @return the current location id of the current user
     */
    public String getCurrentLocationId();

    /**
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @return the title for the context or "--------" (8 hyphens) if none found
     */
    public String getLocationTitle(String locationId);

    /**
     * Check if this user has super admin access
     * 
     * @param userId
     *            the internal user id (not username)
     * @return true if the user has admin access, false otherwise
     */
    public boolean isUserAdmin(String userId);

    /**
     * Check if a user has a specified permission within a context, primarily a convenience method and passthrough
     * 
     * @param userId
     *            the internal user id (not username)
     * @param permission
     *            a permission string constant
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @return true if allowed, false otherwise
     */
    public boolean isUserAllowedInLocation(String userId, String permission, String locationId);

    /**
     * Get the Full URL to the rss feed to this blog
     * 
     * @param blogId
     *            the id of a {@link BlogWowBlog} object
     * @return the full url for the rss feed
     */
    public String getBlogRssUrl(Long blogId);

    /**
     * Get the Full URL to the rss feed for all blogs in a location
     * 
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @return the full url for the rss feed
     */
    public String getBlogLocationRssUrl(String locationId);

}
