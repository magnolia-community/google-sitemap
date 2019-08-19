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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail;

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentconnector.SitemapContentConnector;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.api.availability.AvailabilityChecker;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.contentapp.ContentSubAppView;
import info.magnolia.ui.contentapp.browser.BrowserPresenter;
import info.magnolia.ui.contentapp.browser.BrowserSubApp;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Google Sitemap module specific extension of {@link BrowserSubApp}, manages sub-app caption based on
 * the selected sitemap.
 */
public class SitemapEntryBrowserSubApp extends BrowserSubApp {

    private static Logger log = LoggerFactory.getLogger(SitemapEntryBrowserSubApp.class);

    private SitemapContentConnector contentConnector;

    private String sitemapName;

    @Inject
    public SitemapEntryBrowserSubApp(ActionExecutor actionExecutor, SubAppContext subAppContext, ContentSubAppView view, BrowserPresenter browser, @Named(SubAppEventBus.NAME) EventBus subAppEventBus, @Named(AdmincentralEventBus.NAME) EventBus adminCentralEventBus, ContentConnector contentConnector, AvailabilityChecker checker) {
        super(actionExecutor, subAppContext, view, browser, subAppEventBus, adminCentralEventBus, contentConnector, checker);
        this.contentConnector = (SitemapContentConnector) contentConnector;
    }

    @Override
    public ContentSubAppView start(Location location) {
        ContentSubAppView view = super.start(location);
        try {
            final Node sitemapNode = JcrUtils.getNodeIfExists(contentConnector.getSitemapPath(), MgnlContext.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE));
            if (sitemapNode != null) {
                sitemapName = sitemapNode.getName();
            }
        } catch (RepositoryException e) {
            log.error("Failed to obtain Google Sitemap workspace: [{}]", e.getMessage(), e);
        }
        return view;
    }

    @Override
    public String getCaption() {
        SitemapEntryBrowserSubAppDescriptor descriptor = (SitemapEntryBrowserSubAppDescriptor) getSubAppContext().getSubAppDescriptor();
        return String.format("%s: %s", descriptor.getCaptionPrefix().toUpperCase(), sitemapName);
    }
}
