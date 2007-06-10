package org.sakaiproject.blogwow.tool.producers;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.beans.MugshotGenerator;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class HomeProducer implements ViewComponentProducer, DefaultView {

    public static final String VIEW_ID = "home";
    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private Locale locale;
    private MugshotGenerator mugshotGenerator;
    private ExternalLogic externalLogic;


    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        String currentUserId = externalLogic.getCurrentUserId();
        String locationId = externalLogic.getCurrentLocationId();

        UIMessage.make(tofill, "page-title", "blogwow.homepage.title");

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        
        UIMessage.make(tofill, "blogger", "blogwow.homepage.nameheader");
        UIMessage.make(tofill, "entries", "blogwow.homepage.entryheader");
        UIMessage.make(tofill, "last-updated", "blogwow.homepage.lastupdated");

        BlogWowBlog myblog = blogLogic.getBlogByLocationAndUser(locationId, currentUserId );
        UIInternalLink.make(tofill, "my-blog-link", 
                UIMessage.make("blogwow.homepage.userbloglink"), 
                new SimpleBlogParams(BlogViewProducer.VIEW_ID, myblog.getId().toString()));

        // use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        
        List<BlogWowEntry> myentries = entryLogic.getAllVisibleEntries(myblog.getId(), currentUserId, null, true, 0, 1);
        if (myentries.size() > 0) {
            UIMessage.make(tofill, "last-blogged-date", "blogwow.homepage.userlastblogged", 
                    new Object[] { df.format( myentries.get(0).getDateModified() ) });
        }

        UIMessage.make(tofill, "all-blogs-header", "blogwow.homepage.listofblogs");

        // This needs more work due to URL-encoding of path segments
//        UILink.make(tofill, "all-blog-rss", UIMessage.make("blogwow.homepage.RSStext"), 
//                externalLogic.getBlogLocationRssUrl(locationId));

        List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 0);
        UIBranchContainer blogsTable = UIBranchContainer.make(tofill, "blog-list-table:");

        for (int i = 0; i < blogs.size(); i++) {
            UIBranchContainer row = UIBranchContainer.make(blogsTable, "row:", i+"");
            BlogWowBlog blog = blogs.get(i);
            UILink.make(row, "user-icon", mugshotGenerator.getMugshotUrl(blog.getOwnerId()));
            UIInternalLink.make(row, "blog-title-link", blog.getTitle(), 
                    new SimpleBlogParams(BlogViewProducer.VIEW_ID, blog.getId().toString()));
            List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, true, 0, 1000);
            UIOutput.make(row, "number-of-entries", entries.size()+"");
            if (entries.size() > 0) {
                UIOutput.make(row, "time-last-updated", df.format(entries.get(0).getDateModified()) );
            }
            else {
                // TODO - why is this here? -AZ
                UIOutput.make(row, "time-last-updated", "");
            }
            // UIInternalLink.make(row, "rss-link", new BlogParams(BlogRSSProducer.VIEWID, blog.getId().toString()));
            UILink.make(row, "rss-link", externalLogic.getBlogRssUrl(blog.getId()));
        }

    }


    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMugshotGenerator(MugshotGenerator mugshotGenerator) {
        this.mugshotGenerator = mugshotGenerator;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}