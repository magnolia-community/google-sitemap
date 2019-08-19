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
package info.magnolia.module.googlesitemap;

import info.magnolia.jcr.util.NodeTypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 * Constants and convenience methods for node types introduced by Google Site Map module.
 */
public class SiteMapNodeTypes {
    /**
     * Represents the node type mgnl:siteMap.
     */
    public static class SiteMap {
        public static final String NAME = NodeTypes.MGNL_PREFIX + "siteMap";
        public static final String TYPE = NodeTypes.MGNL_PREFIX + "googleSiteMapType";
        public static final String DISPLAY_NAME = NodeTypes.MGNL_PREFIX + "googleSiteMapDisplayName";
        public static final String INCLUDE_VIRTUAL_URI = NodeTypes.MGNL_PREFIX + "googleSiteMapIncludeVirtualUri";
        public static final String URL = NodeTypes.MGNL_PREFIX + "googleSiteMapURL";
        public static final String PAGES = NodeTypes.MGNL_PREFIX + "googleSiteMapPages";
        public static final String DEFAULT_CHANGEFREQ = NodeTypes.MGNL_PREFIX + "googleSiteMapDefaultChangeFreq";
        public static final String DEFAULT_PRIORITY = NodeTypes.MGNL_PREFIX + "googleSiteMapDefaultPriority";

        /**
         * Returns the type of the site map or null if no type has been stored on the node.
         */
        public static String getType(Node node) throws RepositoryException {
            return node.hasProperty(TYPE) ? node.getProperty(TYPE).getString() : null;
        }

        /**
         * Returns the displayName of the site map or null if no displayName has been stored on the node.
         */
        public static String getDisplayName(Node node) throws RepositoryException {
            return node.hasProperty(DISPLAY_NAME) ? node.getProperty(DISPLAY_NAME).getString() : null;
        }

        /**
         * Returns the value of {@link #INCLUDE_VIRTUAL_URI} stored or false in the property is empty.
         */
        public static boolean isVirtualUriMappingIncluded(Node node) throws RepositoryException {
            return node.hasProperty(INCLUDE_VIRTUAL_URI) ? node.getProperty(INCLUDE_VIRTUAL_URI).getBoolean() : Boolean.FALSE;
        }

        /**
         * Returns the URL of the site map or null if no URL has been stored on the node.
         */
        public static String getUrl(Node node) throws RepositoryException {
            return node.hasProperty(URL) ? node.getProperty(URL).getString() : null;
        }

        /**
         * Returns the change frequency of the site map page or null if no change frequency has been stored on the node.
         */
        public static String getDefaultChangeFreq(Node node) throws RepositoryException {
            return node.hasProperty(DEFAULT_CHANGEFREQ) ? node.getProperty(DEFAULT_CHANGEFREQ).getString() : null;
        }

        /**
         * Returns the priority of the site map page or null if no priority has been stored on the node.
         */
        public static Double getDefaultPriority(Node node) throws RepositoryException {
            return node.hasProperty(DEFAULT_PRIORITY) ? node.getProperty(DEFAULT_PRIORITY).getDouble() : null;
        }

        /**
         * Returns the pages linked to the site map or null if no pages has been stored on the node.
         */
        public static List<String> getPages(Node node) throws RepositoryException {
            if (!node.hasProperty(PAGES)) {
                return new ArrayList<String>();
            }

            Value[] values = node.getProperty(PAGES).getValues();
            List<String> res = new LinkedList<String>();
            for (Value value : values) {
                res.add(value.getString());
            }
            return res;
        }

        public static void update(Node node, String type, String url, String displayName, boolean includeVirtualUri, String defaultChangeFrq, Double defaultPriority, List<String> pages) throws RepositoryException {
            NodeTypes.checkNodeType(node, NAME, TYPE, URL, DISPLAY_NAME, INCLUDE_VIRTUAL_URI, PAGES);
            node.setProperty(TYPE, type);
            node.setProperty(URL, url);
            node.setProperty(DISPLAY_NAME, displayName);
            node.setProperty(INCLUDE_VIRTUAL_URI, includeVirtualUri);
            node.setProperty(DEFAULT_CHANGEFREQ, defaultChangeFrq);
            node.setProperty(DEFAULT_PRIORITY, defaultPriority);
            if (pages != null) {
                node.setProperty(PAGES, pages.toArray(new String[pages.size()]));
            }
        }

    }

    /**
     * Represents the mixIn node type mgnl:googleSiteMap.
     */
    public static class GoogleSiteMap {
        public static final String NAME = NodeTypes.MGNL_PREFIX + "googleSiteMap";
        public static final String SITEMAP_CHANGEFREQ = NodeTypes.MGNL_PREFIX + "googleSiteMapChangeFreq";
        public static final String SITEMAP_PRIORITY = NodeTypes.MGNL_PREFIX + "googleSiteMapPriority";
        public static final String SITEMAP_HIDE_IN_GOOGLESITEMAP = NodeTypes.MGNL_PREFIX + "googleSiteMapHide";
        public static final String SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN = NodeTypes.MGNL_PREFIX + "googleSiteMapHideChildren";

        /**
         * Returns the change frequency of the site map page or null if no change frequency has been stored on the node.
         */
        public static String getChangeFreq(Node node) throws RepositoryException {
            return node.hasProperty(SITEMAP_CHANGEFREQ) ? node.getProperty(SITEMAP_CHANGEFREQ).getString() : null;
        }

        /**
         * Returns the priority of the site map page or null if no priority has been stored on the node.
         */
        public static Double getPriority(Node node) throws RepositoryException {
            return node.hasProperty(SITEMAP_PRIORITY) ? node.getProperty(SITEMAP_PRIORITY).getDouble() : null;
        }

        /**
         * Returns the hide value of the site map page or false if no value is stored on the node.
         */
        public static boolean isHide(Node node) throws RepositoryException {
            return node.hasProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP) ? node.getProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP).getBoolean() : Boolean.FALSE;
        }

        /**
         * Returns the hide children value of the site map page or false if no value is stored on the node.
         */
        public static boolean isHideChildren(Node node) throws RepositoryException {
            return node.hasProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN) ? node.getProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN).getBoolean() : Boolean.FALSE;
        }

        public static void update(Node node, String changeFrq, Double priority, boolean hide, boolean hideChildren) throws RepositoryException {
            NodeTypes.checkNodeType(node, NAME, SITEMAP_CHANGEFREQ, SITEMAP_PRIORITY, SITEMAP_HIDE_IN_GOOGLESITEMAP, SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN);
            node.setProperty(SITEMAP_CHANGEFREQ, changeFrq);
            node.setProperty(SITEMAP_PRIORITY, priority);
            node.setProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP, hide);
            node.setProperty(SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN, hideChildren);
        }
    }
}
