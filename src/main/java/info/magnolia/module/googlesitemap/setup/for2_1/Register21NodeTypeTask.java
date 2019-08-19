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
package info.magnolia.module.googlesitemap.setup.for2_1;

import info.magnolia.jcr.util.NodeTypeTemplateUtil;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;
import info.magnolia.setup.for5_0.AbstractNodeTypeRegistrationTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.version.OnParentVersionAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add GoogleSiteMap mixIn to the mgnl:pages nodeType. <br>
 * Update the existing SiteMap nodeType definition.
 */
public class Register21NodeTypeTask extends AbstractNodeTypeRegistrationTask {
    private final Logger log = LoggerFactory.getLogger(Register21NodeTypeTask.class);
    private List<String> nodeTypeToAddMixIn;

    public Register21NodeTypeTask(String name, String description, String workspaceName, List<String> nodeTypeToAddMixIn) {
        super(name, description, workspaceName);
        this.nodeTypeToAddMixIn = nodeTypeToAddMixIn != null ? nodeTypeToAddMixIn : new ArrayList<String>();
    }

    @Override
    public List<NodeTypeDefinition> getNodeTypesToRegister(NodeTypeManager nodeTypeManager) throws RepositoryException {
        LinkedList<NodeTypeDefinition> res = new LinkedList<NodeTypeDefinition>();
        for (String nodeTypeName : nodeTypeToAddMixIn) {
            if (nodeTypeManager.hasNodeType(nodeTypeName)) {
                NodeType nodeType = nodeTypeManager.getNodeType(nodeTypeName);
                res.add(updateExistingNodeType(nodeType, nodeTypeManager));
            } else {
                log.warn("NodeType {} is not register. {} will not be register as super type to this definition", nodeTypeName, SiteMapNodeTypes.GoogleSiteMap.NAME);
            }
        }
        // Update the siteMap nodeType definition
        if (nodeTypeManager.hasNodeType(SiteMapNodeTypes.SiteMap.NAME)) {
            NodeType nodeType = nodeTypeManager.getNodeType(SiteMapNodeTypes.SiteMap.NAME);
            res.add(updateSiteMapNodeType(nodeType, nodeTypeManager));
        }
        return res;
    }

    @Override
    public List<String> getNodeTypesToUnregister(NodeTypeManager nodeTypeManager) throws RepositoryException {
        return null;
    }

    private NodeTypeTemplate updateExistingNodeType(NodeType nodeType, NodeTypeManager nodeTypeManager) throws RepositoryException {
        NodeTypeTemplate nodeTypeTemplate = null;
        // Add Google SiteMap as superType
        ArrayList<String> superType = new ArrayList<String>(Arrays.asList(nodeType.getDeclaredSupertypeNames()));
        superType.add(SiteMapNodeTypes.GoogleSiteMap.NAME);
        // Copy all others values
        nodeTypeTemplate = NodeTypeTemplateUtil.createNodeType(nodeTypeManager, nodeType.getName(), superType.toArray(new String[superType.size()]), nodeType.isMixin(), nodeType.hasOrderableChildNodes(), nodeType.getPrimaryItemName(), nodeType.isQueryable());
        nodeTypeTemplate.getNodeDefinitionTemplates().addAll(Arrays.asList(nodeType.getDeclaredChildNodeDefinitions()));
        nodeTypeTemplate.getPropertyDefinitionTemplates().addAll(Arrays.asList(nodeType.getDeclaredPropertyDefinitions()));

        return nodeTypeTemplate;
    }

    private NodeTypeTemplate updateSiteMapNodeType(NodeType nodeType, NodeTypeManager nodeTypeManager) throws RepositoryException {
        NodeTypeTemplate nodeTypeTemplate = null;
        // Copy basic initial values
        nodeTypeTemplate = NodeTypeTemplateUtil.createNodeType(nodeTypeManager, nodeType.getName(), nodeType.getDeclaredSupertypeNames(), nodeType.isMixin(), nodeType.hasOrderableChildNodes(), nodeType.getPrimaryItemName(), nodeType.isQueryable());
        // Add the new properties
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(NodeTypeTemplateUtil.createPropertyDefinition(nodeTypeManager, "mgnl:googleSiteMapDisplayName", false, false, false, false, false, true, OnParentVersionAction.COPY, PropertyType.STRING, null, null, null));

        nodeTypeTemplate.getPropertyDefinitionTemplates().add(NodeTypeTemplateUtil.createPropertyDefinition(nodeTypeManager, "mgnl:googleSiteMapType", false, false, false, false, false, true, OnParentVersionAction.COPY, PropertyType.STRING, null, null, null));
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(NodeTypeTemplateUtil.createPropertyDefinition(nodeTypeManager, "mgnl:googleSiteMapURL", false, false, false, false, false, true, OnParentVersionAction.COPY, PropertyType.STRING, null, null, null));
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(NodeTypeTemplateUtil.createPropertyDefinition(nodeTypeManager, "mgnl:googleSiteMapPages", false, false, false, true, false, true, OnParentVersionAction.COPY, PropertyType.STRING, null, null, null));
        nodeTypeTemplate.getPropertyDefinitionTemplates().add(NodeTypeTemplateUtil.createPropertyDefinition(nodeTypeManager, "mgnl:googleSiteMapIncludeVirtualUri", false, false, false, false, false, true, OnParentVersionAction.COPY, PropertyType.BOOLEAN, null, null, null));
        return nodeTypeTemplate;
    }
}
