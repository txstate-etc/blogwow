package org.sakaiproject.blogwow.tool.producers;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.beans.MugshotGenerator;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.site.api.Site;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
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
    public BlogLogic blogLogic;
    public EntryLogic entryLogic;
    public Site site;
    public String userid;
    public Locale locale;
    public MugshotGenerator mugshotGenerator;

    public String getViewID() {
        return VIEWID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        //use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEWID);

        BlogWowBlog myblog = blogLogic.getBlogByLocationAndUser(site.getReference(), userid );
        UIInternalLink.make(tofill, "my-blog-link", UIMessage.make("blogwow.homepage.userbloglink"), new BlogParams(BlogViewProducer.VIEWID, myblog.getId().toString()));

        List<BlogWowEntry> myentries = entryLogic.getAllVisibleEntries(myblog.getId(), userid, null, true, 0, 1);
        if (myentries.size() > 0)  
            UIMessage.make(tofill, "last-blogged-date", "blogwow.homepage.userlastblogged", new Object[] { df.format( myentries.get(0).getDateModified() ) }); // TODO - real date in here

        UIMessage.make(tofill, "all-blogs-header", "blogwow.homepage.listofblogs");

        UIInternalLink.make(tofill, "all-blog-rss", UIMessage.make("blogwow.homepage.RSStext"), new SimpleViewParameters(BlogRSSProducer.VIEWID));

        List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(site.getReference(), null, true, 0, 0);
        UIBranchContainer blogsTable = UIBranchContainer.make(tofill, "blog-list-table:");

        for (int i = 0; i < blogs.size(); i++) {
            UIBranchContainer row = UIBranchContainer.make(blogsTable, "row:", i+"");
            UILink.make(row, "user-icon", mugshotGenerator.getMugshotUrl());
            BlogWowBlog blog = blogs.get(i);
            UIInternalLink.make(row, "blog-title-link", blog.getTitle(), new BlogParams(BlogViewProducer.VIEWID, blog.getId().toString()));
            List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(blog.getId(), userid, null, true, 0, 1000);
            UIOutput.make(row, "number-of-entries", entries.size()+"");
            if (entries.size() > 0) {
                UIOutput.make(row, "time-last-updated", df.format(entries.get(0).getDateModified()) );
            }
            else {
                // TODO - why is this here? -AZ
                UIOutput.make(row, "time-last-updated", "");
            }
            UIInternalLink.make(row, "rss-link", new BlogParams(BlogRSSProducer.VIEWID, blog.getId().toString()));
        }

    }

}