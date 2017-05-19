'use strict';

angular.module('kevoreeRegistryApp')
	.filter('join', function () {
  return function (inputs, separator) {
    return inputs.join(separator || ', ');
  };
});
