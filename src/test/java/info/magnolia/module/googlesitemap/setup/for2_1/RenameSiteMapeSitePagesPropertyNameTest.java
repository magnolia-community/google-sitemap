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

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContextImpl;
import info.magnolia.module.ModuleRegistryImpl;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes.GoogleSiteMap;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.hamcrest.NodeMatchers;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Main test class for {@link RenameSiteMapeSitePagesPropertyName}.
 */
public class RenameSiteMapeSitePagesPropertyNameTest extends RepositoryTestCase {

    private Session session;
    private InstallContextImpl installContext;
    private Node pageNode;
    private RenameSiteMapeSitePagesPropertyName task;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        Node root = session.getRootNode();
        pageNode = root.addNode("page", NodeTypes.Page.NAME);

        final ModuleRegistryImpl moduleRegistry = new ModuleRegistryImpl();
        installContext = new InstallContextImpl(moduleRegistry);

        task = new RenameSiteMapeSitePagesPropertyName("taskName", "taskDescription", RepositoryConstants.WEBSITE);

    }


    @Test
    public void testSitePagePropertyUpdate() throws RepositoryException, TaskExecutionException {
        // GIVEN
        pageNode.setProperty("googleSitemapChangefreq", "googleSitemapChangefreq");
        pageNode.setProperty("googleSitemapHide", "googleSitemapHide");
        pageNode.setProperty("googleSitemapPriority", "googleSitemapPriority");
        pageNode.setProperty("googleSitemapHideChildren", "googleSitemapHideChildren");
        pageNode.setProperty("tutu", "tutu");
        session.save();

        // WHEN
        task.execute(installContext);

        // THEN
        pageNode = session.getNode("/page");
        assertThat(pageNode, NodeMatchers.hasProperty(GoogleSiteMap.SITEMAP_CHANGEFREQ, "googleSitemapChangefreq"));
        assertThat(pageNode, NodeMatchers.hasProperty(GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP, "googleSitemapHide"));
        assertThat(pageNode, NodeMatchers.hasProperty(GoogleSiteMap.SITEMAP_PRIORITY, "googleSitemapPriority"));
        assertThat(pageNode, NodeMatchers.hasProperty(GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN, "googleSitemapHideChildren"));
        assertThat(pageNode, NodeMatchers.hasProperty("tutu", "tutu"));
    }

    @Test
    public void testNoUpdate() throws RepositoryException, TaskExecutionException {
        // GIVEN
        Node contentNode = session.getRootNode().addNode("contentNode", NodeTypes.ContentNode.NAME);
        contentNode.setProperty("googleSitemapChangefreq", "googleSitemapChangefreq");
        contentNode.setProperty("tutu", "tutu");
        session.save();

        // WHEN
        task.execute(installContext);

        // THEN
        contentNode = session.getNode("/contentNode");
        assertThat(contentNode, NodeMatchers.hasProperty("googleSitemapChangefreq", "googleSitemapChangefreq"));
        assertThat(contentNode, NodeMatchers.hasProperty("tutu", "tutu"));
    }
}
