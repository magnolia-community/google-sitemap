/**
 * This file Copyright (c) 2012-2018 Magnolia International
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

import info.magnolia.cms.security.Permission;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddPermissionTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleModuleResource;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.BootstrapSingleResourceAndOrderBefore;
import info.magnolia.module.delta.CheckAndModifyPartOfPropertyValueTask;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.CheckOrCreatePropertyTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.NewPropertyTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.NodeVisitorTask;
import info.magnolia.module.delta.OrderNodeAfterTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.PropertyExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RemovePropertiesTask;
import info.magnolia.module.delta.RemovePropertyTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.SetupModuleRepositoriesTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.app.field.transformer.SiteMapTransformer;
import info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.formatter.FolderNameColumnFormatter;
import info.magnolia.module.googlesitemap.config.mapping.SiteMapVirtualUriMapping;
import info.magnolia.module.googlesitemap.setup.for2_1.Register21NodeTypeTask;
import info.magnolia.module.googlesitemap.setup.for2_1.RenameSiteMapeSitePagesPropertyName;
import info.magnolia.module.googlesitemap.setup.for2_1.UpdatePropertyNamesAndNodeStructure;
import info.magnolia.module.googlesitemap.setup.migration.Removei18nKeysInSiteMapMigrationTask;
import info.magnolia.module.googlesitemap.setup.migration.SiteMapDefinitionMigrationTask;
import info.magnolia.module.googlesitemap.setup.migration.SiteMapDialogMigrationTask;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.contentapp.ContentApp;
import info.magnolia.ui.contentapp.setup.for5_3.ContentAppMigrationTask;
import info.magnolia.ui.framework.setup.AddIsPublishedRuleToAllDeactivateActionsTask;
import info.magnolia.ui.framework.setup.SetWritePermissionForActionsTask;
import info.magnolia.virtualuri.setup.delta.VirtualUriMappingUpdateTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Default version handler.
 */
public class GoogleSiteMapVersionHandler extends DefaultModuleVersionHandler {

    protected static final String GOOGLESITEMAP_APP_BROWSER_ACTIONS = "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/";
    protected static final String GOOGLESITEMAP_APP_PAGES_ACTIONS = "/modules/google-sitemap/apps/siteMaps/subApps/pages/actions/";
    protected static final String GOOGLESITEMAP_APP_VIRTUALURI_ACTIONS = "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI/actions/";

    public GoogleSiteMapVersionHandler() {
        register(DeltaBuilder.update("1.2.1", "")
                .addTask(new RemoveNodeTask("Obsolete folder: templates", "Templates folder has to be removed", "config", "/modules/google-sitemap/templates"))
                .addTask(new RemoveNodeTask("Obsolete folder: dialog", "Dialogs folder has to be removed", "config", "/modules/google-sitemap/dialogs"))
                .addTask(new RemoveNodeTask("Obsolete SiteMap site sample definition", "", "website", "google-sitemap"))
                .addTask(new BootstrapSingleModuleResource("SiteMap new Config", "Setup Config", "config.modules.google-sitemap.config.xml"))
                .addTask(new BootstrapSingleModuleResource("SiteMap new Template", "Setup Template", "config.modules.google-sitemap.templates.xml")));

        register(DeltaBuilder.update("2.0", "")
                .addTask(new SiteMapDefinitionMigrationTask("Migrate M4.5 site map definition into the new App", "", RepositoryConstants.WEBSITE, "/"))
                .addTask(new SiteMapDialogMigrationTask("Migrate M4.5 dialog definition", "Create the new M5 dialog definition. Add actions, tabs and fields definition", "google-sitemap"))
                .addTask(new BootstrapSingleResourceAndOrderBefore("Bootstrap tab", "Bootstrap  site map properties tab",
                        "/mgnl-bootstrap/google-sitemap/dialogs/config.modules.google-sitemap.dialogs.components.content.siteComponentTab.form.tabs.siteMap.xml", "tabSites"))
                .addTask(new BootstrapSingleModuleResource("SiteMap app", "Add SiteMap app", "app/config.modules.google-sitemap.apps.xml"))
                .addTask(new BootstrapSingleModuleResource("SiteMap app App Launcher", "Add SiteMap app to App Launcher", "app/config.modules.ui-admincentral.config.appLauncherLayout.groups.manage.apps.siteMaps.xml"))
                .addTask(new BootstrapSingleModuleResource("Field Types", "Install new field types", "fieldtypes/config.modules.google-sitemap.fieldTypes.xml"))
                .addTask(new BootstrapSingleModuleResource("Repository Mapping", "Add Google SiteMap worksapce mapping", "config/config.server.URI2RepositoryMapping.mappings.sitemaps.xml"))
                .addTask(new BootstrapSingleResource("Virtual URI Mapping", "Add Google SiteMap virtual URI mapping", "/mgnl-bootstrap-prior-2_5/config.modules.google-sitemap.virtualURIMapping.xml"))
                .addTask(new SetupModuleRepositoriesTask())
                .addTask(new AddPermissionTask("Anonymous permissions for the new workspace", "", "anonymous", "googleSitemaps", "/", Permission.READ, true))

                .addTask(new NewPropertyTask("Add default value transformer", "Set a transformer that display the default field value in case the stored value is null", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/generic/controls/googleSiteMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapPriority", "transformerClass", SiteMapTransformer.class.getName()))
                .addTask(new NewPropertyTask("Add default value transformer", "Set a transformer that display the default field value in case the stored value is null", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/generic/controls/googleSiteMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapChangefreq", "transformerClass", SiteMapTransformer.class.getName()))
                .addTask(new NewPropertyTask("Add default value transformer", "Set a transformer that display the default field value in case the stored value is null", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/generic/controls/googleVirtualUriMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapPriority", "transformerClass", SiteMapTransformer.class.getName()))
                .addTask(new NewPropertyTask("Add default value transformer", "Set a transformer that display the default field value in case the stored value is null", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/generic/controls/googleVirtualUriMapTab/form/tabs/tabGoogleSitemapProps/fields/googleSitemapChangefreq", "transformerClass", SiteMapTransformer.class.getName()))

                .addTask(new NodeExistsDelegateTask("Remove area definition from the pages templates", "Remove areas definition in /modules/google-sitemap/templates/pages/siteMapsConfiguration/areas", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/pages/siteMapsConfiguration/areas",
                        new RemoveNodeTask("", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/pages/siteMapsConfiguration/areas")))
                .addTask(new NodeExistsDelegateTask("Remove components definition from templates", "Remove areas definition in /modules/google-sitemap/templates/components", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/components",
                        new RemoveNodeTask("", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/components")))

                .addTask(new Removei18nKeysInSiteMapMigrationTask("google-sitemap", "Remove M4.5 i18n support"))
                .addTask(new NodeExistsDelegateTask("Reorder Sitemaps in MANAGE group", "This reorders the Sitemaps app before Configuration in the Manage group of the applauncher.", RepositoryConstants.CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps/configuration",
                        new OrderNodeBeforeTask("", "", RepositoryConstants.CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps/siteMaps", "configuration")))
                .addTask(new CheckAndModifyPropertyValueTask("Set Page template not visible", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/pages/siteMapsConfiguration", "visible", "true", "false")));

        register(DeltaBuilder.update("2.0.1", "")
                .addTask(new RemoveNodeTask("Remove dialog", "Remove obsolete dialog.", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/pages"))
                .addTask(new ArrayDelegateTask("Rename property", "Rename property [workspace] to [targetWorkspace].",
                        new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/sites/field", "workspace"),
                        new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/sites/field", "targetWorkspace", RepositoryConstants.WEBSITE)))
                .addTask(new RemovePropertyTask("Remove unused property", "Remove unused property called identifier.", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/sites/field", "identifier"))
                .addTask(new RemovePropertyTask("Remove unused property", "Remove unused property called dialog.", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/pages/siteMapsConfiguration", "dialog"))
                .addTask(new RemovePropertyTask("Remove unused property", "Remove unused property called sitemapType.", RepositoryConstants.CONFIG, "/modules/google-sitemap/templates/pages/siteMapsConfiguration", "sitemapType"))
                );
        register(DeltaBuilder.update("2.1", "")
                .addTask(new ContentAppMigrationTask("/modules/google-sitemap")));

        register(DeltaBuilder.update("2.2", "")
                .addTask(new Register21NodeTypeTask("Register new nodeType definition", "", GoogleSiteMapConfiguration.WORKSPACE, Arrays.asList(NodeTypes.Page.NAME)))
                .addTask(new RenameSiteMapeSitePagesPropertyName("Rename Sitemap Website properties", "", RepositoryConstants.WEBSITE))
                .addTask(new UpdatePropertyNamesAndNodeStructure("Rename Sitemap properties", ""))
                .addTask(new NodeExistsDelegateTask("Bootstrap new siteMap dialogs definition", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap",
                        new ArrayDelegateTask("",
                                new RemoveNodeTask("", "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap"),
                                new BootstrapSingleModuleResource("", "", "dialogs/config.modules.google-sitemap.dialogs.components.content.siteComponentTab.form.tabs.siteMap.xml")
                                )))
                .addTask(new NodeExistsDelegateTask("Bootstrap new tabSites dialogs definition", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites",
                        new ArrayDelegateTask("",
                                new RemoveNodeTask("", "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites"),
                                new BootstrapSingleModuleResource("", "", "dialogs/config.modules.google-sitemap.dialogs.components.content.siteComponentTab.form.tabs.tabSites.xml")
                                )))
                .addTask(new NodeExistsDelegateTask("Bootstrap new generic dialogs definition", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/generic",
                        new ArrayDelegateTask("",
                                new RemoveNodeTask("", "/modules/google-sitemap/dialogs/generic"),
                                new BootstrapSingleModuleResource("", "", "dialogs/config.modules.google-sitemap.dialogs.generic.xml")
                                )))
                .addTask(new BootstrapSingleModuleResource("Bootstrap new tabDefaultValue dialogs definition", "", "dialogs/config.modules.google-sitemap.dialogs.components.content.siteComponentTab.form.tabs.tabDefaultValue.xml"))
                .addTask(new OrderNodeAfterTask("order tabs", "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites", "siteMap"))
                .addTask(new OrderNodeAfterTask("order tabs", "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabDefaultValue", "tabSites"))
                .addTask(new CheckAndModifyPropertyValueTask("/modules/google-sitemap/apps/siteMaps/subApps/browser/workbench/contentViews/list/columns/name", "propertyName", "displayName", "mgnl:googleSiteMapDisplayName"))
                .addTask(new CheckAndModifyPropertyValueTask("/modules/google-sitemap/apps/siteMaps/subApps/browser/workbench/contentViews/list/columns/name", "formatterClass", "info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.formatter.FolderNameColumnFormatter", FolderNameColumnFormatter.class.getName()))
                .addTask(new CheckAndModifyPropertyValueTask("/modules/google-sitemap/apps/siteMaps", "appClass", "info.magnolia.module.googlesitemap.app.GoogleSiteMapApp", ContentApp.class.getName()))

                .addTask(new NodeExistsDelegateTask("Bootstrap new sub app Pages definition", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/pages",
                        new ArrayDelegateTask("",
                                new RemoveNodeTask("", "/modules/google-sitemap/apps/siteMaps/subApps/pages"),
                                new PartialBootstrapTask("", "/mgnl-bootstrap/google-sitemap/app/config.modules.google-sitemap.apps.xml", "/apps/siteMaps/subApps/pages"),
                                new OrderNodeAfterTask("order tabs", "/modules/google-sitemap/apps/siteMaps/subApps/pages", "browser")
                                )))

                .addTask(new NodeExistsDelegateTask("Bootstrap new sub app VirtualUri definition", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI",
                        new ArrayDelegateTask("",
                                new RemoveNodeTask("", "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI"),
                                new PartialBootstrapTask("", "/mgnl-bootstrap/google-sitemap/app/config.modules.google-sitemap.apps.xml", "/apps/siteMaps/subApps/virtualURI"),
                                new OrderNodeAfterTask("order tabs", "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI", "pages")
                                )))
                .addTask(new SetWritePermissionForActionsTask(GOOGLESITEMAP_APP_BROWSER_ACTIONS, new String[]{"addFolder", "editFolder", "addSiteMap", "delete", "editSiteMap", "import", "restorePreviousVersion", "confirmDeleteSiteMap", "editSiteEntries", "editVirtualUris", "activate", "deactivate", "activateDeleted"}))
                .addTask(new SetWritePermissionForActionsTask(GOOGLESITEMAP_APP_PAGES_ACTIONS, new String[]{"editSitemapEntry"}))
                .addTask(new SetWritePermissionForActionsTask(GOOGLESITEMAP_APP_VIRTUALURI_ACTIONS, new String[]{"editSitemapEntry"}))
                .addTask(new BootstrapSingleModuleResource("Bootstrap new config values", "", "config.modules.google-sitemap.config.xml"))
                .addTask(new PartialBootstrapTask("", "/mgnl-bootstrap/google-sitemap/fieldtypes/config.modules.google-sitemap.fieldTypes.xml", "/fieldTypes/siteMapSelect")));

        register(DeltaBuilder.update("2.2.2", "")
                .addTask(new AddIsPublishedRuleToAllDeactivateActionsTask("", "/modules/google-sitemap/apps/"))
                );

        register(DeltaBuilder.update("2.2.3", "")
                .addTask(new PropertyExistsDelegateTask("Add property catalog to deactivate action if not existing", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/deactivate", "catalog", null,
                        new NewPropertyTask("", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/deactivate", "catalog", "website")))
                );

        register(DeltaBuilder.update("2.3.3", "")
                .addTask(new RemovePropertyTask("Remove unnecessary 'hidden' property", "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/siteMap/fields/template", "hidden"))
                .addTask(new NodeVisitorTask("Update all broken 'mgnl:template' properties",
                        "Sitemaps with property 'mgnl:template' set to 'GoogleSiteMap' will be updated to 'google-sitemap:pages/siteMapsConfiguration'",
                        GoogleSiteMapConfiguration.WORKSPACE, "/") {
                    @Override
                    protected boolean nodeMatches(Node node) {
                        return !StringUtils.startsWith(NodeUtil.getPathIfPossible(node), "/jcr:system") &&
                                StringUtils.equals(PropertyUtil.getString(node, NodeTypes.Renderable.TEMPLATE), "GoogleSiteMap");
                    }

                    @Override
                    protected void operateOnNode(InstallContext installContext, Node node) {
                        try {
                            node.setProperty(NodeTypes.Renderable.TEMPLATE, "google-sitemap:pages/siteMapsConfiguration");
                        } catch (RepositoryException e) {
                            installContext.error(String.format("Error updating '%s' property of node '%s'", NodeTypes.Renderable.TEMPLATE, NodeUtil.getPathIfPossible(node)), e);
                        }
                    }
                })
                );

        register(DeltaBuilder.update("2.3.4", "")
                .addTask(new BootstrapSingleModuleResource("Sitemap dialog main tab", "Make 'standard' the default sitemap type",
                        "dialogs/config.modules.google-sitemap.dialogs.components.content.siteComponentTab.form.tabs.siteMap.xml"))
                .addTask(new BootstrapSingleModuleResource("Add sitemap renderer", "This one sets the correct content type 'application/xml'", "config.modules.google-sitemap.renderers.sitemap.xml"))
                .addTask(new CheckAndModifyPropertyValueTask("Update siteMap template to use sitemap renderer", "This will make sure the correct content type is set", "config", "/modules/google-sitemap/templates/pages/siteMapsConfiguration", "renderType", "freemarker", "sitemap"))
                );

        register(DeltaBuilder.update("2.4.1", "")
                .addTask(new ArrayDelegateTask("Remove legacy properties",
                        new RemovePropertiesTask("", RepositoryConstants.CONFIG, Arrays.asList(
                                "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/export/extends",
                                "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/editSiteMap/nodeType",
                                "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs/tabSites/fields/mgnl:googleSiteMapPages/chooseOnClick"), false),
                        new CheckOrCreatePropertyTask("", "/modules/google-sitemap/apps/siteMaps/subApps/browser/actions/export", "icon", "icon-export"),
                        // replace colons with dashes in fields' node names and add new name property instead.
                        new NodeVisitorTask("", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/dialogs/components/content/siteComponentTab/form/tabs") {
                    @Override
                    protected boolean nodeMatches(Node node) {
                        return StringUtils.startsWith(NodeUtil.getName(node), "mgnl:");
                    }

                    @Override
                    protected void operateOnNode(InstallContext installContext, Node node) {
                        try {
                            node.setProperty("name", node.getName());
                            NodeUtil.renameNode(node, node.getName().replaceAll(":", "-"));
                        } catch (RepositoryException e) {
                            installContext.error(String.format("Error updating '%s' property of node '%s'", NodeTypes.Renderable.TEMPLATE, NodeUtil.getPathIfPossible(node)), e);
                        }
                    }
                }))
                );
        register(DeltaBuilder.update("2.4.2", "")
                .addTask(new NodeExistsDelegateTask("Change URI to repository mapping to end with slash", "", RepositoryConstants.CONFIG, "/server/URI2RepositoryMapping/mappings/sitemaps",
                        new CheckAndModifyPartOfPropertyValueTask("", "", RepositoryConstants.CONFIG, "/server/URI2RepositoryMapping/mappings/sitemaps", "URIPrefix", "/sitemaps", "/sitemaps/"))));
        register(DeltaBuilder.update("2.4.3", "")
                .addTask(new NodeExistsDelegateTask("Show edit icon on tab of pages sub app", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/pages",
                        new SetPropertyTask("Set icon for pages sub app", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/pages", "icon", "icon-edit")))
                .addTask(new NodeExistsDelegateTask("Show edit icon on tab of virtual uri sub app", "", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI",
                        new SetPropertyTask("Set icon for virtual uri sub app", RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps/subApps/virtualURI", "icon", "icon-edit"))));
        register(DeltaBuilder.update("2.5", "")
                .addTask(new VirtualUriMappingUpdateTask("Update virtualUriMappings", "google-sitemap", ImmutableMap.of(
                        "info.magnolia.module.googlesitemap.config.SiteMapVirtualUriMapping", SiteMapVirtualUriMapping.class))));
        register(DeltaBuilder.update("2.5.1", "")
                .addTask(new RemovePropertyTask("Remove deprecated i18nBasename property", "/modules/google-sitemap/templates/pages/siteMapsConfiguration", "i18nBasename"))
        );

        register(DeltaBuilder.update("2.6", "")
                .addTask(new CheckAndModifyPropertyValueTask("Update icon property to new value", "",
                        RepositoryConstants.CONFIG, "/modules/google-sitemap/apps/siteMaps", "icon",
                        "icon-app", "icon-sitemaps-app")
                )
        );
    }

    /**
     * Export the FTL to the Resource folder menu from the Admin central.
     */
    @Override
    protected List<Task> getExtraInstallTasks(InstallContext ctx) {
        final List<Task> tasks = new ArrayList<Task>();
        tasks.add(new AddPermissionTask("Anonymous permissions for the new workspace", "", "anonymous", "googleSitemaps", "/", Permission.READ, true));
        tasks.add(new NodeExistsDelegateTask("Reorder Sitemaps in MANAGE group", "This reorders the Sitemaps app before Configuration in the Manage group of the applauncher.", RepositoryConstants.CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps/configuration",
                new OrderNodeBeforeTask("", "", RepositoryConstants.CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps/siteMaps", "configuration")));
        tasks.add(new Register21NodeTypeTask("Register new nodeType definition", "", GoogleSiteMapConfiguration.WORKSPACE, Arrays.asList(NodeTypes.Page.NAME)));
        return tasks;
    }
}
