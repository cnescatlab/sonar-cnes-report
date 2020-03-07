/**
 * Here are all the function used to interact with SonarQube API
 */

import { getJSON, postJSON, post } from "sonar-request";

// Function used to get current SonarQube Server version
function getSonarVersion() {
  return getJSON("/api/system/status").then(response => {
    return parseFloat(response.version);
  });
}

// Function used to get the list of existing projects (until 7.9.x)
function getProjectsList79() {
  return getJSON("/api/projects/index").then(response => {
    return response.map(item => {
      // Attribute names changed in SonarQube 8.0
      // so we need to match the new ones for the frontend
      item.key = item.k;
      item.name = item.nm;
      return item;
    });
  });
}

// Function used to get the list of existing projects (since 8.0)
function getProjectsList8() {
  return getJSON("/api/projects/search").then(response => {
    return response.components;
  });
}

// Function used to get the list of existing project regarding SonarQube version
export function getProjectsList() {
  return getSonarVersion().then(version => {
    // Depending on SonarQube version, the API is not the same to retrieve projects list
    if (version >= 8.0) {
      return getProjectsList8();
    } else {
      return getProjectsList79();
    }
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