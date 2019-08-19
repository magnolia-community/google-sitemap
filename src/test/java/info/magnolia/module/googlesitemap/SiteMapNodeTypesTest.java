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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Test;

/**
 * Test class for {@link SiteMapNodeTypes}.
 */
public class SiteMapNodeTypesTest extends SiteMapTestUtil {

    @Test
    public void testSiteMapGetType() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.TYPE, "someString");

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getType(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals("someString", value);
    }

    @Test
    public void testSiteMapGetTypeNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getType(siteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapGetDisplayName() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, "someString");

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getDisplayName(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals("someString", value);
    }

    @Test
    public void testSiteMapGetDisplayNameNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getDisplayName(siteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapGetUrl() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.URL, "someString");

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getUrl(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals("someString", value);
    }

    @Test
    public void testSiteMapGetUrlNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getUrl(siteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapIsVirtualUriMappingIncluded() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        assertFalse(SiteMapNodeTypes.SiteMap.isVirtualUriMappingIncluded(siteMap));

        // WHEN
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI, true);

        // THEN
        assertTrue(SiteMapNodeTypes.SiteMap.isVirtualUriMappingIncluded(siteMap));
    }

    @Test
    public void testSiteMapGetPages() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.PAGES, new String[] { "a", "b" });

        // WHEN
        List<String> value = SiteMapNodeTypes.SiteMap.getPages(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals("a", value.get(0));
        assertEquals("b", value.get(1));
    }

    @Test
    public void testSiteMapGetPagesNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        List<String> value = SiteMapNodeTypes.SiteMap.getPages(siteMap);

        // THEN
        assertNotNull(value);
        assertTrue(value.isEmpty());
    }

    @Test
    public void testSiteMapGetDefaultChangeFreq() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.DEFAULT_CHANGEFREQ, "someString");

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getDefaultChangeFreq(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals("someString", value);
    }

    @Test
    public void testSiteMapGetDefaultChangeFreqNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        String value = SiteMapNodeTypes.SiteMap.getDefaultChangeFreq(siteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapGetDefaultPriority() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.DEFAULT_PRIORITY, 0.5d);

        // WHEN
        Double value = SiteMapNodeTypes.SiteMap.getDefaultPriority(siteMap);

        // THEN
        assertNotNull(value);
        assertEquals(0.5d, value.doubleValue(), 0);
    }

    @Test
    public void testSiteMapGetDefaultPriorityNotSet() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        Double value = SiteMapNodeTypes.SiteMap.getDefaultPriority(siteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapUpdate() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);

        // WHEN
        SiteMapNodeTypes.SiteMap.update(siteMap, "type", "url", "displayName", true, "changeFreq", 0.5d, Arrays.asList("a", "b"));

        // THEN
        assertEquals("type", siteMap.getProperty(SiteMapNodeTypes.SiteMap.TYPE).getString());
        assertEquals("url", siteMap.getProperty(SiteMapNodeTypes.SiteMap.URL).getString());
        assertEquals("displayName", siteMap.getProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME).getString());
        assertEquals(true, siteMap.getProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI).getBoolean());
        assertEquals("a", siteMap.getProperty(SiteMapNodeTypes.SiteMap.PAGES).getValues()[0].getString());
        assertEquals("b", siteMap.getProperty(SiteMapNodeTypes.SiteMap.PAGES).getValues()[1].getString());
        assertEquals("changeFreq", siteMap.getProperty(SiteMapNodeTypes.SiteMap.DEFAULT_CHANGEFREQ).getString());
        assertEquals(0.5d, siteMap.getProperty(SiteMapNodeTypes.SiteMap.DEFAULT_PRIORITY).getDouble(), 0);
    }

    @Test
    public void testGoogleSiteMapGetChangeFreq() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);
        googleSiteMap.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ, "someString");

        // WHEN
        String value = SiteMapNodeTypes.GoogleSiteMap.getChangeFreq(googleSiteMap);

        // THEN
        assertNotNull(value);
        assertEquals("someString", value);
    }

    @Test
    public void testGoogleSiteMapGetChangeFreqNotSet() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);

        // WHEN
        String value = SiteMapNodeTypes.GoogleSiteMap.getChangeFreq(googleSiteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testGoogleSiteMapGetPriority() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);
        googleSiteMap.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY, 0.5d);

        // WHEN
        Double value = SiteMapNodeTypes.GoogleSiteMap.getPriority(googleSiteMap);

        // THEN
        assertNotNull(value);
        assertEquals(0.5d, value.doubleValue(), 0);
    }

    @Test
    public void testGoogleSiteMapGetPriorityNotSet() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);

        // WHEN
        Double value = SiteMapNodeTypes.GoogleSiteMap.getPriority(googleSiteMap);

        // THEN
        assertNull(value);
    }

    @Test
    public void testSiteMapIsHide() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);
        assertFalse(SiteMapNodeTypes.GoogleSiteMap.isHide(googleSiteMap));

        // WHEN
        googleSiteMap.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP, true);

        // THEN
        assertTrue(SiteMapNodeTypes.GoogleSiteMap.isHide(googleSiteMap));
    }

    @Test
    public void testSiteMapIsHideChildren() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);
        assertFalse(SiteMapNodeTypes.GoogleSiteMap.isHideChildren(googleSiteMap));

        // WHEN
        googleSiteMap.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN, true);

        // THEN
        assertTrue(SiteMapNodeTypes.GoogleSiteMap.isHideChildren(googleSiteMap));
    }

    @Test
    public void testGoogleSiteMapUpdate() throws RepositoryException {
        // GIVEN
        Node googleSiteMap = googleSiteMapSession.getRootNode().addNode("googleSiteMap", NodeTypes.Page.NAME);
        googleSiteMap.addMixin(SiteMapNodeTypes.GoogleSiteMap.NAME);

        // WHEN
        SiteMapNodeTypes.GoogleSiteMap.update(googleSiteMap, "changeFrq", 0.1d, true, true);

        // THEN
        assertEquals("changeFrq", googleSiteMap.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ).getString());
        assertEquals(0.1d, googleSiteMap.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY).getDouble(), 0);
        assertEquals(true, googleSiteMap.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP ).getBoolean());
        assertEquals(true, googleSiteMap.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN).getBoolean());

    }
}
