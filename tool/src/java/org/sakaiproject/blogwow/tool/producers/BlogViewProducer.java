package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.params.BlogParams;

import uk.org.ponder.rsf.components.ParameterList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogViewProducer implements 
ViewComponentProducer, 
ViewParamsReporter,
NavigationCaseReporter
{
  public static final String VIEWID = "blog_view";
  
  public NavBarRenderer navBarRenderer;
  public BlogLogic blogLogic;
  public EntryLogic entryLogic;
  public String userid;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    BlogParams params = (BlogParams) viewparams;
    
    navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);
    
    BlogWowBlog blog = blogLogic.getBlogById(new Long(params.blogid));
    
    UIOutput.make(tofill, "blog-title", blog.getTitle());
    UIVerbatim.make(tofill, "profile-verbatim-text", blog.getProfile());
    
    List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(blog.getId(), userid, null, false, 0, 10);
    if (entries.size() <= 0) {
      UIOutput.make(tofill, "blog-entry:empty");
      return;
    }
    
    for (int i = 0; i < entries.size(); i++) {
      UIBranchContainer entrydiv = UIBranchContainer.make(tofill, "blog-entry:", i+"");
      BlogWowEntry entry = entries.get(i);
      UIOutput.make(entrydiv, "blog-title", entry.getTitle());
      UIOutput.make(entrydiv, "blog-date", entry.getDateCreated().toLocaleString());
      UIVerbatim.make(entrydiv, "verbatim-blog-text", entry.getText());
      
      UIOutput.make(entrydiv, "action-items");
      if (entryLogic.canWriteEntry(entry.getId(), userid)) {
        UIInternalLink.make(entrydiv, "edit-entry-link", UIMessage.make("blogwow.blogview.edit"),
            new BlogParams(AddEntryProducer.VIEWID, entry.getId().toString()));
        
        UIForm removeform = UIForm.make(entrydiv, "remove-entry-form");
        
        UICommand removeCommand = UICommand.make(removeform, "remove-command", "", "RemoveBlogEntryCmd.execute");
        removeCommand.parameters = new ParameterList(new UIELBinding("RemoveBlogEntryCmd.entryId", entry.getId().toString()));
        
        UILink removelink = UILink.make(entrydiv, "remove-entry-link", "");
        Map attr = new HashMap();
        attr.put("onclick", "document.getElementById('"+removeCommand.getFullID()+"').click();return false;");
        removelink.decorators = new DecoratorList(new UIFreeAttributeDecorator(attr));
      }
    }
  }

  public ViewParameters getViewParameters() {
    return new BlogParams();
  }

  public List reportNavigationCases() {
    List l = new ArrayList();
    l.add(new NavigationCase(null,new SimpleViewParameters(HomeProducer.VIEWID)));
    return l;
  }

  
}