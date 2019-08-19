/**
 * This file Copyright (c) 2016-2018 Magnolia International
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
package info.magnolia.module.googlesitemap.app.field;

//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.mock;
//
//import info.magnolia.cms.i18n.DefaultI18nContentSupport;
//import info.magnolia.cms.i18n.I18nContentSupport;
//import info.magnolia.context.Context;
//import info.magnolia.context.MgnlContext;
//import info.magnolia.jcr.util.NodeTypes;
//import info.magnolia.test.ComponentsTestUtil;
//import info.magnolia.test.mock.MockWebContext;
//import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
//import info.magnolia.ui.framework.i18n.DefaultI18NAuthoringSupport;
//
//import java.util.Locale;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.vaadin.v7.data.Item;


public class TemplateStaticFieldFactoryTest {

//    private TemplateStaticFieldDefinition templateStaticFieldDefinition;
//    private TemplateStaticFieldFactory templateStaticFieldFactory;
//
//    @Before
//    public void setUp() throws Exception {
//        final Context context = new MockWebContext();
//        MgnlContext.setInstance(context);
//
//        ComponentsTestUtil.setImplementation(I18nContentSupport.class, DefaultI18nContentSupport.class);
//        ComponentsTestUtil.setImplementation(I18NAuthoringSupport.class, DefaultI18NAuthoringSupport.class);
//
//        templateStaticFieldDefinition = new TemplateStaticFieldDefinition();
//        templateStaticFieldFactory = new TemplateStaticFieldFactory(templateStaticFieldDefinition, mock(Item.class));
//    }
//
//    @After
//    public void tearDown() {
//        MgnlContext.setInstance(null);
//        ComponentsTestUtil.clear();
//    }
//
//    @Test
//    public void basicFieldTestThatShowsDefect() {
//        // GIVEN
//        final String templateId = "google-sitemap:pages/siteMapsConfiguration";
//        final String templateName = "GoogleSiteMap";
//        templateStaticFieldDefinition.setName(NodeTypes.Renderable.NAME);
//        templateStaticFieldDefinition.setValue(templateId);
//
//        // WHEN
//        TemplateStaticField field = (TemplateStaticField) templateStaticFieldFactory.createFieldComponent();
//
//        assertThat(field.getInternalValue(), is(templateId));
//
//        // THEN
//        field.setLocale(Locale.FRANCE); // Setting the locale used to cause the internal value to be set to the presentation value (internal value comparison)
//
//
//        assertThat(field.getInternalValue(), allOf(is(templateId), not(is(templateName))));
//    }

}