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
package info.magnolia.module.googlesitemap.service;

import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.module.googlesitemap.bean.SiteMapEntry;
import info.magnolia.module.googlesitemap.bean.SiteMapEntryList;
import info.magnolia.module.googlesitemap.config.SiteMapType;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Implementation of {@link SiteMapXMLUtil}.
 */
@Singleton
public class SiteMapXMLUtilImpl implements SiteMapXMLUtil {

    public static final String MOBILE_SCHEMA_URL = "http://www.google.com/schemas/sitemap-mobile/1.0";

    /**
     * Tag indicating feature phone sitemaps
     * <strong>(should not be used for smartphone versions)</strong>.
     */
    public static final String MOBILE_ELEMENT_NAME = "mobile";

    public static final String MOBILE_PREFIX = "mobile";

    public static final String URL_ELEMENT_NAME = "url";

    private Logger log = LoggerFactory.getLogger(getClass());

    private Provider<SiteMapService> service;

    @Inject
    public SiteMapXMLUtilImpl(Provider<SiteMapService> service) {
        this.service = service;
    }

    /**
     * Builds the XML representation of a give node's sitemap and returns it as a string.
     * <br><br>
     * Resulting XML is only compliant to <a href="http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">definition</a> for
     * non-"mobile" sitemaps, where "mobile" refers to <a href="https://support.google.com/webmasters/answer/6082207?rd=1">feature phone websites</a>.
     */
    @Override
    public String generateSiteMapXML(Node siteMapNode) throws RepositoryException, JAXBException {
        List<SiteMapEntry> entries = new ArrayList<SiteMapEntry>();
        Iterator<SiteMapEntry> entryIt = service.get().getSiteMapBeans(siteMapNode);
        while (entryIt.hasNext()) {
            entries.add(entryIt.next());
        }
        String type = SiteMapNodeTypes.SiteMap.getType(siteMapNode) != null ? SiteMapNodeTypes.SiteMap.getType(siteMapNode) : SiteMapType.Standard.name();
        return marshalSiteMapEntries(entries, SiteMapType.valueOf(type));
    }

    private String marshalSiteMapEntries(List<SiteMapEntry> entries, final SiteMapType type) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SiteMapEntry.class, SiteMapEntryList.class);
        StringWriter sw = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try {
            SiteMapEntryList list = new SiteMapEntryList(entries);
            final XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
            final XMLStreamWriter delWriter = new DelegatingXMLStreamWriter(writer) {
                Stack<String> namespaceStack = new Stack<>();

                @Override
                public void writeStartElement(String prefix, String namespace, String localName) throws XMLStreamException {
                    super.writeStartElement(prefix, namespace, localName);
                    namespaceStack.push(namespace);
                }

                @Override
                public void writeEndElement() throws XMLStreamException {
                    if (URL_ELEMENT_NAME.equals(namespaceStack.pop()) && SiteMapType.Mobile == type) {
                        writeEmptyElement(MOBILE_PREFIX, MOBILE_ELEMENT_NAME, "");
                    }
                    super.writeEndElement();
                }

                @Override
                public void writeNamespace(String prefix, String localName) throws XMLStreamException {
                    super.writeNamespace(prefix, localName);
                    if (type == SiteMapType.Mobile) {
                        super.writeNamespace(MOBILE_PREFIX, MOBILE_SCHEMA_URL);
                    }
                }
            };
            marshaller.marshal(list, delWriter);
        } catch (PropertyException e) {
            log.error("Failed to rebind marshaller's namespace prefix mapper, did the implementation of JAXB change?", e);
        } catch (XMLStreamException e) {
            log.error("Failed to marshal sitemap", e);
        }
        return sw.toString();
    }

}
