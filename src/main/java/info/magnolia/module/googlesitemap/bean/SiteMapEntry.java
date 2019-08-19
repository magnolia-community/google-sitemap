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
package info.magnolia.module.googlesitemap.bean;


import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.googlesitemap.GoogleSiteMapConfiguration;
import info.magnolia.module.googlesitemap.SiteMapNodeTypes;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Simple POJO containing relevant informations for the display.
 * This bean is used as well for the XML and configuration rendering.
 */
@XmlRootElement(name="url")
@XmlType(propOrder = {"loc", "lastmod", "changefreq", "priority"})
public class SiteMapEntry implements Comparable<SiteMapEntry>{

    public final static String PRIORITY_NAME = "priority";
    public final static String CHANGE_FREQ_NAME = "changefreq";
    public final static String SITE_ALERT_NAME = "styleAlert";
    public final static String PATH_NAME = "path";
    public final static String PAGE_NAME = "pageName";
    public final static String FROM_NAME = "from";
    public final static String TO_NAME = "to";

    // Used to display xml siteMaps Infos
    private String loc;
    private String lastmod;
    private String changefreq;
    private Double priority;
    private boolean hide;
    private boolean hideChildren;

    // Used to display edit Infos
    private int level;
    private String path;
    private boolean styleAlert;
    private String pageName;
    private String from;
    private String to;

    public SiteMapEntry() {
    }

    public SiteMapEntry(GoogleSiteMapConfiguration configuration, String loc, Node page, int rootLevel, String changefreq, Double priority) throws RepositoryException {
        this.loc = loc;
        this.lastmod = configuration.getFastDateFormat().format(NodeTypes.LastModified.getLastModified(page));
        this.path = page.getPath();
        this.level = page.getDepth() - rootLevel;
        this.changefreq = SiteMapNodeTypes.GoogleSiteMap.getChangeFreq(page) != null ? SiteMapNodeTypes.GoogleSiteMap.getChangeFreq(page) : changefreq;
        this.priority = SiteMapNodeTypes.GoogleSiteMap.getPriority(page) != null ? SiteMapNodeTypes.GoogleSiteMap.getPriority(page) : priority;
        this.hide = SiteMapNodeTypes.GoogleSiteMap.isHide(page);
        this.hideChildren = SiteMapNodeTypes.GoogleSiteMap.isHideChildren(page);
    }


    /**
     * Getter and Setter Section.
     */
    @XmlElement
    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @XmlElement
    public String getLastmod() {
        return lastmod;
    }


    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    @XmlElement
    public String getChangefreq() {
        return changefreq;
    }


    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }

    @XmlElement
    public Double getPriority() {
        return priority;
    }


    public void setPriority(Double priority) {
        this.priority = priority;
    }

    @XmlTransient
    public int getLevel() {
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    @XmlTransient
    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    @XmlTransient
    public boolean isStyleAlert() {
        return styleAlert;
    }


    public void setStyleAlert(boolean styleAlert) {
        this.styleAlert = styleAlert;
    }


    @XmlTransient
    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    @XmlTransient
    public String getFrom() {
        return from;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    @XmlTransient
    public String getTo() {
        return to;
    }


    public void setTo(String to) {
        this.to = to;
    }

    @XmlTransient
    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    @XmlTransient
    public boolean isHideChildren() {
        return hideChildren;
    }

    public void setHideChildren(boolean hideChildren) {
        this.hideChildren = hideChildren;
    }

    public String toStringDisplay(){
        StringBuffer sb = new StringBuffer();
        sb.append("loc        :"+loc);
        sb.append("lastmod    :"+lastmod);
        sb.append("changefreq :"+changefreq);
        sb.append("priority   :"+priority);
        return sb.toString();
    }

    public String toStringSite(){
        StringBuffer sb = new StringBuffer();
        sb.append("path       :"+path);
        sb.append("pageName   :" + pageName);
        return sb.toString();
    }

    public String toStringVirtualUri(){
        StringBuffer sb = new StringBuffer();
        sb.append("path       :"+path);
        sb.append("from       :"+from);
        sb.append("to         :"+to);
        return sb.toString();
    }

    /**
     * Implementation for the Comparable Interface.
     */
    @Override
    public int compareTo(SiteMapEntry obj) {
        if(obj == null) {
            return -1;
        }

        if (obj==this) {
            return 0;
        }

        return (obj).getLoc().compareTo(this.getLoc());

    }
    /**
     * Used for to check the unicity in the set.
     */
    @Override
    public boolean equals(Object obj) {
        // Check if this is the same object
        if (obj==this) {
            return true;
        }

        // Check Class Type
        if (obj instanceof SiteMapEntry) {
            return ((SiteMapEntry)obj).getLoc().equals(this.getLoc());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 15;
        hash = hash * getLoc().hashCode();
        return hash;
    }

}
