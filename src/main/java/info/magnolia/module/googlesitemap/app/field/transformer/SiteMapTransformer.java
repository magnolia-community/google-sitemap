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
package info.magnolia.module.googlesitemap.app.field.transformer;

import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;
import info.magnolia.ui.vaadin.integration.jcr.DefaultPropertyUtil;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

/**
 * SiteMap specific implementation of {@link BasicTransformer}.<br>
 * Always display the field default value (defined in {@link SiteMapService}) if the related field property has an empty value.<br>
 * Used to define default value for:<br>
 * - googleSitemapChangefreq
 * - googleSitemapPriority
 * 
 * @param <T>
 * @deprecated Since 2.1 version, as the detail dialog only uses {@link info.magnolia.module.googlesitemap.bean.SiteMapEntry} this transformer is no more needed.
 */
@Deprecated
public class SiteMapTransformer<T> extends BasicTransformer<T> {

    private static final Logger log = LoggerFactory.getLogger(SiteMapTransformer.class);
    private static HashMap<String, String> propertyToHandle;
    static {
        propertyToHandle = new HashMap<String, String>();
        propertyToHandle.put(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_CHANGEFREQ, GoogleSiteMapConfiguration.DEFAULT_CHANGE_FREQUENCY);
        propertyToHandle.put(SiteMapNodeTypes.GoogleSiteMap.SITEMAP_PRIORITY, Double.toString(GoogleSiteMapConfiguration.DEFAULT_PRIORITY));
    }

    public SiteMapTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<T> type) {
        super(relatedFormItem, definition, type);
    }

    @Override
    protected <T> Property<T> getOrCreateProperty(Class<T> type) {
        Property<T> property = super.getOrCreateProperty(type);
        if (property.getValue() == null && propertyToHandle.containsKey(definition.getName())) {
            try {
                property.setValue((T) DefaultPropertyUtil.createTypedValue(property.getType(), propertyToHandle.get(definition.getName())));
            } catch (Exception e) {
                log.warn("Could not create a default value of type '{}' based on '{}'. Null stay as default value");
            }
        }
        return property;
    }
}
