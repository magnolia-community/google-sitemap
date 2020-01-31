/**
 * This file Copyright (c) 2012-2018 Magnolia International
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

import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.link.LinkUtil;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.query.QueryUtil;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.commons.iterator.FilteringNodeIterator;
import org.apache.jackrabbit.commons.predicate.NodeTypePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main SiteMapService.
 * This service is responsible for:
 * Searching the appropriate nodes (pages or VirtualUri) that should be displayed (for XML rendering or Editing).
 * From the search nodes, create SiteMapEntrys (POJO containing preformated infos used for the rendering).
 */
@Singleton
public class SiteMapService {

    private static final Logger log = LoggerFactory.getLogger(SiteMapService.class);

    /**
     * Injected service.
     */
    private SiteManager siteManager;
    private I18nContentSupport i18nSupport;
    private GoogleSiteMapConfiguration configuration;
    private QueryUtil queryUtil;

    /**
     * Constructor for injection.
     *
     * @param siteManager: Injected.
     */
    @Inject
    public SiteMapService(SiteManager siteManager, GoogleSiteMapConfiguration configuration, QueryUtil queryUtil) {
        this.siteManager = siteManager;
        this.configuration = configuration;
        this.queryUtil = queryUtil;
        i18nSupport = Components.getComponent(I18nContentSupport.class);
    }

    /**
     * Create the SiteMapEntry List corresponding to <br>
     * - uriMapping if isForVirtualUri = true or<br>
     * - for all child's of the rootNode that are of type MgnlNodeType.NT_CONTENT otherwise.
     */
    public List<SiteMapEntry> getSiteMapBeans(Node siteMapNode, boolean isForVirtualUri, boolean isForEdit) throws RepositoryException {
        // Init
        List<SiteMapEntry> res = new ArrayList<SiteMapEntry>();
        final String changefreq = SiteMapNodeTypes.SiteMap.getDefaultChangeFreq(siteMapNode) != null ? SiteMapNodeTypes.SiteMap.getDefaultChangeFreq(siteMapNode) : configuration.getChangeFrequency();
        final Double priority = SiteMapNodeTypes.SiteMap.getDefaultPriority(siteMapNode) != null ? SiteMapNodeTypes.SiteMap.getDefaultPriority(siteMapNode) : configuration.getPriority();
        Session webSiteSession = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);

        NodeIterator nodeIterator = null;
        if (isForVirtualUri) {
            log.debug("Requested virtualUri info's for EDIT='{}", isForEdit);
            // Create a virtualUri info beans
            nodeIterator = searchVirtualUriNodes();
            feedVirtualUriMapBeans(res, nodeIterator, isForEdit, changefreq, priority);
        } else {
            log.debug("Requested siteMap info's for EDIT='{}' based on the following root {} ", isForEdit, siteMapNode.getPath());
            // Create a site info beans

            List<String> pages = SiteMapNodeTypes.SiteMap.getPages(siteMapNode);
            for (String id : pages) {

                if (!webSiteSession.nodeExists(id)) {
                    log.debug("Page '{}' is not visible. This page will not be added to the result list", id);
                    continue;
                }
                Node page = webSiteSession.getNode(id);
                nodeIterator = searchSiteChildNodes(page);
                feedSiteMapBeans(res, nodeIterator, isForEdit, null, changefreq, priority, page.getDepth());
            }
        }

        return res;
    }

    /**
     * Search the nodes related to the UriMappings.
     */
    private NodeIterator searchVirtualUriNodes() {
        NodeIterator nodes = null;

        String xpath = "//virtualURIMapping//element(*," + NodeTypes.ContentNode.NAME + ")";
        nodes = queryUtil.query(RepositoryConstants.CONFIG, xpath, "xpath", NodeTypes.ContentNode.NAME);

        return nodes;
    }

    /**
     * Extract informations form the Node and create a Bean used in the display
     * for Edit and XML rendering.
     */
    private void feedVirtualUriMapBeans(List<SiteMapEntry> siteMapBeans, NodeIterator nodes, boolean isForEdit, String changefreq, Double priority) throws RepositoryException {

        while (nodes.hasNext()) {
            // Init
            Node child = nodes.nextNode();
            if (hasToBePopulated(child, true, isForEdit, null)) {
                // Populate the Bean used by the display
                SiteMapEntry siteMapBean = populateBean(child, true, isForEdit, null, null, changefreq, priority, 0);
                // Add the bean to the result
                siteMapBeans.add(siteMapBean);
            }
        }
    }

    /**
     * Search children of the root Node of type MgnlNodeType.NT_CONTENT and all inherited node types.
     *
     * @throws RepositoryException
     */
    private NodeIterator searchSiteChildNodes(Node root) throws RepositoryException {
        NodeIterator nodes = null;

        NodeTypePredicate predicate = new NodeTypePredicate(NodeTypes.Page.NAME, true);
        nodes = new FilteringNodeIterator(root.getNodes(), predicate);

        return nodes;
    }

    /**
     * Create a new SiteMapEntry POJO for all node from the NodeIterator if required.
     */
    private void feedSiteMapBeans(List<SiteMapEntry> siteMapBeans, NodeIterator nodes, boolean isForEdit, Boolean inheritedParentDisplayColor, String changefreq, Double priority, int rootLevel) throws RepositoryException {

        while (nodes.hasNext()) {
            // Init
            Node page = nodes.nextNode();
            Site site = siteManager.getAssignedSite(page);
            if (!hasToBePopulated(page, false, isForEdit, site)) {
                log.debug("Page '' is not part of the site map.", NodeUtil.getNodePathIfPossible(page));
                continue;
            }

            // Populate the Bean
            SiteMapEntry siteMapBean = populateBean(page, false, isForEdit, site, inheritedParentDisplayColor, changefreq, priority, rootLevel);
            NodeIterator childNodes = searchSiteChildNodes(page);
            boolean hasChild = (childNodes != null && childNodes.hasNext());

            if (isForEdit) {
                // Handle Edit Mode. Always all the bean.
                siteMapBeans.add(siteMapBean);

                if (hasChild) {
                    // Handle the children if any
                    Boolean hideNodeChildInGoogleSitemapTmp = null;
                    // Handle the color of the child to be display.
                    if (inheritedParentDisplayColor != null) {
                        hideNodeChildInGoogleSitemapTmp = inheritedParentDisplayColor;
                    } else if (SiteMapNodeTypes.GoogleSiteMap.isHideChildren(page)) {
                        hideNodeChildInGoogleSitemapTmp = Boolean.TRUE;
                    }

                    feedSiteMapBeans(siteMapBeans, childNodes, isForEdit, hideNodeChildInGoogleSitemapTmp, changefreq, priority, rootLevel);
                }

            } else {
                // Handle XML Display Mode
                // If node has to be display.
                if (!SiteMapNodeTypes.GoogleSiteMap.isHide(page)) {
                    // Check Multilang.
                    if (i18nSupport.isEnabled() && site.getI18n().isEnabled()) {
                        Locale currentLocale = i18nSupport.getLocale();
                        for (Locale locale : site.getI18n().getLocales()) {
                            i18nSupport.setLocale(locale);
                            SiteMapEntry siteMapBeanLocale = populateBean(page, false, isForEdit, site, null, changefreq, priority, rootLevel);
                            if (siteMapBeanLocale != null) {
                                siteMapBeans.add(siteMapBeanLocale);
                            }
                        }
                        i18nSupport.setLocale(currentLocale);
                    } else {
                        siteMapBeans.add(siteMapBean);
                    }
                }
                // Check if Child Node has to be included
                if (hasChild && !SiteMapNodeTypes.GoogleSiteMap.isHideChildren(page)) {
                    // Call recursively
                    feedSiteMapBeans(siteMapBeans, childNodes, isForEdit, null, changefreq, priority, rootLevel);
                }
            }
        }
    }

    /**
     * Populate the Bean.
     */
    private SiteMapEntry populateBean(Node child, boolean isForUriMapping, boolean isForEdit, Site site, Boolean inheritedParentDisplayColor, String changefreq, Double priority, int rootLevel) throws RepositoryException {
        SiteMapEntry res = null;
        // Populate only if true
        if (hasToBePopulated(child, isForUriMapping, isForEdit, site)) {

            String loc = null;

            // Get loc
            if (isForUriMapping) {
                loc = child.hasProperty("fromURI") ? MgnlContext.getContextPath() + child.getProperty("fromURI").getString() : "";
            } else {
                loc = LinkUtil.createExternalLink(child);
            }

            // Populate the bean:
            res = new SiteMapEntry(configuration, loc, child, rootLevel, changefreq, priority);
            log.debug("Populate Basic info for Node: " + child.getPath() + " with values " + res.toStringDisplay());

            if (isForEdit) {
                res = populateBeanForEdit(child, isForUriMapping, res, inheritedParentDisplayColor);
            }
        }

        return res;
    }

    /**
     * Add additional info's for Edit.
     */
    private SiteMapEntry populateBeanForEdit(Node child, boolean isForUriMapping, SiteMapEntry siteMapBean, Boolean inheritedParentDisplayColor) throws RepositoryException {

        if (isForUriMapping) {
            // For VirtualUri
            siteMapBean.setFrom(child.hasProperty("fromURI") ? child.getProperty("fromURI").getString() : "");
            siteMapBean.setTo(child.hasProperty("toURI") ? child.getProperty("toURI").getString() : "");
            siteMapBean.setStyleAlert(SiteMapNodeTypes.GoogleSiteMap.isHide(child));
            log.debug("Populate Edit VirtualUri info for Node: " + child.getPath() + " with values " + siteMapBean.toStringVirtualUri());
        } else {
            // For Site
            siteMapBean.setPageName(child.hasProperty("title") ? child.getProperty("title").getString() : "");
            siteMapBean.setStyleAlert(inheritedParentDisplayColor != null ? inheritedParentDisplayColor.booleanValue() : SiteMapNodeTypes.GoogleSiteMap.isHide(child));
            log.debug("Populate Edit Site info for Node: " + child.getPath() + " with values " + siteMapBean.toStringSite());
        }
        return siteMapBean;
    }

    /**
     * Check if the node has to be populated.<br>
     * True for UriMapping if:<br>
     * !hideInGoogleSitemap && node has a modification date<br>
     * True for Site if:<br>
     * !hideInGoogleSitemap && !hideInGoogleSitemapChildren && node has a creation date && site.isEnabled()<br>
     */
    private boolean hasToBePopulated(Node child, boolean isForUriMapping, boolean isForEdit, Site site) throws RepositoryException {
        boolean hasCreationDate = NodeTypes.Created.getCreated(child) != null;
        boolean hasModificationDate = NodeTypes.LastModified.getLastModified(child) != null;

        // For edit and if has creation date --> true.
        if (isForEdit) {
            return hasCreationDate;
        }

        // Node has a property set to hide node to be display --> false.
        if (SiteMapNodeTypes.GoogleSiteMap.isHide(child) && (isForUriMapping || SiteMapNodeTypes.GoogleSiteMap.isHideChildren(child))) {
            return false;
        }
        // For uriMapping.
        if (isForUriMapping && hasModificationDate) {
            return true;
        }
        // For site.
        if (!isForUriMapping && site.isEnabled() && hasCreationDate) {
            return true;
        }
        return false;
    }

    /**
     * Get Site and virtualUri informations if they are defined as components.
     * Notice that by using a HashSet, a single object will be keeped in case of equality of the location.
     */
    public Iterator<SiteMapEntry> getSiteMapBeans(Node siteMapNode) throws RepositoryException {
        Set<SiteMapEntry> res = new HashSet<SiteMapEntry>();
        // Return if no content set.
        if (!NodeUtil.isNodeType(siteMapNode, SiteMapNodeTypes.SiteMap.NAME)) {
            return res.iterator();
        }
        // Display Site informations part... if defined
        res.addAll(getSiteMapBeans(siteMapNode, false, false));
        if (SiteMapNodeTypes.SiteMap.isVirtualUriMappingIncluded(siteMapNode)) {
            res.addAll(getSiteMapBeans(siteMapNode, true, false));
        }
        return res.iterator();
    }

    /**
     * Based on a {@link SiteMapEntry} bean, update the related page property. <br>
     * Update the page last modification date in order to visualize that the changes has to be published <br>
     * in order to be visible on the public instance.<br>
     */
    public Node updatePageNode(SiteMapEntry entry) throws RepositoryException {
        Node pageNode = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).getNode(entry.getPath());
        return updateNode(entry, pageNode);
    }

    public Node updateVirtualUriNode(SiteMapEntry entry) throws RepositoryException {
        Node virtualUriNode = MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getNode(entry.getPath());
        return updateNode(entry, virtualUriNode);
    }

    private Node updateNode(SiteMapEntry entry, Node node) throws RepositoryException {
        SiteMapNodeTypes.GoogleSiteMap.update(node, entry.getChangefreq(), entry.getPriority(), entry.isHide(), entry.isHideChildren());
        NodeTypes.LastModified.update(node);
        return node;
    }
}
