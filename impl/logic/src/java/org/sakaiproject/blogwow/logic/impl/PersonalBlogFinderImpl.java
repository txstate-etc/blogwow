package org.sakaiproject.blogwow.logic.impl;

import java.util.Date;
import java.util.List;

import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.PersonalBlogFinder;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

public class PersonalBlogFinderImpl implements PersonalBlogFinder {
  private BlogWowDao blogWowDao;
  private UserDirectoryService userDirectoryService;

  public String getBlogID(String groupref, String userid) {
    //TODO add security checks
    BlogWowBlog blog = new BlogWowBlog();
    blog.setLocation(groupref);
    blog.setOwnerId(userid);
    List<BlogWowBlog> blogs = blogWowDao.findByExample(blog);
    
    if (blogs.size() == 0) {
      blog.setDateCreated(new Date());
      blog.setProfile("");
      String title = "";
      try {
        title = userDirectoryService.getUser(userid).getDisplayName();
      } catch (UserNotDefinedException e) {
        e.printStackTrace();
      }
      blog.setTitle(title);
      blog.setImageUrl("");
      blogWowDao.save(blog);
      return blog.getId().toString();
    }
    else {
      return blogs.get(0).getId().toString();
    }
  }

  public BlogWowDao getBlogWowDao() {
    return blogWowDao;
  }

  public void setBlogWowDao(BlogWowDao blogWowDao) {
    this.blogWowDao = blogWowDao;
  }

  public UserDirectoryService getUserDirectoryService() {
    return userDirectoryService;
  }

  public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
    this.userDirectoryService = userDirectoryService;
  }

}
