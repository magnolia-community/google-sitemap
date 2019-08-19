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
package info.magnolia.module.googlesitemap.app.field;

import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.DefaultPropertyUtil;

import com.google.inject.Inject;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

/**
 * Specific implementation of {@link SelectFieldFactory} in order to set the default values coming from the {@link GoogleSiteMapConfiguration}.
 */
public class GoogleSiteMapSelectFieldFactory extends SelectFieldFactory<GoogleSiteMapSelectFieldFactoryDefinition> {

    private final GoogleSiteMapConfiguration configuration;

    @Inject
    public GoogleSiteMapSelectFieldFactory(GoogleSiteMapSelectFieldFactoryDefinition definition, Item relatedFieldItem, GoogleSiteMapConfiguration configuration) {
        super(definition, relatedFieldItem);
        this.configuration = configuration;
    }

    @Override
    protected Object createDefaultValue(Property dataSource) {
        if (SiteMapNodeTypes.SiteMap.DEFAULT_CHANGEFREQ.equals(definition.getName())) {
            return DefaultPropertyUtil.createTypedValue(String.class, configuration.getChangeFrequency());
        }

        if (SiteMapNodeTypes.SiteMap.DEFAULT_PRIORITY.equals(definition.getName())) {
            return DefaultPropertyUtil.createTypedValue(Double.class, Double.toString(configuration.getPriority()));
        }

        return super.createDefaultValue(dataSource);
    }
}
