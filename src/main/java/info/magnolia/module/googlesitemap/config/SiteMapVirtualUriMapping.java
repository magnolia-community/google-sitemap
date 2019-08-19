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
package info.magnolia.module.googlesitemap.config;

import info.magnolia.cms.beans.config.RegexpVirtualURIMapping;
import info.magnolia.cms.util.ObservationUtil;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Virtual URI mapping that compares source URI to names of sitemaps available in google-sitemap workspace
 * and prepends the prefix.
 *
 * @deprecated since 2.5, SiteMapVirtualUriMapping has been relocated to the {@link info.magnolia.module.googlesitemap.config.mapping.SiteMapVirtualUriMapping}.
 * Because virtual URI mappings have been relocated to the virtual-uri module (see MAGNOLIA-3349, MAGNOLIA-7016).
 */
@Deprecated
public class SiteMapVirtualUriMapping extends RegexpVirtualURIMapping {

    public static final String XML_EXTENSION = ".xml";

    private Logger log = LoggerFactory.getLogger(getClass());

    private SystemContext context;

    private Map<String, String> siteMapUrlMappings = new HashMap<String, String>();

    private String prefix;

    private AbstractPredicate<Node> siteMapNodePredicate  = new AbstractPredicate<Node>() {
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
        ObservationUtil.registerChangeListener(GoogleSiteMapConfiguration.WORKSPACE, "/", true, new EventListener() {
            @Override
            public void onEvent(EventIterator events) {
                reloadSiteMapNames();
            }
        });
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
                    siteMapUrlMappings.put(SiteMapNodeTypes.SiteMap.getUrl(node), node.getPath() + XML_EXTENSION);
                }
            }
        } catch (RepositoryException e) {
            log.warn("Failed to collect sitemap names: ", e.getMessage());
        }
    }

    @Override
    public MappingResult mapURI(String uri) {
        if (siteMapUrlMappings.containsKey(uri)) {
            MappingResult result = new MappingResult();
            result.setLevel(uri.length());
            result.setToURI(prefix + siteMapUrlMappings.get(uri));
            return result;
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotEmpty(prefix);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
