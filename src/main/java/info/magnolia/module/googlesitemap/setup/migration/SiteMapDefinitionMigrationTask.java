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
package info.magnolia.module.googlesitemap.setup.migration;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.JCRMgnlPropertiesFilteringNodeWrapper;
import info.magnolia.jcr.wrapper.JCRPropertiesFilteringNodeWrapper;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.config.SiteMapType;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Google site map dedicated migration task.<br>
 * Implemented logic: <br>
 * - Search all siteMaps definition from the specified workspace.<br>
 * - For every siteMapDefinition <br>
 * -- Create a folders representing the siteMap definition path (demo-project/about/siteMapDefinitoin --> demo-project/about) <br>
 * -- Create a new M5 siteMap definition based on the M4.5 one.<br>
 * -- Remove the M4.5 siteMap definition.<br>
 */
public class SiteMapDefinitionMigrationTask extends AbstractRepositoryTask {

    private final Logger log = LoggerFactory.getLogger(SiteMapDefinitionMigrationTask.class);

    private final String sourceWorkspace;
    private final String searchRootPath;
    private final String templateName = "google-sitemap:pages/siteMapsConfiguration";
    private final String siteDefinitionTemplateName = "google-sitemap:components/content/siteComponent";
    private final String virtualUriTemplateName = "google-sitemap:components/content/virtualUriComponent";
    private final String sites = "sites";
    private Session siteMapSession;


    public SiteMapDefinitionMigrationTask(String name, String description, String sourceWorkspace, String searchRootPath) {
        super(name, description);
        this.sourceWorkspace = (StringUtils.isNotBlank(sourceWorkspace)) ? sourceWorkspace : RepositoryConstants.WEBSITE;
        this.searchRootPath = (StringUtils.isNotBlank(searchRootPath)) ? searchRootPath : "/";
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        // Init
        siteMapSession = installContext.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE);
        try {
            // Perform the query
            NodeIterator nodeIterator = getQueryResult();
            // Handle result
            while (nodeIterator.hasNext()) {
                handleSimeMapDefinitionMigration(nodeIterator.nextNode());
            }

        } catch (RepositoryException re) {
            installContext.error("Unable to perform Migration task " + getName(), re);
            throw new TaskExecutionException(re.getMessage());
        }
    }

    private void handleSimeMapDefinitionMigration(Node siteMapNodeDefinition) throws RepositoryException {
        // Create path in the siteMap workspace
        Node targetRootSiteMapFolder = getOrCreateRootSiteMapDefinitionNode(siteMapNodeDefinition);

        // Create an equivalent M5 siteMap definition
        Node targetSiteMapDefinition = migrateSiteMapDefinition(siteMapNodeDefinition, targetRootSiteMapFolder);
        log.info("New M5 siteMapDefinition created {} based on the previous definition {} ", targetSiteMapDefinition.getPath(), siteMapNodeDefinition.getPath());

        // Remove the original siteMapDefinition
        siteMapNodeDefinition.remove();
    }

    /**
     * Create a M5 siteMapDefinition based on a M4.5 definition.<br>
     * Steps: <br>
     * - Create a folder tree to the sitMapDefinition node if required<br>
     * - Create a new siteMapDefinition nodeTye <br>
     * - Create a new sites contentNode child
     * - Copy all site definition from M4.5 into the newly created child node.
     */
    private Node migrateSiteMapDefinition(Node siteMapNodeDefinition, Node targetRootSiteMapFolder) throws RepositoryException {
        // Create the new M5 definition
        Node targetSiteMapDefinition = targetRootSiteMapFolder.addNode(siteMapNodeDefinition.getName(), SiteMapNodeTypes.SiteMap.NAME);
        // Copy all root properties
        copyOrUpdateProperty(siteMapNodeDefinition, targetSiteMapDefinition);

        // Copy all properties from M4.5 definition into 5.2 format
        populateSiteMapDefinition(targetSiteMapDefinition, siteMapNodeDefinition);

        return targetSiteMapDefinition;
    }

    private void populateSiteMapDefinition(Node targetSiteMapDefinition, Node sourceSiteMapNodeDefinition) throws RepositoryException {
        List<String> siteMapDefinitionList = new ArrayList<String>();
        boolean insertVirtualUri = false;
        Iterable<Node> children = NodeUtil.collectAllChildren(sourceSiteMapNodeDefinition);
        Iterator<Node> childrenIterator = children.iterator();
        // Iterate the child nodes and extract the configuration
        while (childrenIterator.hasNext()) {
            Node child = childrenIterator.next();
            if (StringUtils.isNotBlank(NodeTypes.Renderable.getTemplate(child))) {
                String templateName = NodeTypes.Renderable.getTemplate(child);
                if (virtualUriTemplateName.equals(templateName)) {
                    insertVirtualUri = true;
                } else if (siteDefinitionTemplateName.equals(templateName)) {
                    siteMapDefinitionList.addAll(extractSiteMapDefinition(child));
                }
            }
        }

        // Set includeVirtualURIMappings property
        targetSiteMapDefinition.setProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI, insertVirtualUri);
        // Set siteMaps definition
        if (!siteMapDefinitionList.isEmpty()) {
            Node sites = targetSiteMapDefinition.addNode("sites", NodeTypes.ContentNode.NAME);
            int pos = 0;
            for (String definition : siteMapDefinitionList) {
                sites.setProperty("" + pos, definition);
                pos += 1;
            }
        }
    }

    private List<String> extractSiteMapDefinition(Node parent) throws RepositoryException {
        List<String> siteDefinitions = new ArrayList<String>();
        if (parent.hasNode(sites)) {
            Node filteredNode = new JCRMgnlPropertiesFilteringNodeWrapper(parent.getNode(sites));
            PropertyIterator iterator = filteredNode.getProperties();
            while (iterator.hasNext()) {
                Property property = iterator.nextProperty();
                siteDefinitions.add(property.getString());
            }
        } else {
            log.info("Node '{}' do not have a 'sites' child node.", parent.getPath());
        }
        return siteDefinitions;
    }


    /**
     * Copy all non jcr: properties from the source to the target node.
     */
    private void copyOrUpdateProperty(Node source, Node target) throws RepositoryException {
        Node filteredSource = new JCRPropertiesFilteringNodeWrapper(source);
        PropertyIterator iterator = filteredSource.getProperties();
        while (iterator.hasNext()) {
            Property property = iterator.nextProperty();
            if (target.hasProperty(property.getName())) {
                target.getProperty(property.getName()).setValue(property.getValue());
            } else {
                target.setProperty(property.getName(), property.getValue());
            }
        }
        // Set Url
        target.setProperty(SiteMapNodeTypes.SiteMap.URL, source.getPath());
        // Set default type
        target.setProperty(SiteMapNodeTypes.SiteMap.TYPE, SiteMapType.Standard.name());
        // Set display name
        target.setProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, source.getName());
    }

    /**
     * Create the site map definition parent folder.
     */
    Node getOrCreateRootSiteMapDefinitionNode(Node siteMapNodeDefinition) throws RepositoryException {
        // If the definition is at the root level, return the root node.
        if (siteMapNodeDefinition.getDepth() <= 1) {
            return siteMapSession.getRootNode();
        }
        // If the path already exist return the referenced node..
        String targetPath = siteMapNodeDefinition.getParent().getPath();
        if (siteMapSession.nodeExists(targetPath)) {
            return siteMapSession.getNode(targetPath);
        }
        // If the path do not already exist, create it.
        return NodeUtil.createPath(siteMapSession.getRootNode(), targetPath, NodeTypes.Folder.NAME, false);
    }

    /**
     * Search all node within 'sourceWorkspace' that are under the 'searchRootPath' containing a template definition equal to 'templateName'.
     */
    private NodeIterator getQueryResult() throws RepositoryException {
        NodeIterator nodeIterator = null;
        String query = "SELECT * FROM [nt:base] AS t WHERE (ISSAMENODE(t, '" + searchRootPath + "') OR ISDESCENDANTNODE(t, '" + searchRootPath + "')) " +
                "AND t.[mgnl:template] is not null AND contains(t.[" + NodeTypes.Renderable.TEMPLATE + "],'" + templateName + "')";
        nodeIterator = QueryUtil.search(sourceWorkspace, query, Query.JCR_SQL2);
        log.info("{} google site map definitions will be handled", nodeIterator.getSize());
        return nodeIterator;
    }
}
