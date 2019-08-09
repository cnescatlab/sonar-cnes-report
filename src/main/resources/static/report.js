/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

window.registerExtension('cnesreport/report', function (options) {
    // let's create a flag telling if the page is still displayed
    var isDisplayedReporting = true;


    /**
     *  Get projects list from the server and fill out the combo box
     */
    var initProjectsDropDownList = function() {
        window.SonarRequest.getJSON(
            '/api/projects/index'
        ).then(function (response) {
            // on success
            // we put each quality gate in the list
            for (i=0;i<response.length;++i){
                document.getElementById("key").innerHTML += "<option value='"+response[i].k+"'>"+response[i].nm+"</option>"
            }
        });

        window.SonarRequest.post(
                            '/api/user_tokens/revoke', {name: "cnes-report"}
                ).then(function(){
                    window.SonarRequest.postJSON(
                        '/api/user_tokens/generate', {name: "cnes-report"}
                    ).then(function (response) {
                        document.getElementById("token_cnesreport").value = response.token;
                        window.SonarRequest.getJSON(
                            '/api/users/search',
                            {"q":response.login}
                        ).then(function (response) {
                            // on success
                            document.getElementById("author").value = response.users[0].name;
                        });
                    });

                })
                
    };

    // once the request is done, and the page is still displayed (not closed already)
    if (isDisplayedReporting) {

        // Add html template
        var template = document.createElement("div");
        template.setAttribute("id", "template");
        options.el.appendChild(template);
        // retrieve template from html

        const reportForm = `
         <div class="page-wrapper-simple"><div class="page-simple">
           <h1 class="maintenance-title text-center">Generate a report</h1>
           <form id="generation-form" action="../../api/cnesreport/report" method="get">
            <div class='forminput'>
               <label for="key" id="keyLabel" class="login-label" style="display: block;"><strong>Project key</strong></label>
               <select id="key"
                       name="key"
                       class="login-input" required></select>
             </div>     <div class='forminput'>
               <label for="author" id="authorLabel" class="login-label" style="display: block;"><strong>Author</strong></label>
               <input   type="text"
                        id="author"
                        name="author"
                        class="login-input"
                        maxlength="255"
                        required
                        placeholder="Report's author">
               <input type="hidden" name="token" id="token_cnesreport" value="noauth"/>

               <div id="loading" class="text-center overflow-hidden" style="margin-bottom: 1em; display: none;">
                   <img src="../../static/cnesreport/images/loader.gif" alt="Working..."/>
               </div>
            </div><br />
               <input id="generation" name="generation" type="submit" value="Generate"><br />
               <em style="color:grey;">This operation may take some time, please wait while the report is being generated.</em>

           </form>
         </div></div>
           <style>
                .forminput select, .forminput input{
                    width:100%;
                }
           </style>
           `;
         document.getElementById("template").innerHTML=reportForm
         initProjectsDropDownList();


    }

    // return a function, which is called when the page is being closed
    return function () {
        // we unset the `isDisplayedReporting` flag to ignore to Web API calls finished after the page is closed
        isDisplayedReporting = false;
        // clean elements of this page
        options.el.textContent = '';
    };
});