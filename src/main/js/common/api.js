/**
 * Here are all the function used to interact with SonarQube API
 */

import { getJSON, postJSON, post } from "sonar-request";

// Function used to get current SonarQube Server version
export function isCompatible() {
  const COMPATIBILITY_PATTERN = /(8|9|10)(\.[5-9])(\.\d)*/; // To be change to the SQ 10 LTS

  return getJSON("/api/system/status").then(response => {
    return response.version.match(COMPATIBILITY_PATTERN) != null;
  });
}

//Functions used to get all user projects
//The maximum number of projects with this API (api/components/search) is 500.
//Thus, the objective is to display an infinite number of projects
export function getProjectsList() {
  const elementByPage = 500;
  let allPromises = [];
  //Get the number of projects and compute the number of pages required
  return getJSON("/api/components/search", { "qualifiers": "TRK", "ps": elementByPage }).then(response => {
    const nbProjects = response.paging.total;
    let nbPages = Math.ceil(nbProjects / elementByPage);
    //Store the first page
    allPromises.push(response.components);
    //Fill the array of promises with next pages if there are ones
    if (nbPages >= 2) {
      for (let i = 2; i <= nbPages; i++) {
        allPromises.push(getJSON("/api/components/search", { "qualifiers": "TRK", "ps": elementByPage, "p": i }).then(response => {
          return response.components;
        }));
      }
    }
    //Wait until all promises are done
    return Promise.all(allPromises);
  }).then((results) => {
    //Concatenate an array : x * 500 (x = number of pages)
    let projects = [];
    results.forEach((result) => {
      projects = projects.concat(result);
    })
    //Sort the projects list for a user-friendlier display
    //The name is displayed in the user interface, so we sort the projects by name
    projects.sort(GetSortOrder("name"));
    return projects;
  });
}

//Comparator function in order to compare each specific key of the json array
function GetSortOrder(key) {
  return function (a, b) {
    if (a[key] > b[key]) {
      return 1;
    } else if (a[key] < b[key]) {
      return -1;
    }
    return 0;
  }
}

// Function used to revoke the plugin token
function revokeToken(name) {
  return post("/api/user_tokens/revoke", { "name": name });
}

// Function used to create the plugin token
function createToken(name) {
  const expireDate = formatDate(new Date(Date.now() + 24 * 60 * 60 * 1000));
  return postJSON("/api/user_tokens/generate", { "name": name, "expirationDate": expireDate });
}

// Function used to get the current logged user name
function getUserName(login) {
  return getJSON("/api/users/search", { "q": login }).then(response => {
    return response.users[0].name;
  });
}

function formatDate(date) {
    let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}

// Macro function used to execute the whole plugin token process
export function initiatePluginToken() {
  const name = "cnes-report";

  return revokeToken(name).then(() => {
    return createToken(name).then(tokenResponse => {
      return getUserName(tokenResponse.login).then(userResponse => {
        return {
          token: tokenResponse.token,
          author: userResponse
        }
      });
    });
  });
}

// Function used to get the list of branches from a specific project
export function getBranches(project) {
  return getJSON("/api/project_branches/list", { "project": project }).then(response => {
    return response.branches;
  });
}