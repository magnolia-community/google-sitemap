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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.api.action.ActionExecutionException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.BeanItem;

/**
 * test class for {@link SaveSiteMapEntryDialogAction}.
 */
public class SaveSiteMapEntryDialogActionTest extends SiteMapTestUtil {
    private GoogleMapEditorCallbackTest callback;
    private GoogleMapEditorValidatorTest validator;
    private SaveSiteMapEntryDialogActionDefinition definition;
    private SiteMapService service;

    private Node page;
    private SiteMapEntry entry;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        callback = new GoogleMapEditorCallbackTest();
        validator = new GoogleMapEditorValidatorTest();
        definition = new SaveSiteMapEntryDialogActionDefinition();
        definition.setName("save");
        definition.setLabel("label");

        // Init page
        Session webSiteSession = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        this.page = webSiteSession.getRootNode().addNode("page", NodeTypes.Page.NAME);
        NodeTypes.LastModified.update(page);
        this.entry = new SiteMapEntry(new GoogleSiteMapConfiguration(), null, this.page, 1, "weekly", 0.5d);

        service = mock(SiteMapService.class);
        when(service.updatePageNode(entry)).thenCallRealMethod();
    }

    @Test
    public void executeSaveEmptyNode() throws RepositoryException, ActionExecutionException {
        // GIVEN
        BeanItem<SiteMapEntry> item = new BeanItem<SiteMapEntry>(entry);
        entry.setChangefreq("Daly");
        entry.setPriority(0.2d);
        entry.setHide(true);

        SaveSiteMapEntryDialogAction action = new SaveSiteMapEntryDialogAction(definition, item, validator, callback, service);
        // WHEN
        action.execute();

        // THEN
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ));
        assertEquals("Daly", page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ).getString());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY));
        assertEquals(0.2d, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY).getDouble(), 0);
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP));
        assertEquals(true, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP).getBoolean());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN));
        assertEquals(false, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN).getBoolean());
    }

    @Test
    public void executeSaveExistingNode() throws RepositoryException, ActionExecutionException {
        // GIVEN
        page.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ, "Monthly");
        page.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY, 0.9d);
        page.setProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN, true);
        page.getSession().save();
        BeanItem<SiteMapEntry> item = new BeanItem<SiteMapEntry>(entry);
        entry.setChangefreq("Daly");
        entry.setPriority(0.2d);
        entry.setHide(true);

        SaveSiteMapEntryDialogAction action = new SaveSiteMapEntryDialogAction(definition, item, validator, callback, service);

        // WHEN
        action.execute();

        // THEN
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ));
        assertEquals("Daly", page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ).getString());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY));
        assertEquals(0.2d, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY).getDouble(), 0);
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP));
        assertEquals(true, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP).getBoolean());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN));
    }

    @Test
    public void executeSaveDefaultValue() throws RepositoryException, ActionExecutionException {
        // GIVEN
        BeanItem<SiteMapEntry> item = new BeanItem<SiteMapEntry>(entry);

        SaveSiteMapEntryDialogAction action = new SaveSiteMapEntryDialogAction(definition, item, validator, callback, service);

        // WHEN
        action.execute();

        // THEN
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ));
        assertEquals("weekly", page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ).getString());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY));
        assertEquals(0.5d, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY).getDouble(), 0);
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP));
        assertEquals(false, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP).getBoolean());
        assertTrue(page.hasProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN));
        assertEquals(false, page.getProperty(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_HIDE_IN_GOOGLESITEMAP_CHILDREN).getBoolean());
    }
}
