/**
 * Main JS component of the plugin
 * Written with ReactJS
 */

import React from "react";

import DeferredSpinner from 'sonar-ui-common/components/ui/DeferredSpinner';
import { getProjectsList, initiatePluginToken, getBranches } from "../common/api";

export default class CnesReportApp extends React.PureComponent {
    state = {
        loading: true,
        projects: [],
        token: "",
        author: "",
        branches: []
    };

    onChangeAuthor = (event) => {
        this.setState({ author: event.target.value })
    };

    onChangeProject = (event) => {
        getBranches(event.target.value).then(branches => {
            this.setState({ branches: branches });
        });
    };

    componentDidMount() {
        initiatePluginToken().then(tokenInfo => {
            getProjectsList().then(projects => {
                if (projects.length > 0) {
                    getBranches(projects[0].key).then(branches => {
                        this.setState({
                            loading: false,
                            projects: projects,
                            token: tokenInfo.token,
                            author: tokenInfo.author,
                            branches: branches
                        });
                    });
                } else {
                    this.setState({
                        loading: false,
                        projects: projects,
                        token: tokenInfo.token,
                        author: tokenInfo.author,
                        branches: []
                    });
                }
            });
        });
    }

    render() {
        if (this.state.loading) {
            return <div className="page page-limited"><DeferredSpinner /></div>;
        }

        let projectsList = this.state.projects.length > 0
            && this.state.projects.map((item, i) => {
                return (
                    <option key={i} value={item.key}>{item.name}</option>
                )
            }, this);

        let branchesList = this.state.branches.length > 0
            && this.state.branches.map((item, i) => {
                return (
                    <option key={i} value={item.name}>{item.name}</option>
                )
            }, this);

        return (
            <div class="page-wrapper-simple">
                <div class="page-simple">
                    <h1 class="maintenance-title text-center">Generate a report</h1>
                    <form id="generation-form" action="../../api/cnesreport/report" method="get">
                        <div class='forminput'>
                            <label for="key" id="keyLabel" class="login-label"><strong>Project</strong></label>
                            <select id="key"
                                name="key"
                                class="login-input"
                                onChange={this.onChangeProject} required>
                                {projectsList}
                            </select>
                        </div>
                        <div class='forminput'>
                            <label for="branch" id="branchLabel" class="login-label"><strong>Branch key</strong></label>
                            <select id="branch"
                                name="branch"
                                class="login-input" required>
                                {branchesList}
                            </select>
                        </div>
                        <div class='forminput'>
                            <label for="author" id="authorLabel" class="login-label"><strong>Author</strong></label>
                            <input type="text"
                                id="author"
                                name="author"
                                class="login-input"
                                maxlength="255"
                                required
                                placeholder="Report's author" value={this.state.author}
                                onChange={this.onChangeAuthor} />
                            <input type="hidden" name="token" id="token_cnesreport" defaultValue={this.state.token} />
                        </div>
                        <br />
                        <input id="generation" name="generation" type="submit" value="Generate" /><br />
                        <em class="info-message">This operation may take some time, please wait while the report is being generated.</em>
                    </form>
                </div>
            </div>
        );
    }
}