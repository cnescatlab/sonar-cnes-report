# Sonar CNES Report
[![Java CI](https://github.com/cnescatlab/sonar-cnes-report/actions/workflows/java-continuous-integration.yml/badge.svg)](https://github.com/cnescatlab/sonar-cnes-report/actions/workflows/java-continuous-integration.yml)
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
   - Give your jar read permissions for others (chmod o+r `myfile.jar`)
   - In plugin mode, copy jar in `/opt/sonarqube/extensions/plugins`, restart sonarqube, then click on "More" > "CNES Report".

#### Installation
##### Standalone mode
**cnesreport** does not need any installation. It is a portable Java application you can copy and run according to following examples. The only requirement is an **up-to-date JRE (>=1.8)**.

##### Plugin mode (Since 2.2.0)
- Copy the sonar-cnes-report.jar with read rights for others in the plugin folder of sonarqube (On linux path should be like `/opt/sonarqube/extensions/plugins`)
- Restart sonarqube (On linux: `sudo service sonar restart`)

#### Get help
Use `java -jar sonar-cnes-report.jar -h` to get the following help about cnesreport:
````
usage: java -jar sonar-cnes-report.jar [-a <arg>] [-b <arg>] [-c] [-d <arg>] [-e] [-h] [-l <arg>] [-o <arg>] [-p <arg>] [-r <arg>]
       [-s <arg>] [-t <arg>] [-v] [-w] [-x <arg>]
Generate editable reports for SonarQube projects.

 -a,--author <arg>                 Name of the report writer.
 -b,--branch <arg>                 Branch of the targeted project. Requires Developer Edition or sonarqube-community-branch-plugin. Default: usage of main branch.
 -c,--disable-conf                 Disable export of quality configuration used during analysis.
 -d,--date <arg>                   Date for the report. Format: yyyy-MM-dd. Default: current date.
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
 -t,--token <arg>                  SonarQube "User token" of the SonarQube user who has permissions on the project.
 -v,--version                      Display current version.
 -w,--disable-report               Disable report generation.
 -x,--template-spreadsheet <arg>   Path to the spreadsheet template. Default: usage of internal template.


Please report issues at https://github.com/cnescatlab/sonar-cnes-report/issues
````

#### Get logs
You can have more detailed logs in the hidden directory `.cnesreport` which should be created in your home directory at first launch.  
In addition, you can have a lookt at your SonarQube server logs, which can be very helpful in debugging a problem.

#### Examples

##### Simplest usage
This is the minimal usage of cnesreport. This example export (report + spreadsheet + configuration) the public project `projectId` from SonarQube server `http://localhost:9000`. This will use default internal templates.
````
java -jar sonar-cnes-report.jar -p projectId
````

If you have installed cnes-report in your sonarqube: open web interface, click on "CNES Report" then choose a project.

##### Advanced usage (standalone)
If you are using a secured instance of SonarQube, you can provide a SonarQube user token (Global- or Poject Analyses Token is not sufficient!) (https://docs.sonarqube.org/latest/user-guide/user-account/generating-and-using-tokens/) thanks to `-t` option and specify the url of the SonarQube instance with `-s`. The internal template for the text report can be replaced by the one given through `-r` option.
````
java -jar sonar-cnes-report.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId -r ./template.docx
````

##### Export of a specific project branch (standalone)
If you are using a commercial edition of sonarqube or the [sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin) you can export the report for a specific branch of your project using the -b option. 
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
-jar sonar-cnes-report.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId

```

### Features
- Export code analysis as a set of files
- Export code analysis configuration
- Use custom templates
- Get a custom OpenXML (docx, xlsx) report
- Get a dynamic pivot table with all issues
- Export in french or english

### Compatibility matrix

For legacy versions, check the wiki page here : [Note on legacy versions](https://github.com/cnescatlab/sonar-cnes-report/wiki#note-on-legacy-versions)

<table>
 <tr>
  <td><b>cnesreport <br>\<br> SonarQube</b></td>
  <td><b>3.0.x<br/>Standalone + Plugin</td>
  <td><b>3.1.0<br/>Standalone + Plugin</td>
  <td><b>3.2.x<br/>Standalone + Plugin</td>
  <td><b>3.3.x<br/>Standalone + Plugin</td>
  <td><b>4.0.0<br/>Standalone + Plugin</td>
  <td><b>4.1.x<br/>Standalone + Plugin</td>
  <td><b>4.2.x<br/>Standalone + Plugin</td>
 </tr>
 <tr>
  <td><b>7.9.x</b></td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
  <td>X</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>8.9.x</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>X</td>
  <td>X</td>
  <td>-</td>
 </tr>
 <tr>
  <td><b>9.9.x (LTS)</b></td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
  <td>-</td>
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

- Plugin mode is compatible with SonarQube branch feature.

- During execution, the plugin mode use the `${SONARQUBE_HOME}/temp` folder. It writes some files,
zip these files and send them to client. Files are deleted after download. If you interrupt plugin
during execution (bug, stopping sonar, etc.) you may check this folder to remove useless files.

### Compile from sources
In order to compile the project, a simple `mvn clean package` command from the project's root, where the `pom.xml` is located, will do the trick.  
Of course, Maven and Java JDK are required to build the JAR file.

### License
Copyright 2021 CATLab.

Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
