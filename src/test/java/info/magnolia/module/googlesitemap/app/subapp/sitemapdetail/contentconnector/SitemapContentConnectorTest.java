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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentconnector;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.contentapp.detail.DetailLocation;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;

/**
 * Tests for {@link SitemapContentConnector}.
 */
public class SitemapContentConnectorTest {

    private SitemapContentConnectorDefinition contentConnectorDefinition = new SitemapContentConnectorDefinition();

    private SitemapContentConnector contentConnector;

    private SiteMapService siteMapService;

    private MockNode existingSitemapNode = new MockNode("sitemap");

    private MockSession session = new MockSession(GoogleSiteMapConfiguration.WORKSPACE);

    private MockWebContext ctx = new MockWebContext();

    private SubAppContext subAppContext;

    private GoogleSiteMapConfiguration configuration = new GoogleSiteMapConfiguration();

    private SiteMapEntry pageEntry;

    private SiteMapEntry subPageEntry;

    private SiteMapEntry subSubPageEntry;

    private SiteMapEntry virtualUriEntry;

    @Before
    public void setUp() throws Exception {
        this.ctx.addSession(GoogleSiteMapConfiguration.WORKSPACE, session);

        this.siteMapService = mock(SiteMapService.class);
        this.subAppContext = mock(SubAppContext.class);

        final MockNode rootNode = (MockNode) this.session.getRootNode();
        rootNode.addNode(existingSitemapNode);

        MgnlContext.setInstance(ctx);

        mockPageEntries();
    }

    @After
    public void tearDown() throws Exception {
         MgnlContext.setInstance(null);
    }

    @Test
    public void testUrlFragmentToSitemapEntryConversion() throws Exception {
        // GIVEN
        initContentConnectorWithExistingSitemap(false);

        // WHEN
        Object pageId = contentConnector.getItemIdByUrlFragment("/sitemap@/page");
        Object subPageId = contentConnector.getItemIdByUrlFragment("/sitemap@/page/subPage");
        Object subSubPageFragmentId = contentConnector.getItemIdByUrlFragment("/sitemap@/page/subPage/subSubPage");

        // THEN
        assertThat(pageEntry, equalTo(pageId));
        assertThat(subPageEntry, equalTo(subPageId));
        assertThat(subSubPageEntry, equalTo(subSubPageFragmentId));
    }

    @Test
    public void tesSitemapEntryTotUrlFragmentConversion() throws Exception {
        // GIVEN
        initContentConnectorWithExistingSitemap(false);

        // WHEN
        String pageFragment = contentConnector.getItemUrlFragment(pageEntry);
        String subPageFragment = contentConnector.getItemUrlFragment(subPageEntry);
        String subSubPageFragment = contentConnector.getItemUrlFragment(subSubPageEntry);

        // THEN
        assertThat(pageFragment, equalTo("/sitemap@/page"));
        assertThat(subPageFragment, equalTo("/sitemap@/page/subPage"));
        assertThat(subSubPageFragment, equalTo("/sitemap@/page/subPage/subSubPage"));
    }

    @Test
    public void testSitemapContentConnectorHandlingCapabilities() throws Exception {
        // GIVEN
        initContentConnectorWithExistingSitemap(false);

        // WHEN
        boolean isSupportsSitemapEntry = contentConnector.canHandleItem(new SiteMapEntry());
        boolean isSupportsArbitraryObject = contentConnector.canHandleItem(new Object());

        // THEN
        assertThat(isSupportsArbitraryObject, is(false));
        assertThat(isSupportsSitemapEntry, is(true));
    }

    @Test
    public void testSitemapEntryToBeanItemConversion() throws Exception {
        // GIVEN
        initContentConnectorWithExistingSitemap(false);

        // WHEN
        final Item vaadinItem = contentConnector.getItem(pageEntry);

        // THEN
        assertThat(vaadinItem, instanceOf(BeanItem.class));
        assertThat(pageEntry, equalTo(((BeanItem) vaadinItem).getBean()));
    }

    @Test
    public void testDoesNotFailAndEmptyForNonExistingSitemap() throws Exception {
        // GIVEN
        initContentConnectorWithNonExistingSitemap(false);

        // WHEN
        final Container c = contentConnector.getContainer();

        // THEN
        assertThat(c.size(), equalTo(0));
    }

    @Test
    public void testContainsVirtualUrisWhenConfiguredAccordingly() throws Exception {
        // GIVEN
        initContentConnectorWithExistingSitemap(true);

        // WHEN
        Object pageId = contentConnector.getItemIdByUrlFragment("/sitemap@/page");
        Container.Indexed c = contentConnector.getContainer();

        // THEN
        assertThat(virtualUriEntry, equalTo(pageId));
        assertThat(c.size(), equalTo(1));
        assertThat(virtualUriEntry, equalTo(c.firstItemId()));
    }

    private void mockPageEntries() throws RepositoryException {
        MockNode page = getMockPage("page");
        MockNode subPage = getMockPage("subPage");
        MockNode subSubPage = getMockPage("subSubPage");

        page.addNode(subPage);
        subPage.addNode(subSubPage);

        pageEntry = new SiteMapEntry(configuration, "/page", page, 0, "daily", 0.9);
        subPageEntry = new SiteMapEntry(configuration, "/subPage", subPage, 0, "daily", 0.9);
        subSubPageEntry = new SiteMapEntry(configuration, "/subSubPage", subSubPage, 0, "daily", 0.9);

        virtualUriEntry = new SiteMapEntry(configuration, "/page", page, 0, "daily", 0.9);
        virtualUriEntry.setTo("/to");
        virtualUriEntry.setFrom("/from");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(
                        pageEntry,
                        subPageEntry,
                        subSubPageEntry
                );
            }
        }).when(siteMapService).getSiteMapBeans(eq(existingSitemapNode), eq(false), eq(true));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(virtualUriEntry);
            }
        }).when(siteMapService).getSiteMapBeans(eq(existingSitemapNode), eq(true), eq(true));
    }

    private MockNode getMockPage(String name) throws RepositoryException {
        MockNode page = new MockNode(name);
        page.setProperty(NodeTypes.LastModified.NAME, GregorianCalendar.getInstance());
        return page;
    }

    private void initContentConnectorWithExistingSitemap(boolean isVirtualUris) {
        doReturn(new DetailLocation("app", "subapp", "/sitemap")).when(subAppContext).getLocation();
        doCreateSitemapContentConnector(isVirtualUris);
    }

    private void initContentConnectorWithNonExistingSitemap(boolean isVirtualUris) {
        doReturn(new DetailLocation("app", "subapp", "/not-a-sitemap")).when(subAppContext).getLocation();
        doCreateSitemapContentConnector(isVirtualUris);
    }

    private void doCreateSitemapContentConnector(boolean isVirtualUris) {
        this.contentConnectorDefinition.setVirtualUris(isVirtualUris);
        this.contentConnector = new SitemapContentConnector(contentConnectorDefinition, siteMapService, subAppContext);
    }


}
