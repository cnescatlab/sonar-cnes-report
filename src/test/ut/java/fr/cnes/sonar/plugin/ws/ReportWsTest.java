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

package fr.cnes.sonar.plugin.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.sonar.api.server.ws.WebService;

import fr.cnes.sonar.plugin.tools.PluginStringManager;

public class ReportWsTest {

    private WebService.Context context;
    private ReportWs report;
    private WebService.Controller controller;
    private WebService.Action reportAction;

    @Test
    public void testAllWebservice() {
        testWebservice();        
        testAction();        
        testActionAuthor();
        testActionBranch();
        testActionConf();
        testActionCsv();
        testActionDocx();
        testActionKey();
        testActionLanguage();
        testActionMd();
        testActionToken();
        testActionXlsx();
    }

    public void testWebservice() {
        // Generate Webservice
        this.context = new WebService.Context();
        this.report = new ReportWs(null);
        this.report.define(this.context);

        // Control the webservice controller
        this.controller = this.context.controller(PluginStringManager.getProperty("api.url"));
        assertNotNull(this.controller);
        assertEquals(PluginStringManager.getProperty("api.url"), this.controller.path());
        assertEquals(PluginStringManager.getProperty("api.description"), this.controller.description());
        assertEquals(PluginStringManager.getProperty("plugin.since"), this.controller.since());
        assertEquals(1, this.controller.actions().size());
    }

    public void testAction() {
        // Control the webservice actions
        this.reportAction = this.controller.action(PluginStringManager.getProperty("api.report.actionKey"));
        assertNotNull(this.reportAction);
        assertEquals(PluginStringManager.getProperty("api.report.actionKey"), this.reportAction.key());
        assertEquals(PluginStringManager.getProperty("api.description"), this.reportAction.description());
        assertNotNull(this.reportAction.handler());
        assertEquals(PluginStringManager.getProperty("plugin.since"), this.reportAction.since());
    }

    public void testActionKey() {
        // Control the webservice key parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.key"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.key")).description());
        assertEquals(true,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.key")).isRequired());
        assertEquals(PluginStringManager.getProperty("api.report.args.exampleValue.key"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.key")).exampleValue());
    }

    public void testActionAuthor() {
        // Control the webservice author parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.author"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.author")).description());
        assertEquals(true,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.author")).isRequired());
        assertEquals(PluginStringManager.getProperty("api.report.args.exampleValue.author"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.author")).exampleValue());
    }

    public void testActionToken() {
        // Control the webservice token parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.token"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.token")).description());
        assertEquals(true,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.token")).isRequired());
        assertEquals(PluginStringManager.getProperty("api.report.args.exampleValue.token"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.token")).exampleValue());
    }

    public void testActionBranch() {
        // Control the webservice branch parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.branch"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.branch")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.branch")).isRequired());
        assertEquals(PluginStringManager.getProperty("api.report.args.exampleValue.branch"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.branch")).exampleValue());
    }

    public void testActionLanguage() {
        // Control the webservice language parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.language"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.language")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.language")).isRequired());
        assertEquals(PluginStringManager.getProperty("api.report.args.defaultValue.language"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.language")).defaultValue());
        assertEquals(2, this.reportAction.param(PluginStringManager.getProperty("api.report.args.language"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.language")).possibleValues()
                .contains(PluginStringManager.getProperty("api.report.args.defaultValue.language")));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.language")).possibleValues()
                .contains(PluginStringManager.getProperty("api.report.args.possibleValue.language")));
    }

    public void testActionDocx() {
        // Control the webservice enableDocx parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.enableDocx"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx")).isRequired());
        assertEquals(4, this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx"))
                .possibleValues().contains("true"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx"))
                .possibleValues().contains("false"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx"))
                .possibleValues().contains("yes"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableDocx"))
                .possibleValues().contains("no"));
    }

    public void testActionMd() {
        // Control the webservice enableMd parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.enableMd"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).isRequired());
        assertEquals(4, this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).possibleValues()
                .contains("true"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).possibleValues()
                .contains("false"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).possibleValues()
                .contains("yes"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableMd")).possibleValues()
                .contains("no"));
    }

    public void testActionXlsx() {
        // Control the webservice enableXlsx parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.enableXlsx"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx")).isRequired());
        assertEquals(4, this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx"))
                .possibleValues().contains("true"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx"))
                .possibleValues().contains("false"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx"))
                .possibleValues().contains("yes"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableXlsx"))
                .possibleValues().contains("no"));
    }

    public void testActionCsv() {
        // Control the webservice enableCsv parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.enableCsv"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv")).isRequired());
        assertEquals(4, this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv"))
                .possibleValues().contains("true"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv"))
                .possibleValues().contains("false"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv"))
                .possibleValues().contains("yes"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableCsv"))
                .possibleValues().contains("no"));
    }

    public void testActionConf() {
        // Control the webservice enableConf parameter
        assertEquals(PluginStringManager.getProperty("api.report.args.description.enableConf"),
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf")).description());
        assertEquals(false,
                this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf")).isRequired());
        assertEquals(4, this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf"))
                .possibleValues().size());
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf"))
                .possibleValues().contains("true"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf"))
                .possibleValues().contains("false"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf"))
                .possibleValues().contains("yes"));
        assertTrue(this.reportAction.param(PluginStringManager.getProperty("api.report.args.enableConf"))
                .possibleValues().contains("no"));
    }

}
