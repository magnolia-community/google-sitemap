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
package info.magnolia.module.googlesitemap.setup.for2_1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContextImpl;
import info.magnolia.module.ModuleRegistryImpl;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes.SiteMap;
import info.magnolia.test.hamcrest.NodeMatchers;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.MockUtil;
import info.magnolia.test.mock.jcr.MockSession;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Main test class for {@link UpdatePropertyNamesAndNodeStructure}.
 */
public class UpdatePropertyNamesAndNodeStructureTest {

    private Node googleSite;
    private InstallContextImpl installContext;
    private UpdatePropertyNamesAndNodeStructure task;
    private MockSession session;

    @Before
    public void setUp() throws Exception {
        MockUtil.initMockContext();
        session = new MockSession(GoogleSiteMapConfiguration.WORKSPACE);
        Node root = session.getRootNode();
        googleSite = root.addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        ((MockContext) MgnlContext.getInstance()).addSession(GoogleSiteMapConfiguration.WORKSPACE, session);
        ((MockContext) MgnlContext.getSystemContext()).addSession(GoogleSiteMapConfiguration.WORKSPACE, session);

        final ModuleRegistryImpl moduleRegistry = new ModuleRegistryImpl();
        installContext = new InstallContextImpl(moduleRegistry);

        task = new UpdatePropertyNamesAndNodeStructure("taskName", "taskDescription");

    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testSitePagePropertyMigration() throws RepositoryException, TaskExecutionException {
        // GIVEN
        googleSite.setProperty("displayName", "displayName");
        googleSite.setProperty("type", "type");
        googleSite.setProperty("url", "url");
        googleSite.setProperty("includeVirtualURIMappings", "includeVirtualURIMappings");
        googleSite.setProperty(SiteMap.DEFAULT_PRIORITY, 0.6d);
        Node sites = googleSite.addNode("sites", NodeTypes.ContentNode.NAME);
        sites.setProperty("0", "/site1");
        sites.setProperty("1", "/site2");

        // WHEN
        task.execute(installContext);

        // THEN
        googleSite = session.getNode("siteMap");
        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.DISPLAY_NAME, "displayName"));
        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.TYPE, "type"));
        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.URL, "url"));
        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.INCLUDE_VIRTUAL_URI, "includeVirtualURIMappings"));

        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.DEFAULT_CHANGEFREQ, GoogleSiteMapConfiguration.DEFAULT_CHANGE_FREQUENCY));
        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.DEFAULT_PRIORITY, 0.6d));

        assertThat(googleSite, NodeMatchers.hasProperty(SiteMap.PAGES));
        assertThat(googleSite.getProperty(SiteMap.PAGES).getValues()[0].getString(), is("/site1"));
        assertThat(googleSite.getProperty(SiteMap.PAGES).getValues()[1].getString(), is("/site2"));
    }

}
