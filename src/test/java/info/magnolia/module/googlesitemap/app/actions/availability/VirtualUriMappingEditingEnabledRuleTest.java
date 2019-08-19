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
package info.magnolia.module.googlesitemap.app.actions.availability;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemId;

import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link VirtualUriMappingEditingEnabledRule}.
 */
public class VirtualUriMappingEditingEnabledRuleTest extends SiteMapTestUtil {

    private VirtualUriMappingEditingEnabledRule rule;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        rule = new VirtualUriMappingEditingEnabledRule();
    }

    @Test
    public void testHasVirtualUriMappingTest() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI, true);
        JcrItemId item = new JcrItemId(siteMap.getIdentifier(), GoogleSiteMapConfiguration.WORKSPACE);

        // WHEN
        boolean isAvailable = rule.isAvailable(Arrays.asList(item));

        // THEN
        assertTrue(isAvailable);
    }

    @Test
    public void testHasNoVirtualUriMappingTest() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI, false);
        JcrItemId item = new JcrItemId(siteMap.getIdentifier(), GoogleSiteMapConfiguration.WORKSPACE);

        // WHEN
        boolean isAvailable = rule.isAvailable(Arrays.asList(item));

        // THEN
        assertFalse(isAvailable);
    }

    @Test
    public void testHasVirtualUriMappingWrongTypeTest() throws RepositoryException {
        // GIVEN
        Node siteMap = googleSiteMapSession.getRootNode().addNode("siteMap", NodeTypes.Page.NAME);
        siteMap.setProperty(SiteMapNodeTypes.SiteMap.INCLUDE_VIRTUAL_URI, true);
        JcrItemId item = new JcrItemId(siteMap.getIdentifier(), GoogleSiteMapConfiguration.WORKSPACE);

        // WHEN
        boolean isAvailable = rule.isAvailable(Arrays.asList(item));

        // THEN
        assertFalse(isAvailable);
    }
}
