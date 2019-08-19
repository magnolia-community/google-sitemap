/**
 * This file Copyright (c) 2014-2018 Magnolia International
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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentconnector;

import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.util.SiteMapEntryContainer;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;

/**
 * {@link ContentConnector} implementation capable of handling {@link SiteMapEntry} objects.
 */
public class SitemapContentConnector implements ContentConnector {

    public static final String SEPARATOR = "@";

    private final DetailLocation siteMapLocation;

    private SiteMapService siteMapService;

    private SiteMapEntryContainer container;

    private SitemapContentConnectorDefinition definition;

    @Inject
    public SitemapContentConnector(SitemapContentConnectorDefinition definition, SiteMapService siteMapService, SubAppContext subAppContext) {
        this.definition = definition;
        this.siteMapLocation = DetailLocation.wrap(subAppContext.getLocation());
        this.siteMapService = siteMapService;
        this.container = new SiteMapEntryContainer(getSitemapPath(), this.definition.isVirtualUris(), this.siteMapService);
    }

    @Override
    public String getItemUrlFragment(Object itemId) {
        if (itemId instanceof SiteMapEntry) {
            SiteMapEntry entry = (SiteMapEntry)itemId;
            return getSitemapPath() + SEPARATOR + entry.getPath();
        }
        return getSitemapPath();
    }

    @Override
    public SiteMapEntry getItemIdByUrlFragment(String urlFragment) {
        if (urlFragment.startsWith(siteMapLocation.getNodePath())) {
            return findEntry(parsePageName(urlFragment));
        }
        return null;
    }

    @Override
    public Object getDefaultItemId() {
        return new Object();
    }

    @Override
    public Item getItem(Object itemId) {
        if (itemId instanceof SiteMapEntry) {
            return new BeanItem<SiteMapEntry>((SiteMapEntry) itemId);
        }
        return null;
    }

    @Override
    public SiteMapEntry getItemId(Item item) {
        if (item instanceof BeanItem) {
            Object bean = ((BeanItem)item).getBean();
            if (bean instanceof SiteMapEntry) {
                return (SiteMapEntry)bean;
            }
        }
        return null;
    }

    @Override
    public boolean canHandleItem(Object itemId) {
        return itemId instanceof SiteMapEntry;
    }

    private SiteMapEntry findEntry(String pagePath) {
        if (!StringUtils.isBlank(pagePath)) {
            SiteMapEntry id = container.firstItemId();
            while (id != null) {
                if (id.getPath().equals(pagePath)) {
                    return id;
                }
                id = container.nextItemId(id);
            }
        }
        return null;
    }

    public String getSitemapPath() {
        return StringUtils.substringBefore(siteMapLocation.getNodePath(), SEPARATOR);
    }

    public SiteMapEntryContainer getContainer() {
        return container;
    }

    private String parsePageName(String urlFragment) {
        return StringUtils.substringAfterLast(urlFragment, SEPARATOR);
    }
}
