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
package info.magnolia.module.googlesitemap.app.actions;

import info.magnolia.cms.core.Path;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.module.googlesitemap.service.SiteMapXMLUtil;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;

/**
 * Exports sitemap to xml via JAXB using according to  <a href = "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"></a>.
 * @param <T> exact definition type.
 */
public class ExportSiteMapToXMLAction<T extends ExportSiteMapToXMLActionDefinition> extends AbstractAction<T> {

    public static final String XML_EXTENSION = ".xml";

    private Logger log = LoggerFactory.getLogger(getClass());

    private FileOutputStream fileOutputStream;

    private SimpleTranslator translator;

    private JcrNodeAdapter siteMapNode;

    private SiteMapXMLUtil xmlUtil;

    private UiContext uiContext;

    private File fileOutput;

    @Inject
    public ExportSiteMapToXMLAction(T definition, JcrNodeAdapter siteMapNode, UiContext uiContext, SimpleTranslator translator, SiteMapXMLUtil xmlUtil) {
        super(definition);
        this.siteMapNode = siteMapNode;
        this.uiContext = uiContext;
        this.translator = translator;
        this.xmlUtil = xmlUtil;
    }

    @Override
    public void execute() throws ActionExecutionException {
        try {
            String sw = xmlUtil.generateSiteMapXML(siteMapNode.getJcrItem());
            fileOutput = File.createTempFile(siteMapNode.getItemId().toString(), XML_EXTENSION, Path.getTempDirectory());
            fileOutputStream = new FileOutputStream(fileOutput);
            fileOutputStream.write(sw.toString().getBytes());
            openFileInBlankWindow(siteMapNode.getNodeName() + XML_EXTENSION, "application/octet-stream");
        } catch (RepositoryException e) {
            onError(e);
        } catch (JAXBException e) {
            onError(e);
        } catch (IOException e) {
            onError(e);
        }
    }

    protected void onError(Exception e) {
        log.error("Error occurred during site map export", e);
        String message = translator.translate("siteMaps.export.failed");
        uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, message + ":" + e.getMessage());
    }

    protected void openFileInBlankWindow(String fileName, String mimeType) {
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    return new DeleteOnCloseFileInputStream(fileOutput);
                } catch (IOException e) {
                    return null;
                }
            }
        };
        StreamResource resource = new StreamResource(source, fileName);
        resource.setCacheTime(-1);
        resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + fileName + "\"");
        resource.setMIMEType(mimeType);
        resource.setCacheTime(0);
        Page.getCurrent().open(resource, "", true);
    }

    /**
     * Implementation of {@link java.io.FileInputStream} that ensure that the {@link File} <br>
     * used to construct this class is deleted on close() call.
     */
    private class DeleteOnCloseFileInputStream extends FileInputStream {
        private File file;
        private final Logger log = LoggerFactory.getLogger(DeleteOnCloseFileInputStream.class);

        public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (file.exists() && !file.delete()) {
                log.warn("Could not delete temporary export file {}", file.getAbsolutePath());
            }
        }
    }
}
