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
 * Stub class for the external logic impl (for testing)
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class ExternalLogicStub implements ExternalLogic {

	/**
	 * current, access level user in CONTEXT 1
	 */
	public final static String USER_ID = "user-11111111";
	public final static String USER_DISPLAY = "Aaron Zeckoski";
	public final static String ADMIN_USER_ID = "admin";
	public final static String ADMIN_USER_DISPLAY = "Administrator";
	/**
	 * maintain level user in CONTEXT 1
	 */
	public final static String MAINT_USER_ID = "main-22222222";
	public final static String MAINT_USER_DISPLAY = "Maint User";
	public final static String INVALID_USER_ID = "invalid-UUUUUU";

	/**
	 * current
	 */
	public final static String CONTEXT1 = "testContext1";
	public final static String CONTEXT1_TITLE = "C1 title";
	public final static String CONTEXT2 = "testContext2";
	public final static String CONTEXT2_TITLE = "C2 title";
	public final static String INVALID_CONTEXT = "invalid-CCCCCCCC";

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentContext()
	 */
	public String getCurrentContext() {
		return CONTEXT1;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getContextTitle(java.lang.String)
	 */
	public String getContextTitle(String context) {
		if (context.equals(CONTEXT1)) {
			return CONTEXT1_TITLE;
		} else if (context.equals(CONTEXT2)) {
			return CONTEXT2_TITLE;
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
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAllowedInContext(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInContext(String userId, String permission, String context) {
		if (userId.equals(USER_ID)) {
			if (context.equals(CONTEXT1)) {
				if (permission.equals(BLOG_CREATE) ||
						permission.equals(BLOG_ENTRY_WRITE) ||
						permission.equals(BLOG_COMMENTS_ADD) ) {
					return true;
				}
			}
		} else if (userId.equals(MAINT_USER_ID)) {
			if (context.equals(CONTEXT1)) {
				return true; // can do anything in context 1
			}
		} else if (userId.equals(ADMIN_USER_ID)) {
			// admin can do anything in any context
			return true;
		}
		return false;
	}

}
