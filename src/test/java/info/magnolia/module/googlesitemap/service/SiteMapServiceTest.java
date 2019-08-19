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
package info.magnolia.module.googlesitemap.service;

import static org.junit.Assert.*;

import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;

import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Test;

/**
 * Main test class for {@link SiteMapService}.
 */
public class SiteMapServiceTest extends ServiceTestUtil {

    @Test
    public void testGetSiteMapBeansForPagesNotForEdit() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        // WHEN
        List<SiteMapEntry> res = service.getSiteMapBeans(siteMapNode, false, false);

        // THEN
        assertNotNull(res);
        // check amount of res
        assertEquals(2, res.size());

        SiteMapEntry entry0 = res.get(0);
        assertEquals("always", entry0.getChangefreq());
        assertEquals("2014-01-08", entry0.getLastmod());
        assertEquals("defaultBaseUrl/demo-site/article/article1", entry0.getLoc());
        assertEquals("/demo-site/article/article1", entry0.getPath());
        assertEquals(0.3d, entry0.getPriority(), 0);
        assertEquals(1, entry0.getLevel());

        SiteMapEntry entry1 = res.get(1);
        assertEquals("defaultBaseUrl/demo-site/article/articleSection", entry1.getLoc());
        assertEquals("/demo-site/article/articleSection", entry1.getPath());

    }

    @Test
    public void testGetSiteMapBeansForPagesForEdit() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        // WHEN
        List<SiteMapEntry> res = service.getSiteMapBeans(siteMapNode, false, true);

        // THEN
        assertNotNull(res);
        assertEquals(4, res.size());

        SiteMapEntry entry0 = res.get(0);
        assertEquals("always", entry0.getChangefreq());
        assertEquals("2014-01-08", entry0.getLastmod());
        assertEquals("defaultBaseUrl/demo-site/article/article1", entry0.getLoc());
        assertEquals("/demo-site/article/article1", entry0.getPath());
        assertEquals(0.3d, entry0.getPriority(), 0);
        assertEquals(1, entry0.getLevel());
        assertFalse(entry0.isStyleAlert());

        SiteMapEntry entry1 = res.get(1); // Second article is hidden
        assertEquals("defaultBaseUrl/demo-site/article/article2", entry1.getLoc());
        assertTrue(entry1.isStyleAlert());
        assertEquals(0.8d, entry1.getPriority(), 0);
        assertEquals(1, entry1.getLevel());

        SiteMapEntry entry2 = res.get(2);
        assertEquals("/demo-site/article/articleSection", entry2.getPath());
        assertFalse(entry2.isStyleAlert());
        assertEquals(1, entry2.getLevel());

        SiteMapEntry entry3 = res.get(3); // Section is required to hide child
        assertEquals("defaultBaseUrl/demo-site/article/articleSection/article3", entry3.getLoc());
        assertTrue(entry3.isStyleAlert());
        assertEquals(2, entry3.getLevel());
    }

    @Test
    public void testGetSiteMapBeansForUirNotForEdit() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        // WHEN
        List<SiteMapEntry> res = service.getSiteMapBeans(siteMapNode, true, false);

        // THEN
        assertNotNull(res);
        assertEquals(2, res.size());

        SiteMapEntry entry0 = res.get(0);
        assertEquals("always", entry0.getChangefreq());
        assertEquals("2008-06-12", entry0.getLastmod());
        assertEquals("null/\\.magnolia/pages/messages\\.(.*)\\.js", entry0.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/messages", entry0.getPath());
        assertEquals(0.8d, entry0.getPriority(), 0);
        assertEquals(4, entry0.getLevel());
        assertFalse(entry0.isStyleAlert());

        SiteMapEntry entry1 = res.get(1);
        assertEquals("null/.magnolia/dialogs/fileThumbnail.jpg", entry1.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/dialogsFileThumbnail", entry1.getPath());
    }

    @Test
    public void testGetSiteMapBeansForUriNotForEdit() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        // WHEN
        List<SiteMapEntry> res = service.getSiteMapBeans(siteMapNode, true, false);

        // THEN
        assertNotNull(res);
        assertEquals(2, res.size());

        SiteMapEntry entry0 = res.get(0);
        assertEquals("null/\\.magnolia/pages/messages\\.(.*)\\.js", entry0.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/messages", entry0.getPath());
        assertEquals(4, entry0.getLevel());
        assertFalse(entry0.isStyleAlert());

        SiteMapEntry entry1 = res.get(1);
        assertEquals("null/.magnolia/dialogs/fileThumbnail.jpg", entry1.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/dialogsFileThumbnail", entry1.getPath());
    }

    @Test
    public void testGetSiteMapBeansNotForEdit() throws RepositoryException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");
        // WHEN
        List<SiteMapEntry> res = IteratorUtils.toList(service.getSiteMapBeans(siteMapNode));
        Collections.sort(res);

        // THEN
        assertNotNull(res);
        assertEquals(4, res.size());

        SiteMapEntry entry0 = res.get(0);
        assertEquals("always", entry0.getChangefreq());
        assertEquals("2008-06-12", entry0.getLastmod());
        assertEquals("null/\\.magnolia/pages/messages\\.(.*)\\.js", entry0.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/messages", entry0.getPath());
        assertEquals(0.8d, entry0.getPriority(), 0);
        assertEquals(4, entry0.getLevel());
        assertFalse(entry0.isStyleAlert());

        SiteMapEntry entry1 = res.get(1);
        assertEquals("null/.magnolia/dialogs/fileThumbnail.jpg", entry1.getLoc());
        assertEquals("/modules/adminInterface/virtualURIMapping/dialogsFileThumbnail", entry1.getPath());

        SiteMapEntry entry2 = res.get(2);
        assertEquals("defaultBaseUrl/demo-site/article/articleSection", entry2.getLoc());

        SiteMapEntry entry3 = res.get(3);
        assertEquals("defaultBaseUrl/demo-site/article/article1", entry3.getLoc());
    }

    @Test
    public void testUpdatePageNode() throws RepositoryException {
        // GIVEN
        SiteMapEntry entry = new SiteMapEntry();
        entry.setPath("/demo-site/article/article1");
        entry.setChangefreq("changefreq");
        entry.setPriority(0.5d);
        entry.setHide(true);
        entry.setHideChildren(true);

        // WHEN
        service.updatePageNode(entry);

        // THEN
        Node node = websiteSession.getNode("/demo-site/article/article1");
        assertTrue(SiteMapNodeTypes.GoogleSiteMap.isHide(node));
        assertEquals("changefreq", SiteMapNodeTypes.GoogleSiteMap.getChangeFreq(node));
        assertEquals(0.5d, SiteMapNodeTypes.GoogleSiteMap.getPriority(node), 0);
        assertTrue(SiteMapNodeTypes.GoogleSiteMap.isHideChildren(node));
    }

}
