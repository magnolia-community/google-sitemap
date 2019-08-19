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
package info.magnolia.module.googlesitemap.setup;

import static info.magnolia.test.hamcrest.ExecutionMatcher.throwsNothing;
import static info.magnolia.test.hamcrest.NodeMatchers.hasNode;
import static info.magnolia.test.hamcrest.NodeMatchers.hasProperty;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import info.magnolia.cms.core.version.VersionManager;
import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.formatter.FolderNameColumnFormatter;
import info.magnolia.module.model.Version;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.repository.RepositoryManager;
import info.magnolia.test.hamcrest.Execution;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Test;

/**
 * Tests for {@link GoogleSiteMapVersionHandler}.
 */
public class GoogleSiteMapVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Session session;
    private final String siteMapNodeTypeConfigFile = "/test-google-sitemap-nodetypes.xml";

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/google-sitemap.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new GoogleSiteMapVersionHandler();
    }

    @Override
    protected String[] getExtraWorkspaces() {
        return new String[]{"templates", GoogleSiteMapConfiguration.WORKSPACE};
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList("/META-INF/magnolia/core.xml");
    }

    @Override
    protected String getExtraNodeTypes() {
        return siteMapNodeTypeConfigFile;
    }

    @Override
    public String getRepositoryConfigFileName() {
        return "/test-siteMap-repositories.xml";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);

        addSupportForSetupModuleRepositoriesTask(null);

        // Register Google SiteMap node type
        try {
            Components.getComponent(RepositoryManager.class).getRepositoryProvider("magnolia").registerNodeTypes(ClasspathResourcesUtil.getResource(siteMapNodeTypeConfigFile).openStream());
        } catch (RepositoryException e) {
        }

        this.setupConfigProperty("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/sites/field", "workspace", "website");
        this.setupConfigNode("/modules/google-sitemap/dialogs/generic/controls/googleSiteMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapPriority");
        this.setupConfigNode("/modules/google-sitemap/dialogs/generic/controls/googleSiteMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapChangefreq");
        this.setupConfigNode("/modules/google-sitemap/dialogs/generic/controls/googleVirtualUriMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapPriority");
        this.setupConfigNode("/modules/google-sitemap/dialogs/generic/controls/googleVirtualUriMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapChangefreq");
        this.setupConfigNode("/modules/google-sitemap/templates/pages/siteMapsConfiguration");
        this.setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/deactivate");
        this.setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/export");
        this.setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/editSiteMap");
    }

    @Test
    public void updateFrom123ReordersSitemapBeforeConfigApp() throws Exception {
        // GIVEN
        Node manageApps = NodeUtil.createPath(session.getRootNode(), "modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps", NodeTypes.ContentNode.NAME);
        NodeUtil.createPath(manageApps, "rssAggregator", NodeTypes.ContentNode.NAME);
        NodeUtil.createPath(manageApps, "configuration", NodeTypes.ContentNode.NAME);

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.2.3"));

        // THEN
        NodeIterator it = manageApps.getNodes();
        assertTrue(manageApps.hasNode("siteMaps"));
        assertEquals("rssAggregator", it.nextNode().getName());
        assertEquals("siteMaps", it.nextNode().getName());
        assertEquals("configuration", it.nextNode().getName());
    }

    @Test
    public void freshInstallReordersSitemapBeforeConfigApp() throws Exception {
        // GIVEN
        Node manageApps = NodeUtil.createPath(session.getRootNode(), "modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps", NodeTypes.ContentNode.NAME);
        NodeUtil.createPath(manageApps, "rssAggregator", NodeTypes.ContentNode.NAME);
        NodeUtil.createPath(manageApps, "configuration", NodeTypes.ContentNode.NAME);
        // we have to create the siteMaps node artificially before bootstrapping, otherwise test would fail in maven
        NodeUtil.createPath(manageApps, "siteMaps", NodeTypes.ContentNode.NAME);

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        NodeIterator it = manageApps.getNodes();
        assertTrue(manageApps.hasNode("siteMaps"));
        assertEquals("rssAggregator", it.nextNode().getName());
        assertEquals("siteMaps", it.nextNode().getName());
        assertEquals("configuration", it.nextNode().getName());
    }

    @Test
    public void update22() throws RepositoryException, ModuleManagementException {
        // GIVEN
        this.setupConfigProperty("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap", "property", "value");
        this.setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/browser");
        this.setupConfigProperty("/modules/google-sitemap/apps/siteMaps/subApps/pages", "property", "value");
        this.setupConfigProperty("/modules/google-sitemap/apps/siteMaps/subApps/browser/workbench/contentViews/list/columns/name", "formatterClass", "info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.formatter.FolderNameColumnFormatter");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0"));

        // THEN
        NodeIterator it = session.getNode("/modules/google-sitemap/apps/siteMaps/subApps").getNodes();
        assertEquals("browser", it.nextNode().getName());
        assertEquals("pages", it.nextNode().getName());
        assertConfig(FolderNameColumnFormatter.class.getName(), "/modules/google-sitemap/apps/siteMaps/subApps/browser/workbench/contentViews/list/columns/name/formatterClass");
        assertConfig("0.5", "/modules/google-sitemap/config/priority");
        assertConfig("weekly", "/modules/google-sitemap/config/changeFrequency");
        assertTrue(session.nodeExists("/modules/google-sitemap/fieldTypes/siteMapSelect"));
    }

    @Test
    public void updateFrom21ConfigureActions() throws Exception {
        // GIVEN
        Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        this.setupConfigProperty("/modules/google-sitemap/apps/siteMaps/subApps/browser/workbench/contentViews/list/columns/name", "formatterClass", "info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.formatter.FolderNameColumnFormatter");
        Node addSiteMapAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "addSiteMap", NodeTypes.ContentNode.NAME);
        Node deleteAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "delete", NodeTypes.ContentNode.NAME);
        Node editSiteMapAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "editSiteMap", NodeTypes.ContentNode.NAME);
        Node activateAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "activate", NodeTypes.ContentNode.NAME);
        Node deactivateAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "deactivate", NodeTypes.ContentNode.NAME);
        Node activateDeletedAction = NodeUtil.createPath(session.getRootNode(), GoogleSiteMapVersionHandler.GOOGLESITEMAP_APP_BROWSER_ACTIONS + "activateDeleted", NodeTypes.ContentNode.NAME);

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.1"));

        // THEN
        assertThat(addSiteMapAction, hasNode("availability"));
        assertThat(addSiteMapAction.getNode("availability"), hasProperty("writePermissionRequired", true));
        assertThat(deleteAction, hasNode("availability"));
        assertThat(deleteAction.getNode("availability"), hasProperty("writePermissionRequired", true));
        assertThat(editSiteMapAction, hasNode("availability"));
        assertThat(editSiteMapAction.getNode("availability"), hasProperty("writePermissionRequired", true));
        assertThat(activateAction, hasNode("availability"));
        assertThat(activateAction.getNode("availability"), hasProperty("writePermissionRequired", true));
        assertThat(deactivateAction, hasNode("availability"));
        assertThat(deactivateAction.getNode("availability"), hasProperty("writePermissionRequired", true));
        assertThat(activateDeletedAction, hasNode("availability"));
        assertThat(activateDeletedAction.getNode("availability"), hasProperty("writePermissionRequired", true));
    }

    @Test
    public void updateFrom222NewCatalogProperty() throws RepositoryException, ModuleManagementException {
        // GIVEN

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.2"));

        // THEN
        assertTrue(session.propertyExists("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/deactivate/catalog"));
        assertEquals("website", session.getProperty("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/deactivate/catalog").getString());
    }

    @Test
    public void updateFrom232RemovesHiddenProperty() throws Exception {
        // GIVEN
        setupConfigProperty("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap/fields/template", "hidden", "true");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.3.2"));

        // THEN
        assertThat(session.getNode("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap/fields/template"), not(hasProperty("hidden")));
    }

    @Test
    public void updateFrom232ChangesBrokenTemplateOnExistingSites() throws Exception {
        // GIVEN
        final Session siteMapSession = MgnlContext.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE);
        final Node siteMap = NodeUtil.createPath(siteMapSession.getRootNode(), "testSiteMap", SiteMapNodeTypes.SiteMap.NAME);
        siteMap.setProperty(NodeTypes.Renderable.TEMPLATE, "GoogleSiteMap");

        Components.getComponent(VersionManager.class).addVersion(siteMap);

        // WHEN
        assertThat("We want to make sure that the update task is not executed on jcr:system nodes (modifying a property would result in exception)", new Execution() {
            @Override
            public void evaluate() throws Exception {
                executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.3.2"));
            }
        }, throwsNothing());

        // THEN
        assertThat(siteMapSession.getNode("/testSiteMap"), hasProperty(NodeTypes.Renderable.TEMPLATE, "google-sitemap:pages/siteMapsConfiguration"));
    }

    @Test
    public void updateFrom233SetsDefaultType() throws Exception {
        // GIVEN

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.3.3"));

        // THEN
        assertThat(session.getNode("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap/fields/mgnl-googleSiteMapType/options/standard"), hasProperty("selected", "true"));
    }

    @Test
    public void updateFrom233AddsRendererAndUsage() throws Exception {
        // GIVEN
        setupConfigProperty("/modules/google-sitemap/templates/pages/siteMapsConfiguration", "renderType", "freemarker");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.3.3"));

        // THEN
        assertThat(session.getNode("/modules/google-sitemap/renderers/sitemap"), hasProperty("contentType", "application/xml"));
        assertThat(session.getNode("/modules/google-sitemap/templates/pages/siteMapsConfiguration"), hasProperty("renderType", "sitemap"));
    }

    @Test
    public void updateFrom24RemovesInvalidProperties() throws Exception {
        // GIVEN
        Node exportAction = session.getNode("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/export");
        Node editSiteMapAction = session.getNode("/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/editSiteMap");
        Node siteMapPagesField = NodeUtil.createPath(session.getRootNode(), "modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/mgnl:googleSiteMapPages", NodeTypes.ContentNode.NAME);
        exportAction.setProperty("extends", "");
        editSiteMapAction.setProperty("nodeType", "");
        siteMapPagesField.setProperty("chooseOnClick", "");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.4"));
        siteMapPagesField = session.getNode("/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/mgnl-googleSiteMapPages");

        // THEN
        assertThat(exportAction, not(hasProperty("export")));
        assertThat(exportAction, hasProperty("icon"));
        assertThat(editSiteMapAction, not(hasProperty("nodeType")));
        assertThat(siteMapPagesField, not(hasProperty("chooseOnClick")));
        assertThat(siteMapPagesField, hasProperty("name", "mgnl:googleSiteMapPages"));
    }

    @Test
    public void updateFrom24UpdatesURI2RepoMapping() throws Exception {
        // GIVEN
        setupConfigProperty("/server/URI2RepositoryMapping/mappings/sitemaps", "URIPrefix", "/sitemaps");
        // WHEN
        InstallContext ctx = executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.4"));
        // THEN
        Node uriNode = ctx.getJCRSession(RepositoryConstants.CONFIG).getNode("/server/URI2RepositoryMapping/mappings/sitemaps");
        assertThat(uriNode, hasProperty("URIPrefix", "/sitemaps/"));

    }

    @Test
    public void updateFrom242AddEditIcon() throws Exception {
        // GIVEN
        setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/pages");
        setupConfigNode("/modules/google-sitemap/apps/siteMaps/subApps/virtualURI");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.4.2"));

        // THEN
        assertThat(session.getNode("/modules/google-sitemap/apps/siteMaps/subApps/pages"), hasProperty("icon", "icon-edit"));
        assertThat(session.getNode("/modules/google-sitemap/apps/siteMaps/subApps/virtualURI"), hasProperty("icon", "icon-edit"));
    }

    @Test
    public void updateFrom242ChangeVirtualUriMappings() throws Exception {
        // GIVEN
        String path = "/modules/google-sitemap/virtualURIMapping/siteMaps";
        setupConfigNode(path);
        setupConfigProperty(path, "class", "info.magnolia.module.googlesitemap.config.SiteMapVirtualUriMapping");
        setupConfigProperty(path, "prefix", "redirect:/sitemaps");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.4.2"));

        // THEN
        String newPath = "/modules/google-sitemap/virtualUriMappings/siteMaps";
        assertThat(session.getNode(newPath), hasProperty("class", "info.magnolia.module.googlesitemap.config.mapping.SiteMapVirtualUriMapping"));
        assertThat(session.getNode(newPath), hasProperty("prefix", "redirect:/sitemaps"));
    }

    @Test
    public void freshInstallToWithNewVirtualUriMappings() throws Exception {
        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        String newPath = "/modules/google-sitemap/virtualUriMappings/siteMaps";
        assertThat(session.getNode(newPath), hasProperty("class", "info.magnolia.module.googlesitemap.config.mapping.SiteMapVirtualUriMapping"));
        assertThat(session.getNode(newPath), hasProperty("prefix", "redirect:/sitemaps"));
    }

    @Test
    public void updateFrom25UpdatesIconValue() throws Exception {
        // GIVEN
        final Node jcrToolsNode = NodeUtil.createPath(session.getRootNode(), "/modules/google-sitemap/apps/siteMaps", NodeTypes.ContentNode.NAME);
        jcrToolsNode.setProperty("icon", "icon-app");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.5"));

        // THEN
        assertThat(jcrToolsNode, hasProperty("icon", "icon-sitemaps-app"));
    }
}
