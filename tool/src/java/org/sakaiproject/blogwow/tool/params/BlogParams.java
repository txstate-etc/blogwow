package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/*
 * These view parameters specify what you need to pull up a single
 * blog. The location of the blog and the owner's userId.
 * 
 * The location is going to be a ref something like:
 *   /site/chem101 or
 *   /user/userid
 */
public class BlogParams extends SimpleViewParameters {
  public String blogid;
  
  public String entryid;
  public boolean showcomments;
  
  public BlogParams() {}
  public BlogParams(String viewid) {
    this.viewID = viewid;
  }
  
  public BlogParams(String viewid, String blogid) {
    this.viewID = viewid;
    this.blogid = blogid;
  }
  
  public BlogParams(String viewid, String blogid, String entryid, boolean showcomments) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = entryid;
      this.showcomments = showcomments;
  }
  
  public String getParseSpec() {
    return super.getParseSpec() + ",blogid,entryid,showcomments";
  }
}
