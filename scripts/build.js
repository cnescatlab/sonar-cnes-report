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
/* eslint-disable no-console */
process.env.NODE_ENV = 'production';

const chalk = require('chalk');
const webpack = require('webpack');
const config = require('../conf/webpack/webpack.config.prod.js');

function formatSize(bytes) {
  if (bytes === 0) {
    return '0';
  }
  const k = 1000; // or 1024 for binary
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
}

function build() {
  console.log(chalk.cyan.bold('Creating optimized production build...'));
  console.log();

  webpack(config, (err, stats) => {
    if (err) {
      console.log(chalk.red.bold('Failed to create a production build!'));
      console.log(chalk.red(err.message || err));
      process.exit(1);
    }

    if (stats.compilation.errors && stats.compilation.errors.length) {
      console.log(chalk.red.bold('Failed to create a production build!'));
      stats.compilation.errors.forEach(err => console.log(chalk.red(err.message || err)));
      process.exit(1);
    }

    const jsonStats = stats.toJson();

    console.log('Assets:');
    const assets = jsonStats.assets.slice();
    assets.sort((a, b) => b.size - a.size);
    assets.forEach(asset => {
      let sizeLabel = formatSize(asset.size);
      const leftPadding = ' '.repeat(Math.max(0, 8 - sizeLabel.length));
      sizeLabel = leftPadding + sizeLabel;
      console.log('', chalk.yellow(sizeLabel), asset.name);
    });
    console.log();

    const seconds = jsonStats.time / 1000;
    console.log('Duration: ' + seconds.toFixed(2) + 's');
    console.log();

    console.log(chalk.green.bold('Compiled successfully!'));
  });
}

build();
