'use strict';

angular.module('kevoreeRegistryApp')
	.filter('stringify', function () {
		return function (input) {
			return JSON.stringify(input, null, 2);
		};
	});
