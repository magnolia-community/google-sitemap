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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.virtualuris;

import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.ui.vaadin.grid.MagnoliaTable;
import info.magnolia.ui.workbench.list.ListViewImpl;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

/**
 * Implementation of {@link SitemapListView}.
 */
public class SitemapListViewImpl extends ListViewImpl implements SitemapListView {

    @Override
    protected Table createTable(Container container) {
        MagnoliaTable table = new MagnoliaTable() {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
                if (SiteMapEntry.SITE_ALERT_NAME.equals(colId)) {
                    StringBuilder sb = new StringBuilder("<span class=\"").
                            append(!(Boolean) property.getValue() ? "icon-tick" : "icon-close").
                            append("\"></span>");
                    return sb.toString();
                }

                Object value = property.getValue();
                if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
                    return "-";
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        table.setContainerDataSource(container);
        return table;
    }

    @Override
    public void setColumnAlignment(Object columnId, Table.Align alignment) {
        ((Table)asVaadinComponent()).setColumnAlignment(columnId, alignment);
    }
}
