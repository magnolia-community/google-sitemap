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
package info.magnolia.module.googlesitemap.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;
import info.magnolia.module.googlesitemap.service.query.QueryUtil;
import info.magnolia.module.site.ConfiguredSite;
import info.magnolia.module.site.SiteManager;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;

/**
 * Util test class used for initialization of a service.
 */
public class ServiceTestUtil extends SiteMapTestUtil {

    protected Session websiteSession;
    protected Session configSession;
    protected SiteMapService service;
    protected Node websiteNode;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        websiteSession = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        // Register SiteMap
        File file = new File(getClass().getResource("/googleSitemaps.xml").getFile());
        InputStream inputStream = new FileInputStream(file);
        DataTransporter.importXmlStream(inputStream, GoogleSiteMapConfiguration.WORKSPACE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        // Register Pages
        file = new File(getClass().getResource("/website.demo-site.xml").getFile());
        inputStream = new FileInputStream(file);
        DataTransporter.importXmlStream(inputStream, RepositoryConstants.WEBSITE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);
        websiteNode = websiteSession.getNode("/demo-site");

        // Register Config
        // Initialize config tree
        configSession = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        NodeUtil.createPath(configSession.getRootNode(), "/modules/adminInterface", NodeTypes.ContentNode.NAME);
        file = new File(getClass().getResource("/config.modules.adminInterface.virtualURIMapping.xml").getFile());
        inputStream = new FileInputStream(file);
        DataTransporter.importXmlStream(inputStream, RepositoryConstants.CONFIG, "/modules/adminInterface", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        IOUtils.closeQuietly(inputStream);
        // Initial check
        assertTrue(googleSiteMapSession.nodeExists("/test1"));
        assertTrue(googleSiteMapSession.nodeExists("/test2"));
        assertTrue(websiteSession.nodeExists("/demo-site"));

        // Init service
        initService();
    }

    protected void initService() throws RepositoryException {
        // Init Site
        ConfiguredSite site = new ConfiguredSite();
        site.setEnabled(true);
        SiteManager siteManager = mock(SiteManager.class);
        when(siteManager.getAssignedSite((Node) any())).thenReturn(site);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
        ServerConfiguration.getInstance().setDefaultBaseUrl("defaultBaseUrl");
        service = new SiteMapService(siteManager, new GoogleSiteMapConfiguration(), new QueryUtil());
    }
}
