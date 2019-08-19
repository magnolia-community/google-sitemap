/**
 * This file Copyright (c) 2017-2018 Magnolia International
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
package info.magnolia.module.googlesitemap.config.mapping;

import info.magnolia.context.SystemContext;
import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.observation.WorkspaceEventListenerRegistration;
import info.magnolia.virtualuri.VirtualUriMapping;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Virtual URI mapping that compares source URI to names of sitemaps available in google-sitemap workspace
 * and prepends the prefix.
 */
public class SiteMapVirtualUriMapping implements VirtualUriMapping {

    public static final String XML_EXTENSION = ".xml";

    private static final Logger log = LoggerFactory.getLogger(SiteMapVirtualUriMapping.class);

    private final SystemContext context;

    private final Map<URI, String> siteMapUrlMappings = new HashMap();

    private String prefix;

    private AbstractPredicate<Node> siteMapNodePredicate = new AbstractPredicate<Node>() {
        @Override
        public boolean evaluateTyped(Node node) {
            try {
                return NodeUtil.isNodeType(node, SiteMapNodeTypes.SiteMap.NAME);
            } catch (RepositoryException e) {
                return false;
            }
        }
    };

    @Inject
    public SiteMapVirtualUriMapping(SystemContext context) {
        this.context = context;

        try {
            WorkspaceEventListenerRegistration.observe(GoogleSiteMapConfiguration.WORKSPACE, "/", events -> reloadSiteMapNames())
                    .withSubNodes(true).register();
        } catch (RepositoryException e) {
            log.error("Cannot register workspace listener.", e);
        }
        reloadSiteMapNames();
    }

    private void reloadSiteMapNames() {
        try {
            siteMapUrlMappings.clear();
            Node root = context.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE).getRootNode();
            final Iterator<Node> it = NodeUtil.collectAllChildren(root, siteMapNodePredicate).iterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (SiteMapNodeTypes.SiteMap.getUrl(node) != null) {
                    URI uri = new URI(SiteMapNodeTypes.SiteMap.getUrl(node));
                    siteMapUrlMappings.put(uri, node.getPath() + XML_EXTENSION);
                }
            }
        } catch (RepositoryException | URISyntaxException e) {
            log.warn("Failed to collect sitemap names: ", e.getMessage());
        }
    }

    @Override
    public Optional<Result> mapUri(URI uri) {
        String mapping = siteMapUrlMappings.get(uri);
        return mapping == null ? Optional.empty() : Optional.of(new Result(prefix + mapping, uri.getPath().length(), this));
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotEmpty(prefix);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
