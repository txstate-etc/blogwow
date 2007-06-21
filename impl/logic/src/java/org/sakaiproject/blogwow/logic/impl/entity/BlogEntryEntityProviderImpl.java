/******************************************************************************
 * BlogEntryEntityProviderImpl.java - created by aaronz on Jun 21, 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.impl.entity;

import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.entity.BlogEntryEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;

/**
 * 
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class BlogEntryEntityProviderImpl implements BlogEntryEntityProvider, CoreEntityProvider, AutoRegisterEntityProvider {

    private EntryLogic entryLogic;
    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    public boolean entityExists(String id) {
        // entity is real if there are any entries that match this id
        String entryId = id;
        if (entryLogic.entryExists(entryId)) {
            return true;
        }
        return false;
    }

}
