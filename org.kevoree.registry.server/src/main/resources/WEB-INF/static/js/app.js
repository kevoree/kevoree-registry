'use strict';

var FQN_REGEX = /^([a-z_]{2,}(\.[a-z_]+[0-9]*[a-z_]*)(\.[a-z_]+[0-9]*[a-z_]*)*)$/,
    INFO_TIMEOUT = 4000;

/**
 * Kevoree Registry AngularJS main entry
 */
angular
    .module('kevoreeRegistry', [
        'ui.bootstrap'
    ]);