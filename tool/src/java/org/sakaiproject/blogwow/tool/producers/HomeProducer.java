package org.sakaiproject.blogwow.tool.producers;

import java.util.Date;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class HomeProducer implements
  ViewComponentProducer,
  DefaultView
{
  public static final String VIEWID = "home";
  
  public NavBarRenderer navBarRenderer;
  
  public String getViewID() {
    return VIEWID;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);
    
    UIInternalLink.make(tofill, "my-blog-link", UIMessage.make("blogwow.homepage.userbloglink"), new SimpleViewParameters(BlogViewProducer.VIEWID));
  
    UIMessage.make(tofill, "last-blogged-date", "blogwow.homepage.userlastblogged", new Object[] {new Date().toLocaleString()});
  
    UIMessage.make(tofill, "all-blogs-header", "blogwow.homepage.listofblogs");
    
    UIInternalLink.make(tofill, "all-blog-rss", UIMessage.make("blogwow.homepage.RSStext"), new SimpleViewParameters(BlogRSSProducer.VIEWID));
  }

}