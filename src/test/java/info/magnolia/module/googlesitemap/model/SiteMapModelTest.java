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
package info.magnolia.module.googlesitemap.model;

import static org.junit.Assert.*;

import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.ServiceTestUtil;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.module.googlesitemap.service.SiteMapXMLUtilImpl;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;

import java.util.Collections;
import java.util.List;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Main test class for {@link SiteMapModel}.
 */
public class SiteMapModelTest extends ServiceTestUtil {

    private SiteMapModel<RenderableDefinition> model;
    private SiteMapXMLUtilImpl xmlUtil;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        xmlUtil = new SiteMapXMLUtilImpl(new Provider<SiteMapService>() {
            @Override
            public SiteMapService get() {
                return service;
            }
        });
    }

    private void initModel(Node contentNode) {
        RenderingModel<RenderableDefinition> parent = null;
        RenderableDefinition definition = null;
        model = new SiteMapModel<RenderableDefinition>(contentNode, definition, parent, service, xmlUtil);
    }

    @Test
    public void getSiteMapBeans() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        initModel(siteMapNode);

        // WHEN
        List<SiteMapEntry> res = IteratorUtils.toList(model.getSiteMapBeans());
        Collections.sort(res);

        // THEN
        assertNotNull(res);
        assertEquals(4, res.size());
        SiteMapEntry entry0 = res.get(0);
        assertEquals("/modules/adminInterface/virtualURIMapping/messages", entry0.getPath());

        SiteMapEntry entry1 = res.get(1);
        assertEquals("/modules/adminInterface/virtualURIMapping/dialogsFileThumbnail", entry1.getPath());

        SiteMapEntry entry2 = res.get(2);
        assertEquals("defaultBaseUrl/demo-site/article/articleSection", entry2.getLoc());

        SiteMapEntry entry3 = res.get(3);
        assertEquals("defaultBaseUrl/demo-site/article/article1", entry3.getLoc());
    }

    @Test
    public void getXmlBeans() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test3");
        initModel(siteMapNode);

        // WHEN
        String res = model.getXML();

        // THEN
        assertNotNull(res);
        assertTrue("Contains a url def for Pages", StringUtils.contains(res, "<url><loc>defaultBaseUrl/demo-site/news/subNewsSection2/news2</loc><lastmod>2014-01-08</lastmod><changefreq>hourly</changefreq><priority>0.7</priority><mobile:mobile/></url>"));

    }
}
