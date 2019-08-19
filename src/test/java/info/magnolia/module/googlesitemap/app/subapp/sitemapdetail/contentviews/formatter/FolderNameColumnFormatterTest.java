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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.contentviews.formatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;
import info.magnolia.util.EscapeUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

/**
 * Tests for {@link FolderNameColumnFormatter}.
 */
public class FolderNameColumnFormatterTest {

    private final String columnId = "displayName";

    private Table table;
    private Item item;
    private Object itemId;

    private PropertyColumnDefinition propertyColumnDefinition;
    private FolderNameColumnFormatter folderNameColumnFormatter;

    @Before
    public void setIp() {
        table = mock(Table.class);
        item = mock(Item.class);
        itemId = item;

        propertyColumnDefinition = new PropertyColumnDefinition();
        folderNameColumnFormatter = new FolderNameColumnFormatter(propertyColumnDefinition);
    }

    @Test
    public void testGenerateCellWhenFolder() throws RepositoryException {
        // GIVEN
        final String nodeName = "nodeName";

        JcrNodeAdapter nodeAdapter = mock(JcrNodeAdapter.class);
        Node node = new MockNode(nodeName, NodeTypes.Folder.NAME);

        Mockito.when(nodeAdapter.getJcrItem()).thenReturn(node);
        Mockito.when(table.getItem(itemId)).thenReturn(nodeAdapter);

        // WHEN
        Object cell = folderNameColumnFormatter.generateCell(table, itemId, columnId);

        // THEN
        assertThat((String) cell, is(nodeName));
    }

    @Test
    public void testGenerateCellWhenItemPropertyIsNotNull() {
        // GIVEN
        final String value = "testValue";
        Mockito.when(table.getItem(itemId)).thenReturn(item);
        Mockito.when(item.getItemProperty(columnId)).thenReturn(new DefaultProperty(value));

        // WHEN
        Object cell = folderNameColumnFormatter.generateCell(table, itemId, columnId);

        // THEN
        assertThat((String) cell, is(value));
    }

    @Test
    public void testGenerateCellWhenItemPropertyIsNull() {
        // GIVEN
        Mockito.when(table.getItem(itemId)).thenReturn(item);
        Mockito.when(item.getItemProperty(columnId)).thenReturn(null);

        // WHEN
        Object cell = folderNameColumnFormatter.generateCell(table, itemId, columnId);

        // THEN
        assertThat((String) cell, is(""));
    }

    @Test
    public void testGenerateCellWhenItemIsNull() {
        // GIVEN
        Mockito.when(table.getItem(itemId)).thenReturn(null);

        // WHEN
        Object cell = folderNameColumnFormatter.generateCell(table, itemId, columnId);

        // THEN
        assertThat((String) cell, is(""));
    }

    @Test
    public void testXSSOnItemName() throws Exception {
        // GIVEN
        String unEscapedHTML = "<A onmouseover=alert('XSS_by_Vishal_V_Sonar_&_Akash_Chavan')> XSS </A>";

        Mockito.when(table.getItem(itemId)).thenReturn(item);
        Mockito.when(item.getItemProperty(columnId)).thenReturn(new DefaultProperty(unEscapedHTML));

        // WHEN
        Object cell = folderNameColumnFormatter.generateCell(table, itemId, columnId);

        // THEN
        assertThat((String) cell, is(EscapeUtil.escapeXss(unEscapedHTML)));
    }

}
