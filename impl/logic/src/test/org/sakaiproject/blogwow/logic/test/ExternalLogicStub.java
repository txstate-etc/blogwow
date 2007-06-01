/******************************************************************************
 * ExternalLogicStub.java - created by aaronz on 1 Jun 2007
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

package org.sakaiproject.blogwow.logic.test;

import org.sakaiproject.blogwow.logic.ExternalLogic;

/**
 * Stub class for the external logic impl (for testing),
 * also contains test constants
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class ExternalLogicStub implements ExternalLogic {

	/**
	 * current user, access level user in LOCATION_ID1
	 */
	public final static String USER_ID = "user-11111111";
	public final static String USER_DISPLAY = "Aaron Zeckoski";
	public final static String ADMIN_USER_ID = "admin";
	public final static String ADMIN_USER_DISPLAY = "Administrator";
	/**
	 * maintain level user in LOCATION_ID1
	 */
	public final static String MAINT_USER_ID = "main-22222222";
	public final static String MAINT_USER_DISPLAY = "Maint User";
	public final static String INVALID_USER_ID = "invalid-UUUUUU";

	/**
	 * current location
	 */
	public final static String LOCATION1_ID = "/site/ref-1111111";
	public final static String LOCATION1_TITLE = "Location 1 title";
	public final static String LOCATION2_ID = "/site/ref-22222222";
	public final static String LOCATION2_TITLE = "Location 2 title";
	public final static String INVALID_LOCATION_ID = "invalid-LLLLLLLL";


	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentLocationId()
	 */
	public String getCurrentLocationId() {
		return LOCATION1_ID;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getLocationTitle(java.lang.String)
	 */
	public String getLocationTitle(String locationId) {
		if (locationId.equals(LOCATION1_ID)) {
			return LOCATION1_TITLE;
		} else if (locationId.equals(LOCATION2_ID)) {
			return LOCATION2_TITLE;
		}
		return "--------";
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentUserId()
	 */
	public String getCurrentUserId() {
		return USER_ID;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getUserDisplayName(java.lang.String)
	 */
	public String getUserDisplayName(String userId) {
		if (userId.equals(USER_ID)) {
			return USER_DISPLAY;
		} else if (userId.equals(MAINT_USER_ID)) {
			return MAINT_USER_DISPLAY;
		} else if (userId.equals(ADMIN_USER_ID)) {
			return ADMIN_USER_DISPLAY;
		}
		return "----------";
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		if (userId.equals(ADMIN_USER_ID)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAllowedInLocation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
		if (userId.equals(USER_ID)) {
			if (locationId.equals(LOCATION1_ID)) {
				if (permission.equals(BLOG_CREATE) ||
						permission.equals(BLOG_ENTRY_WRITE) ||
						permission.equals(BLOG_COMMENTS_ADD) ) {
					return true;
				}
			}
		} else if (userId.equals(MAINT_USER_ID)) {
			if (locationId.equals(LOCATION1_ID)) {
				return true; // can do anything in context 1
			}
		} else if (userId.equals(ADMIN_USER_ID)) {
			// admin can do anything in any context
			return true;
		}
		return false;
	}

}
