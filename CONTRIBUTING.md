# Contributing to CNES Report
First off, thanks for taking the time to contribute!
The following is a set of guidelines for contributing to CNES Report, which are hosted in the [CATLab organization](https://github.com/cnescatlab) on GitHub. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

#### Table Of Contents
+ [Code of Conduct](#code-of-conduct)
+ [How Can I Contribute?](#how-can-i-contribute)  
  * [Reporting Bugs](#reporting-bugs)  
  * [Suggesting Enhancements](#suggesting-enhancements)  
  * [Pull Requests](#pull-requests)
  * [Git Commit Messages](#git-commit-messages)

## Code of Conduct
This project and everyone participating in it is governed by the [CATLab Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to us or [GitHub](https://github.com/contact/report-content).

## How Can I Contribute?

### Reporting Bugs
Before creating bug reports, please check if the problem has already been reported. 

#### How Do I Submit A (Good) Bug Report?
Bugs are tracked as [GitHub issues](https://guides.github.com/features/issues/). 
Provide information by filling [the template](ISSUE_TEMPLATE.md).

Explain the problem and include additional details to help maintainers reproduce the problem:
* **Use a clear and descriptive title** for the issue to identify the problem.
* **Describe the exact steps which reproduce the problem** in as many details as possible. When listing steps, don't just say what you did, but explain how you did it.
* **Provide specific examples to demonstrate the steps**. Include links to files or GitHub projects, which you use in those examples.
* **Explain which behavior you expected and describe the behavior you observed** after following the steps and point out what exactly is the problem with that behavior.
* **Include screenshots and animated GIFs** which show you following the described steps and clearly demonstrate the problem. You can use [this tool](https://www.cockos.com/licecap/) to record GIFs on macOS and Windows, and [this tool](https://github.com/colinkeenan/silentcast) or [this tool](https://github.com/GNOME/byzanz) on Linux.
* **If you're reporting that the plugin crashed**, include a crash report with a stack trace from the operating system.

Provide more context by answering these questions:
* **Did the problem start happening recently** (e.g. after updating to a new version of the plugin or SonarQube) or was this always a problem?
* If the problem started happening recently, **can you reproduce the problem in an older version?** What's the most recent version in which the problem doesn't happen? You can download older versions from [the releases page](https://github.com/cnescatlab/sonar-cnes-report/releases).
* **Can you reliably reproduce the issue?** If not, provide details about how often the problem happens and under which conditions it normally happens.

Include details about your configuration and environment:
* **Which SonarQube version are you using?** You can get the exact version at the bottom of your SonarQube instance.

### Suggesting Enhancements
Before creating enhancement suggestions, please check if the problem has already been suggested.

#### How Do I Submit A (Good) Enhancement Suggestion?
Enhancement suggestions are tracked as [GitHub issues](https://guides.github.com/features/issues/). To submit en enhancement suggestion, create an issue and provide the following information:
* **Use a clear and descriptive title** for the issue to identify the suggestion.
* **Provide a step-by-step description of the suggested enhancement** in as many details as possible.
* **Provide specific examples to demonstrate the steps**. Include copy/pasteable snippets which you use in those examples, as [Markdown code blocks](https://help.github.com/articles/markdown-basics/#multiple-lines).
* **Describe the current behavior** and **explain which behavior you expected to see instead** and why.
* **Include screenshots and animated GIFs** which help you demonstrate the steps or point out the part of Atom which the suggestion is related to. You can use [this tool](https://www.cockos.com/licecap/) to record GIFs on macOS and Windows, and [this tool](https://github.com/colinkeenan/silentcast) or [this tool](https://github.com/GNOME/byzanz) on Linux.
* **Explain why this enhancement would be useful**
* **Specify which version you're using.**
* **Specify the SonarQube version you're using.** The SonarQube version is mentionned at the bottom of the web page.

### Pull Requests
* Fill in [the required template](.github/PULL_REQUEST_TEMPLATE.md)
* Do not include issue numbers in the PR title
* Include screenshots and animated GIFs in your pull request whenever possible
* Follow the Java styleguides and fix all new SonarCloud issues
* Test your code before opening the PR
* Set the current `dev-*` branch as the target branch for your PR

### Git Commit Messages
* Start with the issue you are handling ("#187 ...")
* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line
* When only changing documentation, include `[ci skip]` in the commit description

## Management of contributions

All contributions are welcome. They are made via a *pull request* on the branch `dev` which is the branch of the next version.

* pull request with **major** changes must be approved by at least one maintainer of each team and the CATLab.

* pull requests with **minor** changes must be approved by at least one organization's member.
 
All maintainers have the ability to merge *pull requests* on the `dev` branch. If several maintainers belong to the same team, their validation only counts for one organization.
