/**
 * Here are all the function used to interact with SonarQube API
 */

import { getJSON, postJSON, post } from "sonar-request";

// Function used to check that the current SonarQube Server version is supported
// Community Build reports 25.x / 26.x, Server reports 2025.x / 2026.x
export function isCompatible() {
  const COMPATIBILITY_PATTERN = /^(20)?2[56]\./;

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
// SonarQube checks expirationDate against its own UTC date, so the date is
// computed in UTC and two days ahead to stay valid whatever the browser timezone
function createToken(name) {
  const expireDate = formatDate(new Date(Date.now() + 2 * 24 * 60 * 60 * 1000));
  return postJSON("/api/user_tokens/generate", { "name": name, "expirationDate": expireDate });
}

// Function used to get the current logged user name
function getUserName(login) {
  return getJSON("/api/users/search", { "q": login }).then(response => {
    return response.users[0].name;
  });
}

// Format a date as YYYY-MM-DD using its UTC fields
function formatDate(date) {
    let d = new Date(date),
        month = '' + (d.getUTCMonth() + 1),
        day = '' + d.getUTCDate(),
        year = d.getUTCFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}

// Extract a readable message from a failed SonarQube request
// sonar-request rejects with the raw Response, the message is in its JSON body
function getErrorMessage(error) {
  const fallback = "Unable to create the plugin token.";
  if (error && typeof error.json === "function") {
    return error.json().then(body => {
      const messages = (body.errors || []).map(e => e.msg).filter(Boolean);
      return messages.length > 0 ? messages.join(" ") : fallback;
    }, () => fallback);
  }
  return Promise.resolve(error && error.message ? error.message : fallback);
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
  }).catch(error => {
    return getErrorMessage(error).then(message => {
      throw new Error(message);
    });
  });
}

// Function used to get the list of branches from a specific project
export function getBranches(project) {
  return getJSON("/api/project_branches/list", { "project": project }).then(response => {
    return response.branches;
  });
}