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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.logic.entity.BlogGroupRssEntityProvider;
import org.sakaiproject.blogwow.logic.entity.BlogRssEntityProvider;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.entitybroker.IdEntityReference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * This is the implementation for logic which is external to our app logic
 * 
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

    private EntityBroker entityBroker;
    public void setEntityBroker(EntityBroker entityBroker) {
        this.entityBroker = entityBroker;
    }

    private static final String ANON_USER_ATTRIBUTE = "AnonUserAttribute";

    /**
     * Place any code that should run when this class is initialized by spring here
     */
    public void init() {
        log.debug("init");
        // register Sakai permissions for this tool
        functionManager.registerFunction(BLOG_CREATE);
        functionManager.registerFunction(BLOG_ENTRY_WRITE);
        functionManager.registerFunction(BLOG_ENTRY_WRITE_ANY);
        functionManager.registerFunction(BLOG_ENTRY_READ);
        functionManager.registerFunction(BLOG_ENTRY_READ_ANY);
        functionManager.registerFunction(BLOG_COMMENTS_ADD);
        functionManager.registerFunction(BLOG_COMMENTS_REMOVE_ANY);
    }

    public String getCurrentLocationId() {
        try {
            Site s = (Site) siteService.getSite(toolManager.getCurrentPlacement().getContext());
            return s.getReference(); // get the entity reference to the site
        } catch (IdUnusedException e) {
            return NO_LOCATION;
        }
    }

    public String getLocationTitle(String locationId) {
        try {
            Site site = siteService.getSite(locationId);
            return site.getTitle();
        } catch (IdUnusedException e) {
            log.warn("Cannot get the info about locationId: " + locationId);
            return "----------";
        }
    }

    public String getCurrentUserId() {
        String userId = sessionManager.getCurrentSessionUserId();
        if (userId == null) {
            // if no user found then fake like there is one for this session,
            // we do not want to actually create a user though
            Session session = sessionManager.getCurrentSession();
            userId = (String) session.getAttribute(ANON_USER_ATTRIBUTE);
            if (userId == null) {
                userId = ANON_USER_PREFIX + new Date().getTime();
                session.setAttribute(ANON_USER_ATTRIBUTE, userId);
            }
        }
        return userId;
    }

    public String getUserDisplayName(String userId) {
        try {
            User user = userDirectoryService.getUser(userId);
            return user.getDisplayName();
        } catch (UserNotDefinedException ex) {
            log.error("Could not get user from userId: " + userId, ex);
        }
        if (userId.startsWith(ANON_USER_PREFIX)) {
            return "Anonymous User";
        }
        return "----------";
    }

    public boolean isUserAdmin(String userId) {
        return securityService.isSuperUser(userId);
    }

    public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
        if (securityService.unlock(userId, permission, locationId)) {
            return true;
        }
        return false;
    }

    public String getBlogRssUrl(Long blogId) {
        return entityBroker.getEntityURL(
                new IdEntityReference(BlogRssEntityProvider.ENTITY_PREFIX, blogId.toString()).toString());
    }

    public String getBlogLocationRssUrl(String locationId) {
        return entityBroker.getEntityURL(
                new IdEntityReference(BlogGroupRssEntityProvider.ENTITY_PREFIX, locationId).toString());
    }

}
