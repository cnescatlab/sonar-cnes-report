/**
 * Script taken from https://github.com/SonarSource/sonarqube
 * Used to provide custom css properties to sonar-ui-common library
 */

const cssLoader = () => ({
    loader: 'css-loader',
    options: {
      importLoaders: 1,
      modules: 'global',
      url: false
    }
  });
  
  const customProperties = {};
  const parseCustomProperties = theme => {
    Object.keys(theme).forEach(key => {
      if (typeof theme[key] === 'object') {
        parseCustomProperties(theme[key]);
      } else if (typeof theme[key] === 'string') {
        if (!customProperties[`--${key}`]) {
          customProperties[`--${key}`] = theme[key];
        } else {
          console.error(
            `Custom CSS property "${key}" already exists with value "${
              customProperties[`--${key}`]
            }".`
          );
          process.exit(1);
        }
      }
    });
  };
  
  parseCustomProperties(require('../theme'));
  
  const postcssLoader = () => ({
    loader: 'postcss-loader',
    options: {
      ident: 'postcss',
      plugins: () => [
        require('autoprefixer'),
        require('postcss-custom-properties')({ importFrom: { customProperties }, preserve: false }),
        require('postcss-calc')
      ]
    }
  });
  
  module.exports = {
    cssLoader,
    postcssLoader
  };