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
package info.magnolia.module.googlesitemap.model;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.service.SiteMapService;
import info.magnolia.module.googlesitemap.service.SiteMapXMLUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.RenderableDefinition;

import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Model class of the siteMap Templates.
 * @param <RD>.
 */
public class SiteMapModel<RD extends RenderableDefinition> extends RenderingModelImpl<RD> {

    // Global and static variable section.
    private static final Logger log = LoggerFactory.getLogger(SiteMapModel.class);

    // Injected service.
    protected SiteMapService siteMapService;

    private SiteMapXMLUtil xmlUtil;

    /**
     * Default constructor used for injection.
     */
    @Inject
    public SiteMapModel(Node content, RD definition, RenderingModel<?> parent, SiteMapService siteMapService, SiteMapXMLUtil xmlUtil) {
        super(content, definition, parent);
        this.siteMapService = siteMapService;
        this.xmlUtil = xmlUtil;
    }

    @Override
    public String execute() {

        // For the siteMap display, set the content type to text/xml.
        if (this.definition.getParameters() != null && this.definition.getParameters().containsKey("xmlDisplay")) {
            MgnlContext.getWebContext().getResponse().setContentType("text/xml");
        }

        return null;
    }

    public Iterator<SiteMapEntry> getSiteMapBeans() {
        try {
            return siteMapService.getSiteMapBeans(this.content);
        } catch (RepositoryException e) {
            log.error("Could not initialize SiteMapBeans", e);
            return Collections.<SiteMapEntry>emptySet().iterator();
        }
    }

    public String getXML() {
        try {
            return xmlUtil.generateSiteMapXML(this.content);
        } catch (RepositoryException e) {
            log.error("Repository operation problem occurred while generating XML:", e);
        } catch (JAXBException e) {
            log.error("JAXB operation problem occurred while generating XML:", e);
        }
        return "";
    }
}
