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
package info.magnolia.module.googlesitemap.setup.migration;

import static org.junit.Assert.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.jcr.wrapper.JCRMgnlPropertiesFilteringNodeWrapper;
import info.magnolia.module.InstallContextImpl;
import info.magnolia.module.ModuleRegistryImpl;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.repository.RepositoryManager;
import info.magnolia.test.RepositoryTestCase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 */
public class SiteMapDefinitionMigrationTaskTest extends RepositoryTestCase {

    private static final Logger log = LoggerFactory.getLogger(SiteMapDefinitionMigrationTaskTest.class);
    private Session googleSiteMapSession;
    private Session websiteSession;
    protected InstallContextImpl installContext;

    protected String siteMapNodeType = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<nodeTypes" + " xmlns:rep=\"internal\""
            + " xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\"" + " xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\""
            + " xmlns:mgnl=\"http://www.magnolia.info/jcr/mgnl\"" + " xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" + "<nodeType name=\"" + SiteMapNodeTypes.SiteMap.NAME + "\""
            + " isMixin=\"false\" hasOrderableChildNodes=\"true\" primaryItemName=\"\">"
            + "<supertypes>"
            + "<supertype>mgnl:content</supertype>"
            + "</supertypes>"
            + "</nodeType>"
            + "</nodeTypes>";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        googleSiteMapSession = MgnlContext.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE);
        websiteSession = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        // Register siteMap node type
        RepositoryManager repositoryManager = Components.getComponent(RepositoryManager.class);
        try {
            repositoryManager.getRepositoryProvider("magnolia").registerNodeTypes(new ByteArrayInputStream(siteMapNodeType.getBytes()));
        } catch (RepositoryException e) {
            log.error("", e);
        }

        // Import the sourrce definitions
        File inputFile1 = new File(getClass().getResource("/website.demo-project.xml").getFile());
        InputStream inputStream1 = new FileInputStream(inputFile1);
        DataTransporter.importXmlStream(inputStream1, RepositoryConstants.WEBSITE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        File inputFile2 = new File(getClass().getResource("/website.demoFeaturesSiteMap.xml").getFile());
        InputStream inputStream2 = new FileInputStream(inputFile2);
        DataTransporter.importXmlStream(inputStream2, RepositoryConstants.WEBSITE, "/", "test-stream", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, false);

        final ModuleRegistryImpl moduleRegistry = new ModuleRegistryImpl();
        installContext = new InstallContextImpl(moduleRegistry);

    }

    @Override
    protected String getRepositoryConfigFileName() {
        String repositoryFileName = "test-siteMap-repositories.xml";
        setRepositoryConfigFileName(repositoryFileName);
        return repositoryFileName;
    }

    @Test
    @Ignore
    public void testDoExecuteRootDefinition() throws TaskExecutionException, RepositoryException {
        // GIVEN
        Set<String> siteDef = new HashSet<String>(Arrays.asList("/demo-features/content-templates", "/demo-features/special-templates/glossary", "/demo-features/content-structure"));
        SiteMapDefinitionMigrationTask task = new SiteMapDefinitionMigrationTask("name", "description", RepositoryConstants.WEBSITE, null);
        assertTrue(websiteSession.nodeExists("/demoFeaturesSiteMap"));
        // WHEN
        task.doExecute(installContext);

        // THEN
        assertTrue("Should have a migrated SiteMapDefinition", googleSiteMapSession.nodeExists("/demoFeaturesSiteMap"));
        Node siteMap = googleSiteMapSession.getNode("/demoFeaturesSiteMap");
        assertTrue("Property should have been copied", siteMap.hasProperty("mgnl:template"));
        assertEquals("google-sitemap:pages/siteMapsConfiguration", siteMap.getProperty("mgnl:template").getString());
        assertEquals("/demoFeaturesSiteMap", siteMap.getProperty(SiteMapNodeTypes.SiteMap.URL).getString());
        assertTrue("Property should have been copied", siteMap.hasProperty("mgnl:activationStatus"));
        assertEquals(true, siteMap.getProperty("mgnl:activationStatus").getBoolean());
        assertFalse(siteMap.hasNode("content"));
        assertTrue(siteMap.hasNode(SiteMapNodeTypes.SiteMap.PAGES));
        Node sites = siteMap.getNode(SiteMapNodeTypes.SiteMap.PAGES);

        Node filteredNode = new JCRMgnlPropertiesFilteringNodeWrapper(sites);
        PropertyIterator iterator = filteredNode.getProperties();
        while (iterator.hasNext()) {
            Property property = iterator.nextProperty();
            assertTrue("Migrated content should have this site mapp defined", siteDef.contains(property.getString()));
        }

        // assertFalse("No virtual URI mapping should be defined", siteMap.getProperty(GoogleSiteMapConfiguration.INCLUDE_VIRTUAL_URI_MAPPINGS_PROPERTIES).getBoolean());

        assertFalse(websiteSession.nodeExists("/demoFeaturesSiteMap"));

    }

    @Test
    @Ignore
    public void testDoExecuteSubFolderDefinition() throws TaskExecutionException, RepositoryException {
        // GIVEN
        Set<String> siteDef = new HashSet<String>(Arrays.asList("/demo-project/about/subsection-articles", "/demo-project/news-and-events/events-overview"));
        SiteMapDefinitionMigrationTask task = new SiteMapDefinitionMigrationTask("name", "description", RepositoryConstants.WEBSITE, null);
        assertTrue(websiteSession.nodeExists("/demo-project/demoProjectSiteMap"));
        // WHEN
        task.doExecute(installContext);

        // THEN
        assertTrue("Should have a migrated SiteMapDefinition", googleSiteMapSession.nodeExists("/demo-project/demoProjectSiteMap"));
        Node siteMap = googleSiteMapSession.getNode("/demo-project/demoProjectSiteMap");
        assertTrue("Property should have been copied", siteMap.hasProperty("mgnl:template"));
        assertEquals("google-sitemap:pages/siteMapsConfiguration", siteMap.getProperty("mgnl:template").getString());
        // assertEquals("/demo-project/demoProjectSiteMap", siteMap.getProperty(GoogleSiteMapConfiguration.SITE_MAP_URL_PROPERTY_NAME).getString());
        // assertEquals(SiteMapType.Standard.toString(), siteMap.getProperty(GoogleSiteMapConfiguration.SITE_MAP_TYPE_PROPERTY_NAME).getString());
        assertTrue("Property should have been copied", siteMap.hasProperty("mgnl:activationStatus"));
        assertEquals(true, siteMap.getProperty("mgnl:activationStatus").getBoolean());

        assertFalse(siteMap.hasNode("content"));
        // assertTrue(siteMap.hasNode(GoogleSiteMapConfiguration.SITE_DIALOG_CONFIGURATION_NAME));
        // Node sites = siteMap.getNode(GoogleSiteMapConfiguration.SITE_DIALOG_CONFIGURATION_NAME);

        // Node filteredNode = new JCRMgnlPropertiesFilteringNodeWrapper(sites);
        // PropertyIterator iterator = filteredNode.getProperties();
        // while (iterator.hasNext()) {
        // Property property = iterator.nextProperty();
        // assertTrue("Migrated content should have this site mapp defined", siteDef.contains(property.getString()));
        // }
        //
        // assertTrue("Virtual URI mapping should be defined", siteMap.getProperty(GoogleSiteMapConfiguration.INCLUDE_VIRTUAL_URI_MAPPINGS_PROPERTIES).getBoolean());
        assertFalse(websiteSession.nodeExists("/demo-project/demoProjectSiteMap"));

    }

    @Test
    @Ignore
    public void testDoExecuteConfiguredSearchPath() throws TaskExecutionException, RepositoryException {
        // GIVEN
        SiteMapDefinitionMigrationTask task = new SiteMapDefinitionMigrationTask("name", "description", RepositoryConstants.WEBSITE, "/demo-project");

        // WHEN
        task.doExecute(installContext);

        // THEN
        assertTrue("Should have a migrated SiteMapDefinition", googleSiteMapSession.nodeExists("/demo-project/demoProjectSiteMap"));
        assertFalse("Should not have a migrated SiteMapDefinition", googleSiteMapSession.nodeExists("/demoFeaturesSiteMap"));
        assertFalse(websiteSession.nodeExists("/demo-project/demoProjectSiteMap"));
        assertTrue(websiteSession.nodeExists("/demoFeaturesSiteMap"));
    }

}
