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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.util;

import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.ui.workbench.container.Refreshable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.BeanItemContainer;

/**
 * Container capable of keeping objects of type {@link SiteMapEntry} and resolving their hierarchy.
 */
public class SiteMapEntryContainer extends BeanItemContainer<SiteMapEntry> implements Container.Hierarchical, Refreshable {

    private static Logger log = LoggerFactory.getLogger(SiteMapEntryContainer.class);

    private int maxLevel = Integer.MIN_VALUE;

    private int minLevel = Integer.MAX_VALUE;

    private String siteMapPath;

    private boolean virtualUris;

    private SiteMapService siteMapService;

    public SiteMapEntryContainer(String siteMapPath, boolean isVirtualUris, SiteMapService siteMapService) {
        super(SiteMapEntry.class);
        this.siteMapPath = siteMapPath;
        virtualUris = isVirtualUris;
        this.siteMapService = siteMapService;
        refresh();
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        SiteMapEntry entry = (SiteMapEntry)itemId;

        int maxLevel = entry.getLevel() + 1;
        List<SiteMapEntry> children = new ArrayList<SiteMapEntry>();
        int index = indexOfId(entry) + 1;
        while (index < size()) {
            SiteMapEntry childEntry = getIdByIndex(index++);
            if (childEntry.getLevel() > maxLevel) {
                continue;
            }
            if (childEntry.getLevel() < maxLevel) {
                break;
            }
            children.add(childEntry);
        }
        return children;
    }

    @Override
    public Object getParent(Object itemId) {
        SiteMapEntry parent = null;
        SiteMapEntry child = (SiteMapEntry)itemId;
        int index = indexOfId(itemId);
        while (index > 0 && parent == null) {
            SiteMapEntry id = getIdByIndex(--index);
            if (id.getLevel() < child.getLevel()) {
                parent = id;
            }
        }
        return parent;
    }

    @Override
    public Collection<?> rootItemIds() {
        List<SiteMapEntry> roots = new ArrayList<SiteMapEntry>();
        for (SiteMapEntry entry : getItemIds()) {
            if (entry.getLevel() == minLevel) {
                roots.add(entry);
            }
        }
        return roots;
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return getChildren(itemId).size() > 0;
    }

    @Override
    public boolean isRoot(Object itemId) {
        return ((SiteMapEntry)itemId).getLevel() == minLevel;
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return ((SiteMapEntry)itemId).getLevel() < maxLevel;
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    private void calculateBoundLevels() {
        Collection<SiteMapEntry> ids = getItemIds();
        for (SiteMapEntry id : ids) {
            int level = id.getLevel();
            maxLevel = Math.max(maxLevel, level);
            minLevel = Math.min(minLevel, level);
        }
    }

    protected List<SiteMapEntry> fetchSiteMapEntries() {
        List<SiteMapEntry> entries = new ArrayList<SiteMapEntry>();
        try {
            Node sitemapNode = SessionUtil.getNode(GoogleSiteMapConfiguration.WORKSPACE, siteMapPath);
            if (sitemapNode != null) {
                entries.addAll(siteMapService.getSiteMapBeans(sitemapNode, virtualUris, true));
            }
        } catch (RepositoryException e) {
            log.error("Failed to accumulate site map beans for pages", e);
        }
        return entries;
    }

    @Override
    public void refresh() {
        removeAllItems();
        addAll(fetchSiteMapEntries());
        calculateBoundLevels();
    }
}
