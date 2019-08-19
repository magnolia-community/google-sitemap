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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.*;


import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.ServiceTestUtil;

import java.util.List;

import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

/**
 * Main test class for {@link SiteMapEntryContainer}.
 */
public class SiteMapEntryContainerTest extends ServiceTestUtil {

    private SiteMapEntryContainer container;
    private String siteMapPath;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        siteMapPath = "/test2";
    }

    @Test
    public void checkThatGetChildrenHasAllEntry() throws RepositoryException {
        // GIVEN
        container = new SiteMapEntryContainer(siteMapPath, false, service);
        container.refresh();
        SiteMapEntry itemId = new SiteMapEntry(new GoogleSiteMapConfiguration(), "loc", websiteNode, 1, "weekly", 0.5d);

        // WHEN
        List<SiteMapEntry> children = (List<SiteMapEntry>) container.getChildren(itemId);

        // THEN
        assertThat(children, not(nullValue()));
        assertThat(children, hasSize(2));
        assertThat("/demo-site/article", is(children.get(0).getPath()));
        assertThat("/demo-site/news", is(children.get(1).getPath()));
    }

    @Test
    public void checkThatAllEntryArePresent() throws RepositoryException {
        // GIVEN
        container = new SiteMapEntryContainer(siteMapPath, false, service);
        container.refresh();

        // WHEN
        List<SiteMapEntry> children = container.getItemIds();

        // THEN
        assertThat(children, not(nullValue()));
        assertThat(children, hasSize(11));
        assertThat("/demo-site/article", is(children.get(0).getPath()));
        assertThat("/demo-site/article/article1", is(children.get(1).getPath()));
        assertThat("/demo-site/article/article2", is(children.get(2).getPath()));
        assertThat("/demo-site/article/articleSection", is(children.get(3).getPath()));
        assertThat("/demo-site/article/articleSection/article3", is(children.get(4).getPath()));
        assertThat("/demo-site/news", is(children.get(5).getPath()));
    }
}
