/******************************************************************************
 * BlogWowDaoImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.sakaiproject.genericdao.hibernate.HibernateCompleteGenericDao;

/**
 * Implementations of any specialized DAO methods from the specialized DAO 
 * that allows the developer to extend the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public class BlogWowDaoImpl 
	extends HibernateCompleteGenericDao 
		implements BlogWowDao {

	private static Log log = LogFactory.getLog(BlogWowDaoImpl.class);

	public void init() {
		log.debug("init");
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.dao.BlogWowDao#getLocationsForBlogsIds(java.lang.Long[])
	 */
	public List getLocationsForBlogsIds(Long[] blogIds) {
		String hql = "select distinct blog.location from BlogWowBlog blog where blog.id in " + arrayToInString(blogIds) + " order by blog.id";
		return getHibernateTemplate().find(hql);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.dao.BlogWowDao#getBlogEntries(java.lang.Long[], java.lang.String, java.lang.String[], java.lang.String, boolean, int, int)
	 */
	public List getBlogEntries(Long[] blogIds, String userId, String[] locations, String sortProperty, boolean ascending, int start, int limit) {
		/*
		 * rules to determine which entries to get
		 * 1) entry.id is in blogIds AND 
		 * 2) (entry.privacy is public OR 
		 * 3) entry.blog.owner is userId OR 
		 * 4) entry.owner is userId OR
		 * 5) (entry.privacy is group AND entry.blog.location is in new location array)
		 */

		DetachedCriteria dc = DetachedCriteria.forClass(BlogWowEntry.class)
			.add( Property.forName("blog.id").in(blogIds));

		String hql = "from BlogWowEntry entry where entry.blog.id in " + arrayToInString(blogIds) + 
			" and (entry.privacySetting = '"+BlogConstants.PRIVACY_PUBLIC+"'";
		if (userId != null) {
			hql += " or entry.blog.ownerId = '"+userId+"' or entry.ownerId = '"+userId+"'";
		}
		if (locations != null && locations.length > 0) {
			hql += " or (entry.privacySetting = '"+BlogConstants.PRIVACY_GROUP+"' and " +
					"entry.blog.location is in " + arrayToInString(locations) + ")";
		}
		hql += ")";
		if (sortProperty != null && ! sortProperty.equals("")) {
			if (ascending) {
				hql += " order by " + sortProperty + " asc";
			} else {
				hql += " order by " + sortProperty + " desc";
			}
		}
		Query query = getSession().createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		return query.list();
	}

	
	/**
	 * Turn an array into a string like "('item1','item2','item3')"
	 * @param array
	 * @return
	 */
	private String arrayToInString(Object[] array) {
		String arrayString = "('";
		for (int i = 0; i < array.length; i++) {
			if (i > 0)
				arrayString += "','" + array[i];
			else
				arrayString += array[i];
		}
		arrayString += "')";
		return arrayString;
	}

}
