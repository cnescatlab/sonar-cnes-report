/*
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
// Necessary for setting up, because of Webpack.

import React from "react";
import CnesReportProject from "./view/CnesReportProject";
import "./style.css";


// This creates a page for any component (project, portfolio, etc).
//
//  You can access it at /project/extension/example/project_page?id={COMPONENT_ID}
window.registerExtension('cnesreport/projectreport', function (options) {
  // options.el contains the DOM node we can use for our app. Prepare our node
  // so our Backbone View can correctly target it.
  return <CnesReportProject options={options} />;

});
