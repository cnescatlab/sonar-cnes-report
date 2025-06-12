/**
 * Main JS component of the plugin
 * Written with ReactJS
 */

import React from "react";
import { getProjectsList, initiatePluginToken, getBranches, isCompatible } from "../../common/api";
import { Autocomplete, FormControl, MenuItem, Select, TextField } from "@mui/material";
import { ClipLoader } from "react-spinners";
import { saveAs } from "file-saver"

export default class CnesReportApp extends React.PureComponent {
    state = {
        loading: true,
        generating: false,
        generating: false,
        projects: [],
        token: "",
        author: "",
        branches: [],
        languages: [{ id: 'en_US', name: 'English' }, { id: 'fr_FR', name: 'French' }],
        languages: [{ id: 'en_US', name: 'English' }, { id: 'fr_FR', name: 'French' }],
        enableDocx: true,
        enableMd: true,
        enableXlsx: true,
        enableCsv: true,
        enableConf: true,
        isSupported: true
    };
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    };

    onChangeAuthor = (event) => {
        this.setState({ author: event.target.value })
    };

    onChangeProject = (event) => {
        getBranches(event.target.value).then(branches => {
            this.setState({ branches: branches });
        });
    };
    // This method catches the send form event, gets the form into an object, and handles sending it in a promise
    // It then sets generating state, which is used in rendering, then downloads the report.

    handleSubmit(event) {
        event.preventDefault();

        // Initializes url object from html form contents
        const url = new URLSearchParams(new FormData(event.target));
        let fileName = "";

        this.setState({ generating: true });

        // Makes API request, fetches the filename and blobs the response, saves the blob.
        fetch("../../api/cnesreport/report" + "?" + url, {
            method: "GET"
        })
            .then(res => {
                if (!res.ok)
                    throw new Error(`Bad API call, response status: ${res.status}`);
                if (res.headers.has("Content-Disposition")) {
                    fileName = (res.headers.get("Content-Disposition"))
                        .match(/(?<=filename=")(?<resFileName>[\w-]*.[\w-]*)/);
                }
                else
                    fileName = "report.zip";
                return res.blob();
            })
            .then(blob => {
                let file = window.URL.createObjectURL(blob);
                saveAs(file, fileName.groups.resFileName);
                this.setState({ generating: false });
            })
            .catch((error) => {
                console.error(error);
                alert(error)
                this.setState({ generating: false })
            });

    }

    onChangeCheckbox = (stateParam) => {
        switch (stateParam) {
            case 'enableDocx':
                this.setState({ enableDocx: !this.state.enableDocx });
                break;
            case 'enableMd':
                this.setState({ enableMd: !this.state.enableMd });
                break;
            case 'enableXlsx':
                this.setState({ enableXlsx: !this.state.enableXlsx });
                break;
            case 'enableCsv':
                this.setState({ enableCsv: !this.state.enableCsv });
                break;
            case 'enableConf':
                this.setState({ enableConf: !this.state.enableConf });
                break;
        }
    }

    // disable generate button if no checkbox is checked to prevent the generation of an empty zip
    shouldDisableGeneration = () => {
        if (!(this.state.enableDocx || this.state.enableMd || this.state.enableXlsx || this.state.enableCsv || this.state.enableConf))
            this.setState({ disabled: true });
        else
            this.setState({ disabled: false });
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
        const isGenerating = this.state.generating;
        let generatebutton;

        if (this.state.loading) {
            return <div className="page page-limited"><p>Loading ...</p></div>;
        }

        let projectsList = this.state.projects.length > 0
            && this.state.projects.map((item, i) => {
                return (
                    { label: item.name })
            }, this);

        let branchesList = this.state.branches.length > 0
            && this.state.branches.map((item, i) => {
                return (
                    <MenuItem value={item.name}>{item.name}</MenuItem>
                )
            }, this);

        let languagesList = this.state.languages.map((item, i) => {
            return (
                <MenuItem value={item.id}>{item.name}</MenuItem>
            )
        })

        this.shouldDisableGeneration();
        if (isGenerating === true) {
            generatebutton = <ClipLoader loading={true} color="#0000FF" size={60} />;
        }
        else {
            generatebutton = <input id="generation" name="generation" type="submit" value="Generate" disabled={this.state.disabled} />
        }
        this.shouldDisableGeneration();
        if (isGenerating === true) {
            generatebutton = <ClipLoader loading={true} color="#0000FF" size={60} />;
        }
        else {
            generatebutton = <input id="generation" name="generation" type="submit" value="Generate" disabled={this.state.disabled} />
        }

        return (
            <div class="page-wrapper-simple">
                <div class="page-simple">
                    <h1 class="maintenance-title text-center">Generate a report</h1>
                    {!this.state.isSupported &&
                        <div class="compatibility-warning">
                            <p>This SonarQube version is not supported by this cnesreport version.</p>
                            <p>For further information, please refer to the <a href="https://github.com/cnescatlab/sonar-cnes-report#compatibility-matrix">compatibility matrix</a> on the project GitHub page.</p>
                        </div>
                    }
                    <form id="generation-form" onSubmit={this.handleSubmit} >

                        <div class='forminput'>
                            <label for="key" id="keyLabel" class="login-label"></label>
                            <Autocomplete
                                disablePortal
                                disableClearable
                                id="key"
                                options={projectsList}
                                defaultValue={projectsList[0]}
                                renderInput={(params) => <TextField {...params} label={"Project"} name={"key"} />}
                            />

                        </div>
                        <div class='forminput'>
                            <label for="branch" id="branchLabel" class="login-label">Branch Key</label>
                            <Select
                                displayEmpty
                                fullWidth={true}
                                name={"branch"}
                                defaultValue={branchesList[0].props.value}
                                value={this.state.value}
                                renderInput={(params) => <TextField {...params} label={"branch"} name={"branch"} value={this.state.value} />}
                            >
                                {branchesList}
                            </Select>
                        </div>
                        <div class='forminput'>
                            <label for="language" id="languageLabel" class="login-label">Report Language</label>
                            <Select
                                displayEmpty
                                id="branch"
                                name={"language"}
                                fullWidth={true}
                                value={this.state.value}
                                defaultValue={languagesList[0].props.value}
                                renderInput={(params) => <TextField {...params} label={"language"} value={this.state.value} />}
                            >
                                {languagesList}
                            </Select>
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
                            <input id="enableDocxHidden" type="hidden" value="false" name="enableDocx" disabled={this.state.enableDocx} />
                            <input type="checkbox"
                                id="enableDocx"
                                name="enableDocx"
                                value="true"
                                defaultChecked={this.state.enableDocx}
                                onChange={() => this.onChangeCheckbox('enableDocx')} />
                            <label for="enableDocx" id="enableDocxLabel"><strong>Enable DOCX generation</strong></label>
                        </div>
                        <div>
                            <input id="enableMdHidden" type="hidden" value="false" name="enableMd" disabled={this.state.enableMd} />
                            <input type="checkbox"
                                id="enableMd"
                                name="enableMd"
                                value="true"
                                defaultChecked={this.state.enableMd}
                                onChange={() => this.onChangeCheckbox('enableMd')} />
                            <label for="enableMd" id="enableMdLabel"><strong>Enable MD generation</strong></label>
                        </div>
                        <div>
                            <input id="enableXlsxHidden" type="hidden" value="false" name="enableXlsx" disabled={this.state.enableXlsx} />
                            <input type="checkbox"
                                id="enableXlsx"
                                name="enableXlsx"
                                value="true"
                                defaultChecked={this.state.enableXlsx}
                                onChange={() => this.onChangeCheckbox('enableXlsx')} />
                            <label for="enableXlsx" id="enableXlsxLabel"><strong>Enable XLSX generation</strong></label>
                        </div>
                        <div>
                            <input id="enableCsvHidden" type="hidden" value="false" name="enableCsv" disabled={this.state.enableCsv} />
                            <input type="checkbox"
                                id="enableCsv"
                                name="enableCsv"
                                value="true"
                                defaultChecked={this.state.enableCsv}
                                onChange={() => this.onChangeCheckbox('enableCsv')} />
                            <label for="enableCsv" id="enableCsvLabel"><strong>Enable CSV generation</strong></label>
                        </div>
                        <div>
                            <input id="enableConfHidden" type="hidden" value="false" name="enableConf" disabled={this.state.enableConf} />
                            <input type="checkbox"
                                id="enableConf"
                                name="enableConf"
                                value="true"
                                defaultChecked={this.state.enableConf}
                                onChange={() => this.onChangeCheckbox('enableConf')} />
                            <label for="enableConf" id="enableConfLabel"><strong>Enable quality configuration generation</strong></label>
                        </div>
                        <div class="spinner">
                            <br />
                            {generatebutton}
                        </div>

                    </form>
                </div>
            </div>
        );
    }
}
