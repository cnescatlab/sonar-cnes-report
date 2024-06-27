/**
 * Main JS component of the plugin
 * Written with ReactJS
 */

import React from "react";

import { getProjectsList, initiatePluginToken, getBranches, isCompatible } from "../../common/api";

export default class CnesReportApp extends React.PureComponent {
    state = {
        loading: true,
        projects: [],
        token: "",
        author: "",
        branches: [],
        languages: [{id: 'en_US', name: 'English'}, {id: 'fr_FR', name: 'French'}],
        enableDocx: true,
        enableMd: true,
        enableXlsx: true,
        enableCsv: true,
        enableConf: true,
        isSupported: true
    };

    onChangeAuthor = (event) => {
        this.setState({ author: event.target.value })
    };

    onChangeProject = (event) => {
        getBranches(event.target.value).then(branches => {
            this.setState({ branches: branches });
        });
    };

    onChangeCheckbox = (stateParam) => {
        switch (stateParam) {
            case 'enableDocx':
                this.setState({enableDocx: !this.state.enableDocx});
                break;
            case 'enableMd':
                this.setState({enableMd: !this.state.enableMd});
                break;
            case 'enableXlsx':
                this.setState({enableXlsx: !this.state.enableXlsx});
                break;
            case 'enableCsv':
                this.setState({enableCsv: !this.state.enableCsv});
                break;
            case 'enableConf':
                this.setState({enableConf: !this.state.enableConf});
                break;
          }
    }

    // disable generate button if no checkbox is checked to prevent the generation of an empty zip
    shouldDisableGeneration = () => {
        return !(this.state.enableDocx || this.state.enableMd || this.state.enableXlsx
            || this.state.enableCsv || this.state.enableConf);
    }

    componentDidMount() {
        // Initialize compatibility check
        isCompatible().then(isSupported => {
            this.setState({ isSupported });
        });

        // Initialize data in form
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
            return <div className="page page-limited"><p>Loading ...</p></div>;
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
        
        let languagesList = this.state.languages.map((item, i) => {
            return (
                <option key={i} value={item.id}>{item.name}</option>
            )
        })

        return (
            <div class="page-wrapper-simple">
                <div class="page-simple">
                    <h1 class="maintenance-title text-center">Generate a report</h1>
                    { !this.state.isSupported &&
                        <div class="compatibility-warning">
                            <p>This SonarQube version is not supported by this cnesreport version.</p>
                            <p>For further information, please refer to the <a href="https://github.com/cnescatlab/sonar-cnes-report#compatibility-matrix">compatibility matrix</a> on the project GitHub page.</p>
                        </div>
                    }
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
                            <label for="language" id="languageLabel" class="login-label"><strong>Report language</strong></label>
                            <select id="language"
                                name="language"
                                class="login-input" required>
                                {languagesList}
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
                        <div>
                            {/*
                                We need a hidden field for each checkbox in case it is unchecked because otherwise no value is sent and
                                we want that if we don't fill in a parameter then the api uses the default value i.e. the document is
                                generated.
                            */}
                            <input id="enableDocxHidden" type="hidden" value="false" name="enableDocx" disabled={this.state.enableDocx}/>
                            <input type="checkbox"
                                id="enableDocx"
                                name="enableDocx"
                                value="true"
                                defaultChecked={this.state.enableDocx}
                                onChange={() => this.onChangeCheckbox('enableDocx')}/>
                            <label for="enableDocx" id="enableDocxLabel"><strong>Enable DOCX generation</strong></label>
                        </div>
                        <div>
                            <input id="enableMdHidden" type="hidden" value="false" name="enableMd" disabled={this.state.enableMd}/>
                            <input type="checkbox"
                                id="enableMd"
                                name="enableMd"
                                value="true"
                                defaultChecked={this.state.enableMd}
                                onChange={() => this.onChangeCheckbox('enableMd')}/>
                            <label for="enableMd" id="enableMdLabel"><strong>Enable MD generation</strong></label>
                        </div>
                        <div>
                            <input id="enableXlsxHidden" type="hidden" value="false" name="enableXlsx" disabled={this.state.enableXlsx}/>
                            <input type="checkbox"
                                id="enableXlsx"
                                name="enableXlsx"
                                value="true"
                                defaultChecked={this.state.enableXlsx}
                                onChange={() => this.onChangeCheckbox('enableXlsx')}/>
                            <label for="enableXlsx" id="enableXlsxLabel"><strong>Enable XLSX generation</strong></label>
                        </div>
                        <div>
                            <input id="enableCsvHidden" type="hidden" value="false" name="enableCsv" disabled={this.state.enableCsv}/>
                            <input type="checkbox"
                                id="enableCsv"
                                name="enableCsv"
                                value="true"
                                defaultChecked={this.state.enableCsv}
                                onChange={() => this.onChangeCheckbox('enableCsv')}/>
                            <label for="enableCsv" id="enableCsvLabel"><strong>Enable CSV generation</strong></label>
                        </div>
                        <div>
                            <input id="enableConfHidden" type="hidden" value="false" name="enableConf" disabled={this.state.enableConf}/>
                            <input type="checkbox"
                                id="enableConf"
                                name="enableConf"
                                value="true"
                                defaultChecked={this.state.enableConf}
                                onChange={() => this.onChangeCheckbox('enableConf')}/>
                            <label for="enableConf" id="enableConfLabel"><strong>Enable quality configuration generation</strong></label>
                        </div>
                        <br />
                        <input id="generation" name="generation" type="submit" value="Generate"
                            disabled={this.shouldDisableGeneration()}/>
                        <br />
                        <em class="info-message">This operation may take some time, please wait while the report is being generated.</em>
                    </form>
                </div>
            </div>
        );
    }
}
