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
package info.magnolia.module.googlesitemap.app.field;

import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

import javax.inject.Inject;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Field;

/**
 * Factory class for {@link TemplateStaticField}.
 * @param <T> exact definition type.
 */
public class TemplateStaticFieldFactory<T extends TemplateStaticFieldDefinition> extends AbstractFieldFactory<T, String> {

    @Inject
    public TemplateStaticFieldFactory(T definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    /**
     * @deprecated since 2.3.3 - use {@link TemplateStaticFieldFactory#TemplateStaticFieldFactory(info.magnolia.module.googlesitemap.app.field.TemplateStaticFieldDefinition, com.vaadin.data.Item)} instead.
     */
    @Deprecated
    public TemplateStaticFieldFactory(T definition, Item relatedFieldItem, TemplateDefinitionRegistry templateDefinitionRegistry) {
        super(definition, relatedFieldItem);
    }

    @Override
    protected Field createFieldComponent() {
        return new TemplateStaticField(definition.getValue());
    }

    /**
     * Set datasource for template static field.
     */
    @Override
    public void setPropertyDataSourceAndDefaultValue(Property property) {
        this.field.setPropertyDataSource(property);
    }
}
