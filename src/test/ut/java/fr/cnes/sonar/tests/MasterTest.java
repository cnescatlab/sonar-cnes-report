///*
// * This file is part of cnesreport.
// *
// * cnesreport is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * cnesreport is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package fr.cnes.sonar.tests;
//
//import fr.cnes.sonar.report.utils.Params;
//import org.junit.Before;
//
//import java.util.Date;
//
///**
// * Contains common code for tests
// * @author lequal
// */
//public class MasterTest {
//
//    /**
//     * stubbed parameters for testing
//     */
//    protected Params params;
//
//    /**
//     * Setting of all stubbed resources before launching a test
//	 */
//    @Before
//    public void before() {
//        // create stubbed params
//        params = new Params();
//        params.put("sonar.server", "http://localhost:9000");
//        params.put("sonar.token", "10d9aa688626e238739c7391e30ac9bf28fe0bbd");
//        //params.put("sonar.token", "noauth");
//        params.put("sonar.project.id", "fr.cnes.sonarqube.plugins:sonar-icode-plugin");
//        params.put("report.author", "Lequal");
//        params.put("report.date", new Date().toString());
//        params.put("report.path", "./target");
//        params.put("report.locale", "fr_FR");
//        params.put("report.template", "src/main/resources/template/code-analysis-template.docx");
//        params.put("issues.template", "src/main/resources/template/issues-template.xlsx");
//
//    }
//}
