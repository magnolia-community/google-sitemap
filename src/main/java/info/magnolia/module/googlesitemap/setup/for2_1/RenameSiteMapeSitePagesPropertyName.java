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

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes.GoogleSiteMap;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang3.StringUtils;

/**
 * Rename siteMap properties stored under pages nodes.
 */
public class RenameSiteMapeSitePagesPropertyName extends AbstractRepositoryTask {

    private Session siteSession;
    private final String workspace;
    private final List<String> propertiesToRename = Arrays.asList("googleSitemapChangefreq", "googleSitemapHide", "googleSitemapPriority", "googleSitemapHideChildren");

    public RenameSiteMapeSitePagesPropertyName(String name, String description, String workspace) {
        super(name, description);
        this.workspace = StringUtils.isNotBlank(workspace) ? workspace : RepositoryConstants.WEBSITE;
    }

    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException {
        try {
            siteSession = ctx.getJCRSession(workspace);
            for (String toRename : propertiesToRename) {
                NodeIterator nodesToHandle = search(toRename);
                while (nodesToHandle.hasNext()) {
                    renameProperties(nodesToHandle.nextNode());
                }
            }

        } catch (RepositoryException re) {
            ctx.error("Could not update the siteMap site properties ", re);
        }
    }

    private NodeIterator search(String toRename) throws RepositoryException {
        final String statement = "SELECT * FROM [" + NodeTypes.Page.NAME + "] AS t WHERE  t." + toRename + " is not null";
        QueryManager manager = siteSession.getWorkspace().getQueryManager();
        Query query = manager.createQuery(statement, Query.JCR_SQL2);
        return NodeUtil.filterDuplicates(query.execute().getNodes());
    }

    private void renameProperties(Node page) throws RepositoryException {
        if (page.hasProperty("googleSitemapChangefreq")) {
            PropertyUtil.renameProperty(page.getProperty("googleSitemapChangefreq"), GoogleSiteMap.SITEMAP_CHANGEFREQ);
        }
        if (page.hasProperty("googleSitemapHide")) {
            PropertyUtil.renameProperty(page.getProperty("googleSitemapHide"), GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP);
        }
        if (page.hasProperty("googleSitemapPriority")) {
            PropertyUtil.renameProperty(page.getProperty("googleSitemapPriority"), GoogleSiteMap.SITEMAP_PRIORITY);
        }
        if (page.hasProperty("googleSitemapHideChildren")) {
            PropertyUtil.renameProperty(page.getProperty("googleSitemapHideChildren"), GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN);
        }
    }

}
