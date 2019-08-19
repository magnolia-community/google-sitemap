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
package info.magnolia.module.googlesitemap;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;


/**
 * Configuration singleton of the GoogleSiteMap module.
 * Currently only used to define constant.
 */
@Singleton
public class GoogleSiteMapConfiguration {

    public static final String WORKSPACE = "googleSitemaps";

    private String dateFormat = "yyyy-MM-dd";
    private FastDateFormat fastDateFormat;

    public static final double DEFAULT_PRIORITY = 0.5;
    public static final String DEFAULT_CHANGE_FREQUENCY = "weekly";
    private String changeFrequency;
    private Double priority;
    /**
     * Default constructor.
     */
    public GoogleSiteMapConfiguration() {
    }

    /**
     * Getter and Setter section.
     */
    public String getDateFormat() {
        return dateFormat;
    }


    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        this.fastDateFormat = FastDateFormat.getInstance(this.dateFormat);
    }

    /**
     * Utility section.
     */
    public FastDateFormat getFastDateFormat() {
        if(this.fastDateFormat == null) {
            this.fastDateFormat = FastDateFormat.getInstance(getDateFormat());
        }
        return this.fastDateFormat;
    }

    public String getChangeFrequency() {
        return StringUtils.isBlank(changeFrequency) ? DEFAULT_CHANGE_FREQUENCY : changeFrequency;
    }

    public void setChangeFrequency(String changeFrequency) {
        this.changeFrequency = changeFrequency;
    }

    public Double getPriority() {
        return priority == null ? DEFAULT_PRIORITY : priority;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }

}
