/**
 * Here are all the function used to interact with SonarQube API
 */

import { getJSON, postJSON, post } from "sonar-request";

//Function used to get current SonarQube Server version
function getSonarVersion() {
  return getJSON("/api/system/status").then(response => {
    return parseFloat(response.version);
  });
}

//Functions used to get all user projects
export function getProjectsList(){
  let elementByPage = 500;
  let allPromises = [];
  let finalProjectsList = [];
  return getJSON("/api/components/search", {"qualifiers": "TRK", "ps":elementByPage}).then(response => {
    let nbProjects = response.paging.total;
    let nbPages = Math.ceil(nbProjects/elementByPage);
    for(let i = 1; i <= nbPages; i++){
      allPromises.push(getJSON("/api/components/search", {"qualifiers": "TRK", "ps":elementByPage, "p":i}).then(response => {
        return response.components;
      }));
    }
    return Promise.all(allPromises).then((results) => {
      let projects = [];
      results.forEach((result) => {
        projects = projects.concat(result);
        console.log(projects);
      })
      finalProjectsList = projects;
      return finalProjectsList;
    });
  });
}

// Function used to revoke the plugin token
function revokeToken(name) {
  return post("/api/user_tokens/revoke", { "name": name });
}

// Function used to create the plugin token
function createToken(name) {
  return postJSON("/api/user_tokens/generate", { "name": name });
}

// Function used to get the current logged user name
function getUserName(login) {
  return getJSON("/api/users/search", { "q": login }).then(response => {
    return response.users[0].name;
  });
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