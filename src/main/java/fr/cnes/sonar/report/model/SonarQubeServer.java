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
    private int versionMajor;
    /** Number for minor version **/
    private int versionMinor;
    /** Number for revision version **/
    private int versionRevision;
    /** Number for build version **/
    private int versionBuild;
    /** True if cnesreport support this SonarQube version **/
    private boolean supported;

    /**
     * Default constructor
     */
    public SonarQubeServer() {
        this.url = "http://localhost:9000";
        this.status = false;
        this.supported = false;
        this.versionMajor = 0;
        this.versionMinor = 0;
        this.versionRevision = 0;
        this.versionBuild = 0;
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
     * @param pSupported True if this version is supported.
     */
    public void setVersion(final String pVersion, final boolean pSupported) {
        final String[] numbers = pVersion.split("\\.");
        // case when version is X.Y
        if(numbers.length >= 2) {
            this.versionMajor = Integer.parseInt(numbers[0]);
            this.versionMinor = Integer.parseInt(numbers[1]);
            // case when version is X.Y.Z
            if(numbers.length >= 3) {
                this.versionRevision = Integer.parseInt(numbers[2]);
                // case when version is X.Y.Z.build
                if(numbers.length >= 4) {
                    this.versionBuild = Integer.parseInt(numbers[3]);
                }
            }
        }
        this.supported = pSupported;
    }

    /**
     * SonarQube server full version.
     * @return String containing version.
     */
    public String getVersion() {
        return (new StringBuilder().append(versionMajor).append(".").append(versionMinor)
                .append(".").append(versionRevision).append(".").append(versionBuild)).toString();
    }

    /**
     * SonarQube server normalized version: "X.Y.Z".
     * @return String containing version.
     */
    public String getNormalizedVersion() {
        return (new StringBuilder().append(versionMajor).append(".").append(versionMinor)
                .append(".").append(versionRevision)).toString();
    }

    /**
     * Get SonarQube server support.
     * @return True if server is supported by cnesreport.
     */
    public boolean isSupported() {
        return supported;
    }
}
