# Sonar CNES Report
[![Build Status](https://travis-ci.org/lequal/sonar-cnes-report.svg?branch=master)](https://travis-ci.org/lequal/sonar-cnes-report)
![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Asonar-cnes-report&metric=alert_status)
![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Asonar-cnes-report&metric=bugs)
![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Asonar-cnes-report&metric=coverage)
![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Asonar-cnes-report&metric=sqale_index)

SonarQube is an open platform to manage code quality. This program can export code analysis from a SonarQube server as a docx file, xlsx file and text files.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

### Quickstart
- Setup a SonarQube instance
- Run an analysis
- Execute the program in command line

#### Example
````
java -jar sonar-report-cnes.jar --sonar.token xuixgehubezxbefuiez87fre987 --sonar.url http://sonarqube:9000 --sonar.project.id my-project-id --report.template ./template.docx --report.locale fr_FR
````

### Features
- Export code analysis as a set of files
- Export code analysis configuration
- Use custom templates
- Get a custom OpenXML (docx, xlsx) report
- Get a dynamic pivot table with all issues
- Export in french or english

### Compatibility matrix
<table>
 <tr>
  <td><b>SonarQube / cnesreport</b></td>
  <td><b>1.1.0</b></td>
  <td><b>1.2.0</b></td>
  <td><b>1.2.1</b></td>
  <td><b>2.0.0</b></td>
 </tr>
 <tr>
  <td><b>3.7.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>4.5.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
 </tr>
  <tr>
  <td><b>5.6.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.0.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.1.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.2.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.3.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.4.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.5.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.6.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.7.x (LTS)</b></td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.0</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.1</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.2</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.3</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
 </tr>
</table>

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log.

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

### License
Copyright 2017 LEQUAL.

Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
