/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.model;

/**
 * Instance of SonarQube server
 */
public class SonarQubeServer {
    /** URL of the SonarQube instance **/
    private String url;
    /** Status for server **/
    private boolean status;
    /** Number for major version **/
    private String version;
    /** True if cnesreport support this SonarQube version **/
    private boolean supported;

    /**
     * Default constructor
     */
    public SonarQubeServer() {
        this.url = "http://localhost:9000";
        this.status = false;
        this.supported = false;
        this.version = "";
    }

    /**
     * SonarQube server URL.
     * @return String containing URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * SonarQube server URL.
     * @param url URL of the SonarQube server.
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Get SonarQube server status.
     * @return True if server is up.
     */
    public boolean isUp() {
        return status;
    }

    /**
     * Set server status.
     * @param status String returned by SonarQube API.
     */
    public void setStatus(final String status) {
        this.status = "UP".equals(status);
    }

    /**
     * Setter for version.
     * @param pVersion Version as provided by SonarQube.
     */
    public void setVersion(final String pVersion) {
        this.version = pVersion;
    }

    /**
     * SonarQube server version.
     * @return String containing version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Defines if the SonarQube server is supported
     * @param pSupported True if this version is supported.
     */
    public void setSupported(final boolean pSupported) {
        this.supported = pSupported;
    }

    /**
     * Get SonarQube server support.
     * @return True if server is supported by cnesreport.
     */
    public boolean isSupported() {
        return supported;
    }
}
