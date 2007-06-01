/******************************************************************************
 * ExternalLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * This is the implementation of the blog business logic interface
 * @author Sakai App Builder -AZ
 */
public class ExternalLogicImpl implements ExternalLogic {

	private static Log log = LogFactory.getLog(ExternalLogicImpl.class);

	private FunctionManager functionManager;
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	private ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	private SecurityService securityService;
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	private SessionManager sessionManager;
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	private SiteService siteService;
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}


	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
		// register Sakai permissions for this tool
		functionManager.registerFunction(BLOG_CREATE);
		functionManager.registerFunction(BLOG_ENTRY_WRITE);
		functionManager.registerFunction(BLOG_ENTRY_WRITE_ANY);
		functionManager.registerFunction(BLOG_ENTRY_READ_ANY);
		functionManager.registerFunction(BLOG_COMMENTS_ADD);
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.BlogWowLogic#getCurrentUserDisplayName()
	 */
	public String getUserDisplayName(String userId) {
		try {
			return userDirectoryService.getUser(userId).getDisplayName();
		} catch (UserNotDefinedException e) {
			log.error("Cannot get user displayname for id: " + userId);
		}
		return userId;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentContext()
	 */
	public String getCurrentContext() {
		return toolManager.getCurrentPlacement().getContext();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentUserId()
	 */
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		return securityService.isSuperUser(userId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAllowedInContext(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInContext(String userId, String permission, String context) {
		String reference = siteService.siteReference(context);
		if ( securityService.unlock(userId, permission, reference) ) {
			return true;
		}
		return false;
	}

}
