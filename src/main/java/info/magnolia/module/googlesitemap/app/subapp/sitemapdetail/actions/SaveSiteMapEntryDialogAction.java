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
package info.magnolia.module.googlesitemap.app.subapp.sitemapdetail.actions;

import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;

/**
 * Action used to store the siteMap pages property.<br>
 * From the dialog, retrieve the {@link SiteMapEntry} entry from the incoming {@link BeanItem}.
 * Use the {@link SiteMapService} to store the properties to the related page as GoogleSiteMap mixIn properties.
 */
public class SaveSiteMapEntryDialogAction extends AbstractAction<SaveSiteMapEntryDialogActionDefinition> {

    private static final Logger log = LoggerFactory.getLogger(SaveSiteMapEntryDialogAction.class);

    private final Item item;
    private final EditorValidator validator;
    private final EditorCallback callback;
    private final SiteMapService service;

    @Inject
    protected SaveSiteMapEntryDialogAction(SaveSiteMapEntryDialogActionDefinition definition, Item item, EditorValidator validator, EditorCallback callback, SiteMapService service) {
        super(definition);
        this.item = item;
        this.validator = validator;
        this.callback = callback;
        this.service = service;
    }

    @Override
    public void execute() throws ActionExecutionException {
        // First Validate
        validator.showValidation(true);
        if (validator.isValid()) {
            try {
                SiteMapEntry entry = ((BeanItem<SiteMapEntry>) item).getBean();
                Node node = null;
                if (getDefinition().isPageEntry()) {
                    node = service.updatePageNode(entry);
                } else {
                    node = service.updateVirtualUriNode(entry);
                }
                node.getSession().save();
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        } else {
            log.debug("Validation error(s) occurred. No save performed.");
        }
    }
}
