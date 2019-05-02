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
            $.each(response, function (i, item) {
                // we create a new option for each quality gate
                // in the json response
                var option = $('<option>', {
                                 value: item.k,
                                 text : item.nm + ' [' + item.k + ']'
                             });
                // we add it to the drop down list
                $('#key').append(option);
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
        $('#template').load('../../static/cnesreport/templates/reportForm.html', function(){
            // fill out project's drop down list
            initProjectsDropDownList();
        });

    }

    // return a function, which is called when the page is being closed
    return function () {
        // we unset the `isDisplayedReporting` flag to ignore to Web API calls finished after the page is closed
        isDisplayedReporting = false;
        // clean elements of this page
        options.el.textContent = '';
    };
});