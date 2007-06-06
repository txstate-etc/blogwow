package org.sakaiproject.blogwow.tool.otp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;

import uk.org.ponder.beanutil.BeanLocator;

public class BlogLocator implements BeanLocator {
	private BlogLogic blogLogic;

	private Map<String, BlogWowBlog> blogsmap = new HashMap<String, BlogWowBlog>();

	public Object locateBean(String name) {
		BlogWowBlog togo = blogsmap.get(name);
		if (togo == null) {
			BlogWowBlog blog = blogLogic.getBlogById(new Long(name));
			blog.setProfile("");
			togo = blog;
			blogsmap.put(name, togo);
		}
		return togo;
	}

	public String saveAll() {
		Collection blogs = blogsmap.values();
		for(Iterator i = blogs.iterator(); i.hasNext();) {
			BlogWowBlog blog = (BlogWowBlog) i.next();
			blogLogic.saveBlog(blog);
		}
		return "saved";
	}

	public String cancelAll() {
		blogsmap.clear();
		return "canceled";
	}

	public void setBlogLogic(BlogLogic blogLogic) {
		this.blogLogic = blogLogic;
	}

}
