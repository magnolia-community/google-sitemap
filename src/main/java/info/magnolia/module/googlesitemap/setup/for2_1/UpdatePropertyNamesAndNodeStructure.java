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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.NodeVisitor;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.JCRMgnlPropertiesFilteringNodeWrapper;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes.SiteMap;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rename the {@link SiteMap} properties (from displayName to mgnl:googleSiteMapDisplayName,...) and <br>
 * change the node structure (used to store the related SiteMap pages):<br>
 * - from a 'sites' sub node
 * - to a {@link SiteMap#PAGES} JCR multi value property.
 */
public class UpdatePropertyNamesAndNodeStructure extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(UpdatePropertyNamesAndNodeStructure.class);
    private Session siteMapSession;

    public UpdatePropertyNamesAndNodeStructure(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    @Override
    public void execute(InstallContext ctx) throws TaskExecutionException {
        try {
            siteMapSession = ctx.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE);
            NodeUtil.visit(siteMapSession.getRootNode(), new SiteMapPropertyVisitor());
        } catch (RepositoryException re) {
            ctx.error("Could not update the site map properties ", re);
        }

    }

    /**
     * Rename properties, and change the node structure.
     */
    private class SiteMapPropertyVisitor implements NodeVisitor {

        @Override
        public void visit(Node siteMap) throws RepositoryException {
            if (!StringUtils.equals(siteMap.getPrimaryNodeType().getName(), SiteMap.NAME)) {
                return;
            }
            // rename properties
            renameProperties(siteMap);
            // create new default properties for default ChangeFreq and Priority
            createDefaultProperties(siteMap);
            // transform sub node 'sites' to a JCR multi value property
            if (siteMap.hasNode("sites")) {
                transformSitesNodeIntoMultiValueProperty(siteMap);
            }
        }
    }

    /**
     * Rename property: <br>
     * - displayName to mgnl:googleSiteMapDisplayName<br>
     * - type to mgnl:googleSiteMapType<br>
     * - url to mgnl:googleSiteMapURL<br>
     * - includeVirtualURIMappings to mgnl:googleSiteMapIncludeVirtualUri.
     */
    private void renameProperties(Node siteMapNode) throws RepositoryException {
        if (siteMapNode.hasProperty("displayName")) {
            PropertyUtil.renameProperty(siteMapNode.getProperty("displayName"), SiteMap.DISPLAY_NAME);
        }
        if (siteMapNode.hasProperty("type")) {
            PropertyUtil.renameProperty(siteMapNode.getProperty("type"), SiteMap.TYPE);
        }
        if (siteMapNode.hasProperty("url")) {
            PropertyUtil.renameProperty(siteMapNode.getProperty("url"), SiteMap.URL);
        }
        if (siteMapNode.hasProperty("includeVirtualURIMappings")) {
            PropertyUtil.renameProperty(siteMapNode.getProperty("includeVirtualURIMappings"), SiteMap.INCLUDE_VIRTUAL_URI);
        }
    }

    /**
     * Create : <br>
     * - mgnl:googleSiteMapDefaultChangeFreq based on the default values<br>
     * - mgnl:googleSiteMapDefaultPriority based on the default values.
     */
    private void createDefaultProperties(Node siteMapNode) throws RepositoryException {
        if (!siteMapNode.hasProperty(SiteMap.DEFAULT_CHANGEFREQ)) {
            siteMapNode.setProperty(SiteMap.DEFAULT_CHANGEFREQ, GoogleSiteMapConfiguration.DEFAULT_CHANGE_FREQUENCY);
        }
        if (!siteMapNode.hasProperty(SiteMap.DEFAULT_PRIORITY)) {
            siteMapNode.setProperty(SiteMap.DEFAULT_PRIORITY, GoogleSiteMapConfiguration.DEFAULT_PRIORITY);
        }
    }

    private void transformSitesNodeIntoMultiValueProperty(Node siteMapNode) throws RepositoryException {
        List<String> siteValues = new ArrayList<String>();
        // Get all site properties of the sites sub node.
        Node sitesNode = new JCRMgnlPropertiesFilteringNodeWrapper(siteMapNode.getNode("sites"));
        PropertyIterator iterator = sitesNode.getProperties();
        while (iterator.hasNext()) {
            siteValues.add(iterator.nextProperty().getString());
        }
        if (!siteValues.isEmpty()) {
            siteMapNode.setProperty(SiteMap.PAGES, siteValues.toArray(new String[siteValues.size()]));
        }
        log.info("Remove sub node '{}' containing the pages information from '{}' ", sitesNode.getPath(), siteMapNode.getPath());
        sitesNode.remove();
    }

}
