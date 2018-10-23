# Sonar CNES Report
[![Build Status](https://travis-ci.org/lequal/sonar-cnes-report.svg?branch=master)](https://travis-ci.org/lequal/sonar-cnes-report)
[![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=alert_status)](https://sonarcloud.io/dashboard?id=fr.cnes.sonar%3Acnesreport)
[![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=bugs)](https://sonarcloud.io/project/issues?id=fr.cnes.sonar%3Acnesreport&resolved=false&types=BUG)
[![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=coverage)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Coverage)
[![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=sqale_index)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Maintainability)

SonarQube is an open platform to manage code quality. This program can export code analysis from a SonarQube server as a docx file, xlsx file and text files.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

### Quickstart
- Setup a SonarQube instance.
- Run an analysis with sonar-scanner, maven, gradle, msbuild, etc.
- Execute cnesreport thanks to the command line.

#### Get help
Use `java -jar cnesreport.jar -h` to get the following help about cnesreport:
````
usage: java -jar cnesreport.jar [-a <arg>] [-c] [-d <arg>] [-e] [-h] [-l <arg>] [-o <arg>] [-p <arg>] [-r <arg>] 
       [-s <arg>] [-t <arg>] [-v] [-w] [-x <arg>]
Generate editable reports for SonarQube projects.

 -a,--author <arg>                 Name of the report writer.
 -c,--disable-conf                 Disable export of quality configuration used during analysis.
 -d,--date <arg>                   Date for the report. Default: current date.
 -e,--disable-spreadsheet          Disable spreadsheet generation.
 -h,--help                         Display this message.
 -l,--language <arg>               Language of the report. Values: en_US, fr_FR. Default: en_US.
 -o,--output <arg>                 Output path for exported resources.
 -p,--project <arg>                SonarQube key of the targeted project.
 -r,--template-report <arg>        Path to the report template. Default: usage of internal template.
 -s,--server <arg>                 Complete URL of the targeted SonarQube server.
 -t,--token <arg>                  SonarQube token of the SonarQube user who has permissions on the project.
 -v,--version                      Display current version.
 -w,--disable-report               Disable report generation.
 -x,--template-spreadsheet <arg>   Path to the spreadsheet template. Default: usage of internal template.


Please report issues at https://github.com/lequal/sonar-cnes-report/issues
````

#### Examples

##### Simplest usage
This is the minimal usage of cnesreport. This example export (report + spreadsheet + configuration) the public project `projectId` from SonarQube server `http://localhost:9000`. This will use default internal templates.
````
java -jar cnesreport.jar -p projectId
````

##### Advanced usage
If you are using a secured instance of SonarQube, you can provide a SonarQube authentication token thanks to `-t` option and specify the url of the SonarQube instance with `-s`. The internal template for the text report will be replace by the one given through `-r` option.
````
java -jar cnesreport.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId -r ./template.docx
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
