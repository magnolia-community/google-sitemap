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


import static info.magnolia.test.hamcrest.ExecutionMatcher.throwsNothing;
import static org.junit.Assert.*;

import info.magnolia.test.hamcrest.Execution;

import java.io.StringReader;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


/**
 * Main test class for {@link SiteMapXMLUtilImpl}.
 */
public class SiteMapXMLUtilImplTest extends ServiceTestUtil {
    private SiteMapXMLUtilImpl xmlUtil;
    private Validator validator;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        xmlUtil = new SiteMapXMLUtilImpl(new Provider<SiteMapService>() {
            @Override
            public SiteMapService get() {
                return service;
            }
        });

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getClass().getResource("sitemap.xsd"));
        validator = schema.newValidator();
    }

    @Test
    public void generateAndValidateStandardSiteMapXML() throws Exception {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test1");

        // WHEN
        final String res = xmlUtil.generateSiteMapXML(siteMapNode);

        // THEN
        assertNotNull(res);
        assertTrue("Contains a url def for page", StringUtils.contains(res, "<url><loc>defaultBaseUrl/demo-site/article/articleSection</loc><lastmod>2014-01-08</lastmod><changefreq>always</changefreq><priority>0.8</priority></url>"));
        assertTrue("Contains a url def for Uri", StringUtils.contains(res, "<url><loc>null/\\.magnolia/pages/messages\\.(.*)\\.js</loc><lastmod>2008-06-12</lastmod><changefreq>always</changefreq><priority>0.8</priority></url>"));
        assertThat(new Execution() {
            @Override
            public void evaluate() throws Exception {
                validator.validate(new StreamSource(new StringReader(res)));
            }
        }, throwsNothing());
    }

    @Test
    public void generateMobileSiteMapXML() throws RepositoryException, JAXBException {
        // GIVEN
        Node siteMapNode = googleSiteMapSession.getNode("/test3");

        // WHEN
        String res = xmlUtil.generateSiteMapXML(siteMapNode);

        // THEN
        assertNotNull(res);
        assertTrue("Contains a url def for Pages", StringUtils.contains(res, "<url><loc>defaultBaseUrl/demo-site/news/subNewsSection2/news2</loc><lastmod>2014-01-08</lastmod><changefreq>hourly</changefreq><priority>0.7</priority><mobile:mobile/></url>"));
        assertTrue("Contains a url def for pages", StringUtils.contains(res, "<url><loc>defaultBaseUrl/demo-site/news/subNewsSection1/news11</loc><lastmod>2014-01-08</lastmod><changefreq>hourly</changefreq><priority>0.7</priority><mobile:mobile/></url>"));
        assertTrue("Contains a url def for Uri", StringUtils.contains(res, "<url><loc>null/.magnolia/dialogs/fileThumbnail.jpg</loc><lastmod>2004-11-02</lastmod><changefreq>hourly</changefreq><priority>0.7</priority><mobile:mobile/></url>"));
    }

    @Test
    public void generateNonExistingSiteMapXML() throws RepositoryException, JAXBException {
        // GIVEN

        // WHEN
        String res = xmlUtil.generateSiteMapXML(websiteNode);

        // THEN
        assertNotNull(res);
        assertTrue("Contains an empty XML", StringUtils.contains(res, "<?xml version=\"1.0\" ?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"></urlset>"));
    }
}
