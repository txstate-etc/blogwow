package org.sakaiproject.blogwow.logic;

import org.sakaiproject.blogwow.model.BlogWowBlog;

/*
 * Fetches a blog id for a particular site/group.
 * 
 * If you don't have a blog there yet, but are allowed to
 * have one, your blog is automatically created.
 * 
 * We're returning a String ID regardless of whether or not
 * it's stored as a String or some number. This is mostly
 * for constructing OTP views that need the ID.
 */
public interface PersonalBlogFinder {
  public String getBlogID(String groupref, String userid);
}
