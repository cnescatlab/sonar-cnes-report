/**
 * Script taken from https://github.com/SonarSource/sonar-custom-plugin-example
 * Used to handle environment variable
 */

const REACT_APP = /^REACT_APP_/i;

function getClientEnvironment() {
  return Object.keys(process.env).filter(key => REACT_APP.test(key)).reduce((env, key) => {
    env['process.env.' + key] = JSON.stringify(process.env[key]);
    return env;
  }, {
    // Useful for determining whether we’re running in production mode.
    // Most importantly, it switches React into the correct mode.
    'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
  });
}

module.exports = getClientEnvironment;