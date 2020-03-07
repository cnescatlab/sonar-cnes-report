/**
 * Entrypoint of the plugin frontend
 * Written with ReactJS
 */

import React from 'react';
import './style.css';
import CnesReportApp from './components/CnesReportApp';

window.registerExtension('cnesreport/report', () => {
    return <CnesReportApp />
  });