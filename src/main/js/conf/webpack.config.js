/**
 * Script taken from https://github.com/SonarSource/sonar-custom-plugin-example
 * Used to setup the compiler
 */

const path = require("path");
const utils = require('./utils');

module.exports = {
  // Define the entry points here. They MUST have the same name as the page_id
  // defined in src/main/java/org/sonarsource/plugins/example/web/MyPluginPageDefinition.java
  entry: {
    report: ["./src/main/js/index.js"]
  },
  output: {
    // The entry point files MUST be shipped inside the final JAR's static/
    // directory.
    path: path.join(__dirname, "../../../../target/classes/static"),
    filename: "[name].js"
  },
  resolve: {
    modules: [ path.join(__dirname, "src/main/js"), 'node_modules' ]
  },
  externals: {
    // React 16.8 ships with SonarQube, and should be re-used to avoid 
    // collisions at runtime.
    react: "React",
    "react-dom": "ReactDOM",
    // Register the Sonar* globals as packages, to simplify importing.
    // See src/main/js/common/api.js for more information on what is exposed 
    // in SonarRequest.
    "sonar-request": "SonarRequest"
  },
  module: {
    // Our example uses Babel to transpile our code.
    rules: [
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: /(node_modules)/
      },
      {
        // extract styles from 'app/' into separate file
        test: /\.css$/,
        include: path.resolve(__dirname, 'src/main/js'),
        use: [
          'style-loader',
          utils.cssLoader(),
          utils.postcssLoader()
        ]
      },
      {
        // inline all other styles
        test: /\.css$/,
        exclude: path.resolve(__dirname, 'src/main/js'),
        use: ['style-loader', utils.cssLoader(), utils.postcssLoader()]
      },
      { test: /\.json$/, loader: "json" }
    ]
  }
};