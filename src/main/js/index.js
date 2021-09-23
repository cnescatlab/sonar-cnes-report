/**
 * Entrypoint of the plugin frontend
 * Written with ReactJS
 */

import React from 'react';
import './style.css';
import Initializer from 'sonar-ui-common/helpers/init';
import CnesReportApp from './components/CnesReportApp';

Initializer.setUrlContext(window.baseUrl);

window.registerExtension('cnesreport/report', () => {
    return <CnesReportApp />
  });