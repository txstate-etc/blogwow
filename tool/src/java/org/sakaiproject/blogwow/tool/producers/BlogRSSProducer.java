package org.sakaiproject.blogwow.tool.producers;

import java.util.List;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.params.BlogParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogRSSProducer implements 
  ViewComponentProducer, 
  ViewParamsReporter,
  ContentTypeReporter
{
  public static final String VIEWID = "blog_rss";
  
  public BlogLogic blogLogic;
  public EntryLogic entryLogic;
  public String userid;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    BlogParams params = (BlogParams) viewparams;
    
    BlogWowBlog blog = blogLogic.getBlogById(new Long(params.blogid));
    
    UIOutput.make(tofill, "channel-title", blog.getTitle());
    
    List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(new Long(params.blogid), userid, null, true, 0, 10);
    
    for (int i = 0; i < entries.size(); i++) {
      BlogWowEntry entry = entries.get(i);
      UIBranchContainer rssitem = UIBranchContainer.make(tofill, "item:", i+"");
      UIOutput.make(rssitem, "item-title", entry.getTitle());
      UIOutput.make(rssitem, "creator", entry.getOwnerId());
      
      String desc = "<![CDATA[" 
        + ( entry.getText().length() < 200 
          ? entry.getText().substring(0, entry.getText().length())
          : entry.getText().substring(0, 200)) 
        + "]]>";
      String content = "<![CDATA[" + entry.getText() + "]]>";
      UIVerbatim.make(rssitem, "description", desc);
      UIVerbatim.make(rssitem, "content" ,content);
    }
  }

  public ViewParameters getViewParameters() {
    return new BlogParams();
  }

  public String getContentType() {
    return ContentTypeInfoRegistry.RSS_2;
  }

}
