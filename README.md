# Sonar Report CNES
SonarQube is an open platform to manage code quality. This program can export code analysis from a SonarQube server as a docx file, xlsx file and text files.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

### Quickstart
- Setup a SonarQube instance
- Run an analysis
- Execute th program in command line

#### Example
````
java -jar sonar-report-cnes.jar --sonar.url http://sonarqube:9000 --sonar.project.id my-project-id --report.template ./template.docx --report.locale fr_FR
````

### Features
- Export code analysis as a set of files
- Export code analysis configuration
- Use custom templates
- Get a custom OpenXML (docx, xlsx) report
- Get a dynamic pivot table with all issues
- Export in french or english

### Resources
- Command parameters
- Architecture

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log.

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

### License
Copyright 2017 LEQUAL.

Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
