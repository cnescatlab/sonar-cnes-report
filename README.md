# Sonar CNES Report
[![Build Status](https://travis-ci.org/lequal/sonar-cnes-report.svg?branch=master)](https://travis-ci.org/lequal/sonar-cnes-report)
[![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=alert_status)](https://sonarcloud.io/dashboard?id=fr.cnes.sonar%3Acnesreport)
[![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=bugs)](https://sonarcloud.io/project/issues?id=fr.cnes.sonar%3Acnesreport&resolved=false&types=BUG)
[![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=coverage)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Coverage)
[![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=sqale_index)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Maintainability)

SonarQube is an open platform to manage code quality. This program can export code analysis from a SonarQube server as a docx, xlsx, csv, markdown,  and text files.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

This tool can be used in standalone as a JAR executable (with the command line) or as a Sonarqube plugin.

### Quickstart
- Setup a SonarQube instance.
- Run an analysis with sonar-scanner, maven, gradle, msbuild, etc.
- Execute cnesreport:
   - In standalone, thanks to command line
   - In plugin mode, copy jar in `/opt/sonarqube/plugins`, restart sonarqube, then click on "More" > "CNES Report".

#### Installation
##### Standalone mode
**cnesreport** does not need any installation. It is a portable Java application you can copy and run according to following examples. The only requirement is an **up-to-date JRE (>1.8)**.

##### Plugin mode (Since 2.2.0)
- Copy the sonar-cnes-report.jar in the plugin folder of sonarqube (On linux path should be like `/opt/sonarqube/plugins`)
- Restart sonarqube (On linux: `sudo service sonar restart`)

#### Installation
**cnesreport** does not need any installation. It is a portable Java application you can copy and run according to following examples. The only requirement is an **up-to-date JRE (>1.8)**.

#### Get help
Use `java -jar cnesreport.jar -h` to get the following help about cnesreport:
````
usage: java -jar cnesreport.jar [-a <arg>] [-b <arg>] [-c] [-d <arg>] [-e] [-h] [-l <arg>] [-o <arg>] [-p <arg>] [-r <arg>]
       [-s <arg>] [-t <arg>] [-v] [-w] [-x <arg>]
Generate editable reports for SonarQube projects.

 -a,--author <arg>                 Name of the report writer.
 -b,--branch <arg>                 Branch of the targeted project. Requires Developer Edition or sonarqube-community-branch-plugin.
 -c,--disable-conf                 Disable export of quality configuration used during analysis.
 -d,--date <arg>                   Date for the report. Default: current date.
 -e,--disable-spreadsheet          Disable spreadsheet generation.
 -f,--disable-csv                  Disable csv generation.
 -h,--help                         Display this message.
 -l,--language <arg>               Language of the report. Values: en_US, fr_FR. Default: en_US.
 -m,--disable-markdown             Disable markdown generation.
 -n,--template-markdown <arg>      Path to the report template in markdown. Default: usage of internal template.
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

#### Get logs
You can have more detailed logs in the hidden directory `.cnesreport` which should be created in your home directory at first launch.

#### Examples

##### Simplest usage
This is the minimal usage of cnesreport. This example export (report + spreadsheet + configuration) the public project `projectId` from SonarQube server `http://localhost:9000`. This will use default internal templates.
````
java -jar cnesreport.jar -p projectId
````

If you have installed cnes-report in your sonarqube: open web interface, click on "CNES Report" then choose a project.

##### Advanced usage (standalone)
If you are using a secured instance of SonarQube, you can provide a SonarQube authentication token thanks to `-t` option and specify the url of the SonarQube instance with `-s`. The internal template for the text report will be replace by the one given through `-r` option.
````
java -jar cnesreport.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId -r ./template.docx
````

##### Export of a specific project branch (standalone)
If you are using a commercial edition of sonarqube or the [sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin) you want to export the report for a specific branch of your project using the -b option. 
````
java -jar cnesreport.jar -p projectId -b dev
````

##### Enterprise features available for all
As this application is used in many enterprise contexts, we have added the ability to go through proxy. The **cnesreport** application use system proxy configuration so that you have no fanciful parameter to set.

To use the proxy feature be sure to set following properties:
- `https.proxyHost`
- `https.proxyPort`
- `https.proxyUser`
- `https.proxyPassword`

###### Example
If your JRE's proxy is not set, you can use Java flags as follow:
```bash
java -Dhttps.proxyHost=https://myproxy -Dhttps.proxyPort=42
-Dhttps.proxyUser=jerry -Dhttps.proxyPassword=siegel
-jar cnesreport.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId

```

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
  <td><b>cnesreport <br>\<br> SonarQube</b></td>
  <td><b>1.1.0<br/>Standalone only</b></td>
  <td><b>1.2.0<br/>Standalone only</b></td>
  <td><b>1.2.1<br/>Standalone only</b></td>
  <td><b>2.0.0<br/>Standalone only</b></td>
  <td><b>2.1.0<br/>Standalone only</b></b></td>
  <td><b>2.2.0<br/>Standalone + Plugin</b></td>
  <td><b>3.x.x<br/>Standalone + Plugin</b></b></td>
 </tr>
 <tr>
  <td><b>3.7.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
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
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.0.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.1.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.2.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.3.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.4.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.5.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>6.6.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>6.7.x (LTS)</b></td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
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
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.1</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.2</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.3</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.7</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.8</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
 <tr>
  <td><b>7.9.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>(standalone only)</td>
  <td>X</td>
 </tr>
</table>

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log.

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

### Notice about plugin mode
- You can access to the plugin with the web api (`/api/cnesreport/report`) or
with the web UI (`/extension/cnesreport/report` / "More" > "CNES Report").

- Plugin mode is made to provide an easier usage than standalone usage. If you need to 
use advanced features you should use plugin in standalone.

- During execution, the plugin mode use the `${SONARQUBE_HOME}/temp` folder. It writes some files,
zip these files and send them to client. Files are deleted after download. If you interrupt plugin
during execution (bug, stopping sonar, etc.) you may check this folder to remove useless files.

### License
Copyright 2017 LEQUAL.

Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
