/******************************************************************************
 * BlogLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;

/**
 * This is the implementation of the blog business logic interface
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogLogicImpl implements BlogLogic {

   private static Log log = LogFactory.getLog(BlogLogicImpl.class);

   private ExternalLogic externalLogic;
   public void setExternalLogic(ExternalLogic externalLogic) {
      this.externalLogic = externalLogic;
   }

   private BlogWowDao dao;
   public void setDao(BlogWowDao dao) {
      this.dao = dao;
   }

   /**
    * Place any code that should run when this class is initialized by spring here
    */
   public void init() {
      log.debug("init");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogByLocationAndUser(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   public BlogWowBlog getBlogByLocationAndUser(String locationId, String userId) {
      List<BlogWowBlog> l = dao.findByProperties(BlogWowBlog.class, new String[] { "location", "ownerId" }, new Object[] { locationId, userId });

      if (l.size() <= 0) {
         // no blog found, create a new one
         if (canWriteBlog(null, locationId, userId)) {
            BlogWowBlog blog = new BlogWowBlog(userId, locationId, null, null, null, new Date(), null);
            saveUserBlog(blog, locationId, userId);
            return blog;
         }
         return null;
      } else if (l.size() == 1) {
         // found existing blog
         return (BlogWowBlog) l.get(0);
      } else {
         throw new IllegalStateException("Found more than one blog for user (" + userId + ") in location (" + locationId
               + "), only one is allowed");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.logic.BlogLogic#getAllVisibleBlogs(java.lang.String, java.lang.String, boolean, int, int)
    */
   @SuppressWarnings("unchecked")
   public List<BlogWowBlog> getAllVisibleBlogs(String locationId, String sortProperty, boolean ascending, int start, int limit) {
      if (sortProperty == null) {
         sortProperty = "title";
         ascending = true;
      }

      if (!ascending) {
         sortProperty += ByPropsFinder.DESC;
      }

      List l = dao.findByProperties(BlogWowBlog.class, new String[] { "location" }, new Object[] { locationId },
            new int[] { ByPropsFinder.EQUALS }, new String[] { sortProperty }, start, limit);
      return l;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogById(java.lang.Long)
    */
   public BlogWowBlog getBlogById(String blogId) {
      return (BlogWowBlog) dao.findById(BlogWowBlog.class, blogId);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.logic.BlogLogic#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog, java.lang.String)
    */
   public void saveBlog(BlogWowBlog blog, String locationId) {
      // set the owner to current if not set
      String currentUserId = externalLogic.getCurrentUserId();
      // this check depends that the id of a non-persistent blog is null
      if (canWriteBlog(blog.getId(), locationId, currentUserId)) {
         saveUserBlog(blog, locationId, currentUserId);
         log.info("Saved blog: " + blog.getId() + ":" + blog.getProfile());
      } else {
         throw new SecurityException("Current user cannot save blog " + blog.getId() + ":" + blog.getTitle()
               + " because they do not have permission");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.logic.BlogLogic#canWriteBlog(java.lang.Long, java.lang.String, java.lang.String)
    */
   public boolean canWriteBlog(String blogId, String locationId, String userId) {
      if (externalLogic.isUserAdmin(userId)) {
         return true;
      }
      BlogWowBlog blog = blogId == null? null : getBlogById(blogId);
      if (blog != null && !locationId.equals(blog.getLocation())) {
         // the location must match with the one in the blog, if there is one
         return false;
      }
      if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_CREATE, locationId)) {
         return (blog == null || blog.getOwnerId().equals(userId));
      }
      return false;
   }

   /**
    * Does not do security checks
    * @param blog
    * @param locationId
    * @param userId
    */
   private void saveUserBlog(BlogWowBlog blog, String locationId, String userId) {
      if (blog.getOwnerId() == null) {
         blog.setOwnerId(userId);
      }
      if (blog.getDateCreated() == null) {
         blog.setDateCreated(new Date());
      }
      if (blog.getTitle() == null || "".equals(blog.getTitle())) {
         blog.setTitle( externalLogic.getUserDisplayName(userId) );
      }
      if (blog.getProfile() != null && blog.getProfile().length() > 0) {
         // clean up the blog profile
         blog.setProfile( externalLogic.cleanupUserStrings(blog.getProfile()) );            
      }
      dao.save(blog);
   }
}
