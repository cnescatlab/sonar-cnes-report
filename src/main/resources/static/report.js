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
     * Verify that the fields are correct.
     * @returns {boolean} true if all is good
     */
    var checkForm = function () {
        // check the field key
        // get it
        var key = document.forms["generation-form"]["key"].value;
        // check if void
        if (key === "") {
            // log error
            //TODO
            // abort the process
            return false;
        }

        return true;
    };

    /**
     *  Lock or unlock the form
     *  @param isEnabled true to unlock, false to lock the form
     */
    var setEnabled = function (isEnabled) {
        // retrieve the form
        var form = document.getElementById("generation-form");
        // get all the components of the form
        var elements = form.elements;
        // change all components readOnly field to (un)lock them
        for (var i = 0, len = elements.length; i < len; i++) {
            elements[i].readOnly = !isEnabled;
            elements[i].disabled = !isEnabled;
        }

        if(isEnabled) {
            // hide loading when button are enabled
            $('#loading').hide();
        } else {
            // show loading otherwise
            $('#loading').show();
        }
    };

    /**
     * Generate the report
     * @param key
     * @param author
     */
    var produceReport = function (key, author) {
        // http GET request to the cnes web service
        window.SonarRequest.getJSON(
            '/api/cnesreport/report',
            { key: key, author: author }
        ).then(function (response) {
            // on success log generation
            //TODO
            setEnabled(true);
        }).catch(function (error) {
            // log error
            //TODO
            setEnabled(true);
        });
    };

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
        }).catch(function (response) {
            // log error
            //TODO
        });
    };

    // once the request is done, and the page is still displayed (not closed already)
    if (isDisplayedReporting) {

        // Add html template
        var template = document.createElement("div");
        template.setAttribute("id", "template");
        options.el.appendChild(template);
        // retrieve template from html
        $('#template').load('../../static/cnesreport/templates/reportForm.html', function(){
            // set generation button action
            // set its action on click
            document.querySelector('#generation').onclick = function () {

                // hide loading
                $('#loading').hide();

                // validation of the form
                if(checkForm()) {

                    // Get form values
                    var key = document.forms["generation-form"]["key"].value;
                    var author = document.forms["generation-form"]["author"].value;

                    // lock the form
                    setEnabled(false);

                    // show loading
                    $('#loading').show();

                    // request the creation of the report
                    produceReport(key, author);
                }
            };

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