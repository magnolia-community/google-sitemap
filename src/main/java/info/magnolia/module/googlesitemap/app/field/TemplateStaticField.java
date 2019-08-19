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

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.Label;

/**
 * Simple custom field displaying a string value as a label.
 */
public class TemplateStaticField extends CustomField<String> {

    private Label label;

    public TemplateStaticField(String stringLabel) {
        label = new Label(stringLabel);
        label.setContentMode(ContentMode.HTML);
        setImmediate(true);
        setVisible(false);
    }

    @Override
    protected Component initContent() {
        return label;
    }

    @Override
    protected void setInternalValue(String newValue) {
        label.setValue(newValue);
    }

    @Override
    public String getInternalValue() {
        return label.getValue();
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        newDataSource.setValue(getValue());
        super.setPropertyDataSource(newDataSource);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

}

