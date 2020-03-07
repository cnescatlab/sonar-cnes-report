/**
 * Script taken from https://github.com/SonarSource/sonar-custom-plugin-example
 * Used to setup the compiler for the Maven build
 */

const webpack = require('webpack');
const config = require('./webpack.config');
const getClientEnvironment = require('./env');

const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const autoprefixer = require("autoprefixer");

// Get environment variables to inject into our app.
const env = getClientEnvironment();

// Assert this just to be safe.
// Development builds of React are slow and not intended for production.
if (env['process.env.NODE_ENV'] !== '"production"') {
  throw new Error('Production builds must have NODE_ENV=production.');
}

const noUglify = process.argv.some(arg => arg.indexOf('--no-uglify') > -1);

// Don't attempt to continue if there are any errors.
config.bail = true;

config.plugins = [
  // Makes some environment variables available to the JS code, for example:
  // if (process.env.NODE_ENV === 'production') { ... }. See `./env.js`.
  // It is absolutely essential that NODE_ENV was set to production here.
  // Otherwise React will be compiled in the very slow development mode.
  new webpack.DefinePlugin(env),

  // This helps ensure the builds are consistent if source hasn't changed:
  new webpack.optimize.OccurrenceOrderPlugin(),

  new webpack.LoaderOptionsPlugin({
    options: {
      context: __dirname,
      postcss: [
        autoprefixer({
          browsers: [
            "last 3 Chrome versions",
            "last 3 Firefox versions",
            "last 3 Safari versions",
            "last 3 Edge versions",
            "IE 11"
          ]
        })
      ]
    }
  })
];

if (!noUglify) {
  config.optimization = {
    minimizer: [
      new UglifyJsPlugin({
        uglifyOptions: {
          warnings: false,
          compress: {},
          mangle: true,
          output: {
            comments: false
          }
        }
      })
    ]
  }
}

module.exports = config;