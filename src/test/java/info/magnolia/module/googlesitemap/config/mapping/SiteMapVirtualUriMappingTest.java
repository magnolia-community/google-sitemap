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

import static org.junit.Assert.*;

import info.magnolia.context.SystemContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.virtualuri.VirtualUriMapping;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Optional;

import javax.jcr.ImportUUIDBehavior;

import org.junit.Before;
import org.junit.Test;

public class SiteMapVirtualUriMappingTest extends SiteMapTestUtil {

    private String prefix = "sitemaps";
    private SiteMapVirtualUriMapping mapping;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mapping = new SiteMapVirtualUriMapping(Components.getComponent(SystemContext.class));
        mapping.setPrefix(prefix);

        FileInputStream fis = new FileInputStream(getClass().getResource("/googleSitemaps.xml").getFile());
        DataTransporter.importXmlStream(fis, GoogleSiteMapConfiguration.WORKSPACE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        // Initial check
        assertTrue(googleSiteMapSession.nodeExists("/test1"));
        assertTrue(googleSiteMapSession.nodeExists("/test2"));
    }

    @Test
    public void sitemapCanPublish() throws InterruptedException {
        // GIVEN
        Thread.sleep(300);
        // WHEN
        Optional<VirtualUriMapping.Result> result = mapping.mapUri(URI.create("/test1Uri"));
        Optional<VirtualUriMapping.Result> noResult = mapping.mapUri(URI.create("/test1"));

        // THEN
        assertEquals(prefix + "/test1.xml", result.get().getToUri());
        assertEquals("/test1Uri".length(), result.get().getWeight());
        assertTrue(mapping.isValid());
        assertFalse("'test1' is the display name and 'test1Uri' is the virtual URI. No mapping has to be defined for the display name.", noResult.isPresent());
    }

    @Test
    public void sitemapCanNotPublish() throws InterruptedException {
        // GIVEN
        Thread.sleep(300);
        // WHEN
        Optional<VirtualUriMapping.Result> result = mapping.mapUri(URI.create("/test2"));

        // THEN
        assertFalse("'test2' is not published and should not be part of the URI map", result.isPresent());
    }

}
