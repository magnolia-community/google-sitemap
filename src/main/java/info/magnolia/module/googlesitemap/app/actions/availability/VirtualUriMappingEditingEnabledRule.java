/**
 * This file Copyright (c) 2013-2018 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.googlesitemap.app.actions.availability;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.ui.api.availability.AvailabilityRule;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemId;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemUtil;

import java.util.Collection;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Disables an action when the sitemap does not include virtual URI mappings.
 */
public class VirtualUriMappingEditingEnabledRule implements AvailabilityRule {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isAvailable(Collection<?> itemIds) {
        try {
            if (itemIds != null && itemIds.size() > 0) {
                Object firstId = itemIds.iterator().next();
                if (firstId instanceof JcrItemId) {
                    JcrItemId items = (JcrItemId)firstId;
                    Item item = JcrItemUtil.getJcrItem(items);
                    if (item != null && item.isNode() && NodeUtil.isNodeType((Node) item, SiteMapNodeTypes.SiteMap.NAME)) {
                        return (SiteMapNodeTypes.SiteMap.isVirtualUriMappingIncluded((Node) item));
                    }
                }
            }
        } catch (RepositoryException e) {
            log.debug("Error occurred while checking virtual uri mapping availability check ", e.getMessage());
            //IGNORE
        }
        return false;
    }

}
