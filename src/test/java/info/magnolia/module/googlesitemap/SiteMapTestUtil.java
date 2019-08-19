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
package info.magnolia.module.googlesitemap;

import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryManager;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Site map utility test class.
 */
public class SiteMapTestUtil extends RepositoryTestCase {

    private static final Logger log = LoggerFactory.getLogger(SiteMapTestUtil.class);

    protected Session googleSiteMapSession;
    private String siteMapNodeTypeConfigFile = "test-google-sitemap-nodetypes.xml";
    private String siteMapRepositoryConfigFile = "test-siteMap-repositories.xml";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        googleSiteMapSession = MgnlContext.getJCRSession(GoogleSiteMapConfiguration.WORKSPACE);

        // Register Google SiteMap node type
        RepositoryManager repositoryManager = Components.getComponent(RepositoryManager.class);
        try {
            repositoryManager.getRepositoryProvider("magnolia").registerNodeTypes(ClasspathResourcesUtil.getResource(siteMapNodeTypeConfigFile).openStream());
        } catch (RepositoryException e) {
            log.error("", e);
        }
    }

    @Override
    protected String getRepositoryConfigFileName() {
        setRepositoryConfigFileName(siteMapRepositoryConfigFile);
        return siteMapRepositoryConfigFile;
    }


    /**
     * Test implementation of {@link EditorCallback}.
     */
    public static class GoogleMapEditorCallbackTest implements EditorCallback {

        private String callbackActionCalled;

        public String getCallbackActionCalled() {
            return callbackActionCalled;
        }

        public EditorCallback getCallback() {
            return new EditorCallback() {

                @Override
                public void onSuccess(String actionName) {
                    callbackActionCalled = "onSuccess(" + actionName + ")";
                }

                @Override
                public void onCancel() {
                    callbackActionCalled = "onCancel()";
                }
            };
        }

        @Override
        public void onSuccess(String actionName) {
            callbackActionCalled = "onSuccess(" + actionName + ")";
        }

        @Override
        public void onCancel() {
            callbackActionCalled = "onCancel()";
        }
    }

    /**
     * Test implementation of {@link EditorValidator}.
     */
    public static class GoogleMapEditorValidatorTest implements EditorValidator {

        @Override
        public void showValidation(boolean visible) {
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }
}
