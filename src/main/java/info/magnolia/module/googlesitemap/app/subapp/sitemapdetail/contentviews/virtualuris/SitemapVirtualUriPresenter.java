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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.virtualuris;

import info.magnolia.event.EventBus;
import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentconnector.SitemapContentConnector;
import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.SitemapTableColumnDefinition;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.list.ListPresenter;
import info.magnolia.ui.workbench.list.ListView;

import java.util.Iterator;

import javax.inject.Inject;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Table;

/**
 * Presents the sitemap virtual URI entries view.
 * @see SitemapListView
 */
public class SitemapVirtualUriPresenter extends ListPresenter {

    private SitemapContentConnector contentConnector;

    @Inject
    public SitemapVirtualUriPresenter(SitemapListView view, ComponentProvider componentProvider, ContentConnector contentConnector) {
        super(view, componentProvider);
        this.contentConnector = (SitemapContentConnector) contentConnector;
    }

    @Override
    public ListView start(WorkbenchDefinition workbenchDefinition, EventBus eventBus, String viewTypeName, ContentConnector contentConnector) {
        SitemapListView view = (SitemapListView) super.start(workbenchDefinition, eventBus, viewTypeName, contentConnector);
        Iterator<ColumnDefinition> it = getColumnsIterator();
        while (it.hasNext()) {
            ColumnDefinition columnDefinition = it.next();
            if (columnDefinition instanceof SitemapTableColumnDefinition) {
                view.setColumnAlignment(columnDefinition.getName(), Table.Align.valueOf(((SitemapTableColumnDefinition)columnDefinition).getAlignment()));
            }
        }
        return view;
    }

    @Override
    protected Container initializeContainer() {
        return contentConnector.getContainer();
    }
}
