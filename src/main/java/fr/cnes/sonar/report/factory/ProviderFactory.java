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

package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.providers.component.ComponentProvider;
import fr.cnes.sonar.report.providers.facets.FacetsProvider;
import fr.cnes.sonar.report.providers.issues.IssuesProvider;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import fr.cnes.sonar.report.providers.measure.MeasureProvider;
import fr.cnes.sonar.report.providers.project.ProjectProvider;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProvider;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProvider;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProvider;
import fr.cnes.sonar.report.providers.sonarqubeinfo.SonarQubeInfoProvider;

/**
 * Generic interface for providers factories
 */
public interface ProviderFactory {
    /**
     * Creates a new instance of a ComponentProvider
     * @return A new instance of a ComponentProvider.
     */
    ComponentProvider createComponentProvider();
    /**
     * Creates a new instance of a FacetsProvider
     * @return A new instance of a FacetsProvider.
     */
    FacetsProvider createFacetsProvider();
    /**
     * Creates a new instance of a IssuesProvider
     * @return A new instance of a IssuesProvider.
     */
    IssuesProvider createIssuesProvider();
    /**
     * Creates a new instance of a LanguageProvider
     * @return A new instance of a LanguageProvider.
     */
    LanguageProvider createLanguageProvider();
    /**
     * Creates a new instance of a MeasureProvider
     * @return A new instance of a MeasureProvider.
     */
    MeasureProvider createMeasureProvider();
    /**
     * Creates a new instance of a ProjectProvider
     * @return A new instance of a ProjectProvider.
     */
    ProjectProvider createProjectProvider();
    /**
     * Creates a new instance of a QualityGateProvider
     * @return A new instance of a QualityGateProvider.
     */
    QualityGateProvider createQualityGateProvider();
    /**
     * Creates a new instance of a QualityProfileProvider
     * @return A new instance of a QualityProfileProvider.
     */
    QualityProfileProvider createQualityProfileProvider();
    /**
     * Creates a new instance of a SecurityHotspotsProvider
     * @return A new instance of a SecurityHotspotsProvider.
     */
    SecurityHotspotsProvider createSecurityHotspotsProvider();
    /**
     * Creates a new instance of a SonarQubeInfoProvider
     * @return A new instance of a SonarQubeInfoProvider.
     */
    SonarQubeInfoProvider createSonarQubeInfoProvider();
}