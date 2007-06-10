package org.sakaiproject.blogwow.tool.otp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;

import uk.org.ponder.beanutil.BeanLocator;

public class BlogLocator implements BeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static String NEW_1 = NEW_PREFIX + "1";

	private BlogLogic blogLogic;
    private ExternalLogic externalLogic;

	private Map<String, BlogWowBlog> delivered = new HashMap<String, BlogWowBlog>();

	public Object locateBean(String name) {
        BlogWowBlog togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new blog object at the current location with the current user
                togo = new BlogWowBlog(externalLogic.getCurrentLocationId(), externalLogic.getCurrentLocationId(), null);
            } else {
                togo = blogLogic.getBlogById(new Long(name));
            }
            delivered.put(name, togo);
        }
        return togo;
	}

	public String saveAll() {
        for (Iterator it = delivered.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            BlogWowBlog blog = delivered.get(key);
            if (key.startsWith(NEW_PREFIX)) {
                // could do stuff here
            }
            blogLogic.saveBlog(blog, blog.getLocation());
        }
		return "saved";
	}


	public void setBlogLogic(BlogLogic blogLogic) {
		this.blogLogic = blogLogic;
	}

}
