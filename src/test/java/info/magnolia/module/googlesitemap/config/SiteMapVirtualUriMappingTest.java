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
package info.magnolia.module.googlesitemap.config;

import static org.junit.Assert.*;

import info.magnolia.cms.beans.config.VirtualURIMapping.MappingResult;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.jcr.ImportUUIDBehavior;

import org.junit.Before;
import org.junit.Test;

/**
 * Main test class for {@link SiteMapVirtualUriMapping}.
 */
public class SiteMapVirtualUriMappingTest extends SiteMapTestUtil {

    private SiteMapVirtualUriMapping uriMapping;
    private String prefix = "sitemaps";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Init SiteMap with Observation
        uriMapping = new SiteMapVirtualUriMapping(MgnlContext.getSystemContext());
        uriMapping.setPrefix(prefix);

        // Register SiteMap
        File sitemapFile = new File(getClass().getResource("/googleSitemaps.xml").getFile());
        InputStream inputStream = new FileInputStream(sitemapFile);
        DataTransporter.importXmlStream(inputStream, GoogleSiteMapConfiguration.WORKSPACE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        // Initial check
        assertTrue(googleSiteMapSession.nodeExists("/test1"));
        assertTrue(googleSiteMapSession.nodeExists("/test2"));
    }


    @Test
    public void testSitemapPublished() throws InterruptedException {
        // GIVEN
        Thread.sleep(300);
        // WHEN
        MappingResult result = uriMapping.mapURI("/test1Uri");
        MappingResult noResult = uriMapping.mapURI("/test1");

        // THEN
        assertTrue(uriMapping.isValid());
        assertEquals("/test1Uri".length(), result.getLevel());
        assertNotNull("A virtual URI was entered. This is now used to map", result);
        assertEquals(prefix + "/test1.xml", result.getToURI());
        assertNull("'test1' is the display name and 'test1Uri' is the virtual URI. No mapping has to be defined for the display name.", noResult);
    }

    @Test
    public void testSitemapNotPublished() throws InterruptedException {
        // GIVEN
        Thread.sleep(300);
        // WHEN
        MappingResult result = uriMapping.mapURI("/test2");

        // THEN
        assertNull("'test2' is not published and should not be part of the URI map", result);
    }

}
