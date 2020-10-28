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
/*
// Function used to get the list of existing project regarding SonarQube version
export async function getProjectsList() {
  let result = []; 
  let numberTotal = await new Promise(r => getJSON(r, "/api/components/search", {"qualifiers": "TRK","ps": 500}).then(total => {
    return total.paging.total;
  }));
  let numberOfPages;
  if (numberTotal % 500 === 0){
    numberOfPages = numberTotal / 500;
  } else {
    numberOfPages = (numberTotal / 500) + 1;
  }
  for(let i = 0; i <= numberOfPages; i++){
      let pieceOfArray = [];
      pieceOfArray.push(await new Promise(r => getJSON(r, "/api/components/search", {"qualifiers": "TRK","ps": 500, "p": i}).then(response => {
        return response.components;
      })))
    result.push(pieceOfArray);
  }
  return result;
}
*/
/*export function getProjectsList(){
  return getJSON("/api/components/search", {"qualifiers": "TRK"}).then(response => {
      return response.components;
  });
}*/

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
      results.forEach((result) => {
        finalProjectsList.concat(result);
      })
      return finalProjectsList;
    });
  });
}


  /*finalProjectsList = () => {
    return Promise.all(allPromises);
  }
  Promise.all(allPromises).then((promise) => {
    for(let i = 0; i < promise.length; i++) {
      finalProjectsList.concat(promise[i]);
    }
  });*/
  //return finalProjectsList;
    //let finalProjectsList = [];
      //for(let i = 1; i <= nbPages; i ++){
      //  finalProjectsList.concat(allPromises[i]);
      //}
  //    console.log(finalProjectsList);
    //  return finalProjectsList;
  //});
    //return Promise.all(allPromises).then(finalProjectsList => {
     // return finalProjectsList;
   // });
//finalProjectsList.concat(projectsList);

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