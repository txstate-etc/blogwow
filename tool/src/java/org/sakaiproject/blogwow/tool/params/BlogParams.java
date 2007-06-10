package org.sakaiproject.blogwow.tool.params;

/*
 * These view parameters specify what you need to pull up a single
 * blog. The location of the blog and the owner's userId.
 * 
 * The location is going to be a ref something like:
 *   /site/chem101 or
 *   /user/userid
 */
public class BlogParams extends BlogEntryParams {
  
  public boolean showcomments;
  public boolean addcomment;
  
  public BlogParams() {}

 
  public BlogParams(String viewid, String blogid, String entryid, boolean showcomments) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = entryid;
      this.showcomments = showcomments;
      this.addcomment = false;
  }
  
  public BlogParams(String viewid, String blogid, String entryid, boolean showcomments, boolean addcomment) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = entryid;
      this.showcomments = showcomments;
      this.addcomment = addcomment;
  }
}
