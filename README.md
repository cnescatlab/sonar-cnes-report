# Sonar CNES Report
[![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=alert_status)](https://sonarcloud.io/dashboard?id=fr.cnes.sonar%3Acnesreport)
[![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=bugs)](https://sonarcloud.io/project/issues?id=fr.cnes.sonar%3Acnesreport&resolved=false&types=BUG)
[![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=coverage)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Coverage)
[![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonar%3Acnesreport&metric=sqale_index)](https://sonarcloud.io/component_measures?id=fr.cnes.sonar%3Acnesreport&metric=Maintainability)

> 上方徽章為上游專案 [cnescatlab/sonar-cnes-report](https://github.com/cnescatlab/sonar-cnes-report) 在 SonarCloud 的分析結果。

SonarQube 是管理程式碼品質的開放平台。本程式可將 SonarQube 伺服器上的程式碼分析結果匯出為 docx 報告、xlsx 試算表、csv、markdown，以及分析時使用的品質設定（品質設定檔 xml、品質門檻 json）。

本程式為自由軟體；您可以依自由軟體基金會發布的 GNU 通用公共授權條款第 3 版（或您選擇的任何更新版本）重新散布及／或修改本程式。

本工具可以獨立執行（以命令列執行 JAR），也可以作為 SonarQube plugin 使用。

### 快速開始
- 架設 SonarQube 伺服器。
- 使用 sonar-scanner、Maven、Gradle、MSBuild 等工具執行分析。
- 執行 cnesreport：
   - 獨立模式：透過命令列執行。
   - plugin 模式：將 jar 複製到 `/opt/sonarqube/extensions/plugins`，並給予其他使用者讀取權限（`chmod o+r sonar-cnes-report.jar`），重新啟動 SonarQube 後點選「More」>「CNES Report」。

#### 安裝
##### 獨立模式
**cnesreport** 不需要安裝，是可攜式 Java 應用程式，複製後依下列範例執行即可。唯一需求為 **Java 21 以上的 JRE**。

##### plugin 模式（2.2.0 起）
- 將 `sonar-cnes-report.jar` 複製到 SonarQube 的 plugin 資料夾（Linux 上通常為 `/opt/sonarqube/extensions/plugins`），並給予其他使用者讀取權限。
- 重新啟動 SonarQube（Linux：`sudo service sonar restart`）。

#### 取得說明
執行 `java -jar sonar-cnes-report.jar -h` 可顯示以下說明：
````
usage: java -jar cnesreport.jar [-a <arg>] [-b <arg>] [-c] [-d <arg>] [-e] [-f] [-h] [-l <arg>] [-m] [-n <arg>] [-o <arg>] [-p
       <arg>] [-r <arg>] [-s <arg>] [-t <arg>] [-v] [-w] [-x <arg>]
Generate editable reports for SonarQube projects.

 -a,--author <arg>                 Name of the report writer.
 -b,--branch <arg>                 Branch of the targeted project. Requires Developer Edition or
                                   sonarqube-community-branch-plugin. Default: usage of main branch.
 -c,--disable-conf                 Disable export of quality configuration used during analysis.
 -d,--date <arg>                   Date for the report. Format: yyyy-MM-dd. Default: current date.
 -e,--disable-spreadsheet          Disable spreadsheet generation.
 -f,--disable-csv                  Disable CSV generation
 -h,--help                         Display this message.
 -l,--language <arg>               Language of the report. Values: en_US, fr_FR, zh_TW. Default: en_US.
 -m,--disable-markdown             Disable Markdown generation
 -n,--template-markdown <arg>      Path to the report template in markdown. Default: usage of internal template.
 -o,--output <arg>                 Output path for exported resources.
 -p,--project <arg>                SonarQube key of the targeted project.
 -r,--template-report <arg>        Path to the report template. Default: usage of internal template.
 -s,--server <arg>                 Complete URL of the targeted SonarQube server.
 -t,--token <arg>                  SonarQube token of the SonarQube user who has permissions on the project.
 -v,--version                      Display current version.
 -w,--disable-report               Disable report generation.
 -x,--template-spreadsheet <arg>   Path to the spreadsheet template. Default: usage of internal template.


Please report issues at https://github.com/cnescatlab/sonar-cnes-report/issues
````

| 參數 | 說明 |
|---|---|
| `-a` | 報告撰寫者名稱 |
| `-b` | 要匯出的分支，需要 Developer Edition 以上或 sonarqube-community-branch-plugin，預設為主分支 |
| `-c` | 不匯出分析時使用的品質設定 |
| `-d` | 報告日期，格式 `yyyy-MM-dd`，預設為今天 |
| `-e` / `-f` / `-m` / `-w` | 分別停用 xlsx / csv / markdown / docx 的產生 |
| `-l` | 報告語系：`en_US`（預設）、`fr_FR`、`zh_TW`（正體中文） |
| `-n` / `-r` / `-x` | 分別指定 markdown / docx / xlsx 範本路徑，預設使用內建範本 |
| `-o` | 輸出資料夾，預設為目前目錄 |
| `-p` | 專案的 SonarQube key |
| `-s` | SonarQube 伺服器完整網址，預設 `http://localhost:9000` |
| `-t` | 對該專案有權限之使用者的 SonarQube token |
| `-v` | 顯示目前版本 |

#### 記錄檔
第一次執行時會在使用者家目錄建立隱藏資料夾 `.cnesreport`，較詳細的記錄檔位於 `~/.cnesreport/log/`。
此外，SonarQube 伺服器本身的記錄檔對除錯也很有幫助。

#### 範例

##### 最簡單的用法
以下為 cnesreport 的最簡用法：從 SonarQube 伺服器 `http://localhost:9000` 匯出公開專案 `projectId` 的報告（docx、markdown、xlsx、csv 與品質設定），使用內建範本。
````
java -jar sonar-cnes-report.jar -p projectId
````

若已將 cnesreport 安裝為 SonarQube plugin：開啟網頁介面，點選「CNES Report」後選擇專案即可。

##### 進階用法（獨立模式）
若 SonarQube 需要驗證，請以 `-t` 提供 SonarQube **使用者 token**（User Token；Global Analysis Token 或 Project Analysis Token 權限不足），並以 `-s` 指定 SonarQube 網址。內建的 docx 範本可用 `-r` 換成自訂範本。
````
java -jar sonar-cnes-report.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId -r ./template.docx
````

##### 產生正體中文報告
````
java -jar sonar-cnes-report.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId -l zh_TW
````

##### 匯出指定分支（獨立模式）
若使用 SonarQube 商業版或 [sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin)，可用 `-b` 匯出專案的指定分支。
````
java -jar sonar-cnes-report.jar -p projectId -b dev
````

##### 透過代理伺服器連線
**cnesreport** 使用 Java 系統屬性設定代理伺服器，可設定下列屬性：
- `https.proxyHost`：代理伺服器主機名稱（不含 `http://`／`https://`）
- `https.proxyPort`：代理伺服器連接埠（未設定或格式錯誤時為 80）
- `https.proxyUser`
- `https.proxyPassword`
- `http.nonProxyHosts`：不經過代理的主機，以 `|` 分隔

###### 範例
若 JRE 未設定代理，可用 Java 參數指定：
```bash
java -Dhttps.proxyHost=myproxy -Dhttps.proxyPort=42 \
     -Dhttps.proxyUser=jerry -Dhttps.proxyPassword=siegel \
     -jar sonar-cnes-report.jar -t xuixg5hub345xbefu -s https://example.org:9000 -p projectId
```

### 功能
- 將程式碼分析結果匯出為一組檔案（docx、markdown、xlsx、csv）
- 匯出分析時使用的品質設定檔與品質門檻
- 使用自訂範本
- 產生可編輯的 OpenXML（docx、xlsx）報告
- xlsx 內含所有問題的樞紐分析表
- 報告語系：英文、法文、正體中文

#### 正體中文報告說明
- docx、markdown 與 xlsx 使用中文範本，docx 的表頭、圖表標題與安全熱點類別都已翻譯。
- 規則名稱、指標名稱（例如「Reliability Rating on New Code」）等由 SonarQube 伺服器提供的內容維持伺服器回傳的語言。
- xlsx 的欄位名稱與工作表名稱維持英文，因為程式與樞紐分析表需依名稱存取資料。
- 若以 `-r`／`-n`／`-x` 或 plugin 設定指定自訂範本，會優先使用自訂範本。

#### 報告字型
| 報告 | 西文 | 中文 | 程式碼 |
|---|---|---|---|
| docx（en_US／fr_FR） | Calibri（內文）、Cambria（標題） | － | Consolas |
| docx（zh_TW） | Segoe UI | Microsoft JhengHei（微軟正黑體） | Consolas |
| xlsx（en_US／fr_FR） | Calibri | － | － |
| xlsx（zh_TW） | Microsoft JhengHei | Microsoft JhengHei | － |

- 字型不會嵌入檔案。上述字型為 Windows 與 Office 內建（Office for Mac 2016 起也附帶 Microsoft JhengHei）；在沒有這些字型的環境（例如 LibreOffice／Linux）開啟時，會由系統替換為相近字型，版面可能略有差異。
- zh_TW docx 內文為 10.5pt、1.35 倍行距；en_US／fr_FR 內文為 10pt、1.15 倍行距。

#### 自訂範本注意事項
- 範本以 placeholder（例如 `$ISSUES_DETAILS`、`$VOLUME`）找到要填入的表格，程式會清空表格再依資料重建所有列；表格樣式、欄寬（`tblGrid`，建議搭配固定欄寬配置）與 `tblLook` 會保留。
- 內建範本的資料表格使用「CNES Report Table」樣式（深色表頭、斑馬紋）。自訂範本可沿用此樣式，或改用自己的表格樣式。
- 程式會替所有產生的表格設定表頭跨頁重複、單列不跨頁，並將全為數字的欄位靠右對齊。
- 若修改 `issues.severities` 或 `securityhotspots.priorities` 的項目數量，計數矩陣表格的欄數會跟著改變，請同步調整範本中該表格的 `tblGrid` 欄數。
- 圓餅圖會依 `issues.severities`／`issues.types` 的順序排列，並依嚴重程度與類型套用固定配色。
- xlsx 會凍結各資料工作表的表頭列、依內容設定欄寬（超過 60 字元的欄位自動換行），並依嚴重程度替 `Severity` 欄位上底色；工作表必須保留原本的英文名稱。

### 相容性
- SonarQube：支援 25.x 以上；已實際驗證 SonarQube Community Build 26.10。
- Java：執行與建置皆需要 Java 21 以上。

### plugin 模式說明
- 可透過網頁介面使用：
  - 全域頁面「More」>「CNES Report」（`/extension/cnesreport/report`）：選擇專案、分支、語系與要產生的檔案。
  - 專案頁面「Generate Project Report」：直接產生目前專案與分支的報告。
- 網頁介面支援 SonarQube 的淺色／深色主題，並會在開啟時檢查 SonarQube 版本是否支援。
- 開啟網頁介面時，會撤銷目前使用者名為 `cnes-report` 的 token，並重新產生一個 24 小時後到期的 token 供產生報告使用。
- 也可直接呼叫 Web API `GET /api/cnesreport/report`，回傳 zip 檔：

| 參數 | 必填 | 說明 |
|---|---|---|
| `key` | 是 | 專案 key |
| `token` | 否 | 使用者 token（受保護的專案需要） |
| `branch` | 否 | 分支，預設為主分支 |
| `author` | 否 | 報告撰寫者 |
| `language` | 否 | `en_US`（預設）、`fr_FR`、`zh_TW` |
| `enableDocx` / `enableMd` / `enableXlsx` / `enableCsv` / `enableConf` | 否 | 是否產生各類檔案，預設 `true` |

- 可在 SonarQube 管理介面的「CNES report」設定中指定 docx、markdown、xlsx 自訂範本路徑（`sonar.cnesreport.docx.path`、`sonar.cnesreport.md.path`、`sonar.cnesreport.xlsx.path`），路徑必須是 SonarQube 伺服器主機上的絕對路徑。
- plugin 模式支援 SonarQube 分支功能。
- plugin 模式以簡便為主；需要更多控制（例如指定報告日期）時，請改用獨立模式。
- 執行時 plugin 會在 SonarQube 的暫存資料夾（通常為 `${SONARQUBE_HOME}/temp`）寫入檔案，壓縮成 zip 後傳回用戶端，下載後即刪除。若執行中斷（錯誤、停止 SonarQube 等），可檢查該資料夾並移除殘留檔案。

### 已知限制
- SonarQube 的問題搜尋 API 最多只能取得 10000 筆問題，超過時只會匯出前 10000 筆。
- 安全熱點使用的 `api/hotspots/*` 自 SonarQube 2026.4 起標示為棄用，目前仍可正常使用。

### 從原始碼建置
在專案根目錄（`pom.xml` 所在位置）執行 `mvn clean package`，即可在 `target/` 產生 `sonar-cnes-report-<版本>.jar`。需要 Maven 與 JDK 21 以上；前端程式會在建置時自動下載 Node.js 並打包。

注意：部分日期轉換的單元測試依賴時區，在非 UTC 時區（例如台灣）執行可能失敗，可改用 `TZ=UTC mvn clean package`。

### 發行版本
在 GitHub 建立並發布以版本命名的發行版（例如 tag `v5.1.0`）後，`Release Jar` 工作流程會依 tag 設定版本號（去除開頭的 `v`）、執行測試，並將 jar 與 `.sha256` 校驗檔附加到該發行版。也可在 Actions 頁面以既有的 tag 手動執行。

### 如何貢獻
若使用時遇到問題，請建立 issue，說明重現步驟並附上記錄檔。

若要送出 PR，請說明此 PR 的目的；若是修正某個 issue，請附上 issue 編號，或在 PR 中說明重現方式。詳見 [CONTRIBUTING.md](CONTRIBUTING.md)。

### 授權
Copyright 2021 CATLab.

依 [GNU 通用公共授權條款第 3 版](https://www.gnu.org/licenses/gpl.txt) 授權。
