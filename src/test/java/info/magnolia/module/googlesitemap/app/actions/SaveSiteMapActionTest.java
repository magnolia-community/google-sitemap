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
package info.magnolia.module.googlesitemap.app.actions;

import static org.junit.Assert.*;

import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.SiteMapTestUtil;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.ObjectProperty;

/**
 * Test class for {@link SaveSiteMapAction}.
 */
public class SaveSiteMapActionTest extends SiteMapTestUtil {

    private GoogleMapEditorCallbackTest callback;
    private GoogleMapEditorValidatorTest validator;
    private SaveSiteMapActionDefinition definition;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        callback = new GoogleMapEditorCallbackTest();
        validator = new GoogleMapEditorValidatorTest();
        definition = new SaveSiteMapActionDefinition();
        definition.setName("save");
        definition.setLabel("label");
    }

    @Test
    public void executeSaveNewSiteMap() throws RepositoryException, ActionExecutionException {
        // GIVEN
        Node rootNode = googleSiteMapSession.getRootNode();
        JcrNewNodeAdapter item = new JcrNewNodeAdapter(rootNode, SiteMapNodeTypes.SiteMap.NAME);
        item.addItemProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, new ObjectProperty<String>("displayName"));
        SaveSiteMapAction action = new SaveSiteMapAction(definition, item, validator, callback);

        // WHEN
        action.execute();

        // THEN
        assertEquals("onSuccess(save)", this.callback.getCallbackActionCalled());
        assertTrue(googleSiteMapSession.getRootNode().hasNode("displayName"));
    }

    @Test
    public void executeSaveExistingSiteMap() throws RepositoryException, ActionExecutionException {
        // GIVEN
        Node rootNode = googleSiteMapSession.getRootNode();
        Node displayName = rootNode.addNode("display Name", SiteMapNodeTypes.SiteMap.NAME);
        displayName.setProperty(SiteMapNodeTypes.SiteMap.URL, "initial_url");
        displayName.setProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, "display Name");
        JcrNodeAdapter item = new JcrNodeAdapter(displayName);
        item.addItemProperty(SiteMapNodeTypes.SiteMap.URL, new ObjectProperty<String>("changed_url"));
        SaveSiteMapAction action = new SaveSiteMapAction(definition, item, validator, callback);

        // WHEN
        action.execute();

        // THEN
        assertEquals("onSuccess(save)", this.callback.getCallbackActionCalled());
        assertTrue(googleSiteMapSession.getRootNode().hasNode("display Name"));
        assertEquals("changed_url", displayName.getProperty(SiteMapNodeTypes.SiteMap.URL).getString());
    }

    @Test
    public void executeSaveExistingSiteMapWithNewName() throws RepositoryException, ActionExecutionException {
        // GIVEN
        Node rootNode = googleSiteMapSession.getRootNode();
        Node displayName = rootNode.addNode("displayName", SiteMapNodeTypes.SiteMap.NAME);
        displayName.setProperty(SiteMapNodeTypes.SiteMap.URL, "initial_url");
        displayName.setProperty(SiteMapNodeTypes.SiteMap.TYPE, "initial_type");
        JcrNodeAdapter item = new JcrNodeAdapter(displayName);
        item.addItemProperty(SiteMapNodeTypes.SiteMap.URL, new ObjectProperty<String>("changed_url"));
        item.addItemProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, new ObjectProperty<String>("new_displayName"));
        SaveSiteMapAction action = new SaveSiteMapAction(definition, item, validator, callback);

        // WHEN
        action.execute();

        // THEN
        assertEquals("onSuccess(save)", this.callback.getCallbackActionCalled());
        assertFalse(googleSiteMapSession.getRootNode().hasNode("displayName"));
        assertTrue(googleSiteMapSession.getRootNode().hasNode("new_displayName"));
        assertEquals("changed_url", displayName.getProperty(SiteMapNodeTypes.SiteMap.URL).getString());
        assertEquals("initial_type", displayName.getProperty(SiteMapNodeTypes.SiteMap.TYPE).getString());
    }

    @Test
    public void executeSaveExistingSiteMapWithExistingNewName() throws RepositoryException, ActionExecutionException {
        // GIVEN
        Node rootNode = googleSiteMapSession.getRootNode();
        Node displayName = rootNode.addNode("displayName", SiteMapNodeTypes.SiteMap.NAME);
        Node existingDisplayName = rootNode.addNode("new_displayName", SiteMapNodeTypes.SiteMap.NAME);
        displayName.setProperty(SiteMapNodeTypes.SiteMap.URL, "initial_url");
        displayName.setProperty(SiteMapNodeTypes.SiteMap.TYPE, "initial_type");
        JcrNodeAdapter item = new JcrNodeAdapter(displayName);
        item.addItemProperty(SiteMapNodeTypes.SiteMap.URL, new ObjectProperty<String>("changed_url"));
        item.addItemProperty(SiteMapNodeTypes.SiteMap.DISPLAY_NAME, new ObjectProperty<String>("new_displayName"));
        SaveSiteMapAction action = new SaveSiteMapAction(definition, item, validator, callback);

        // WHEN
        action.execute();

        // THEN
        assertEquals("onSuccess(save)", this.callback.getCallbackActionCalled());
        assertFalse(googleSiteMapSession.getRootNode().hasNode("displayName"));
        assertFalse(existingDisplayName.hasProperty(SiteMapNodeTypes.SiteMap.URL));
        assertTrue(googleSiteMapSession.getRootNode().hasNode("new_displayName0"));
        assertEquals("new_displayName0", displayName.getName());
        assertEquals("changed_url", displayName.getProperty(SiteMapNodeTypes.SiteMap.URL).getString());
        assertEquals("initial_type", displayName.getProperty(SiteMapNodeTypes.SiteMap.TYPE).getString());
    }
}
