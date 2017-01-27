'use strict';

angular.module('kevoreeRegistryApp')
	.filter('mapProp', function () {
		return function (inputs, prop) {
			return inputs.map(function (input) {
				return input[prop];
			});
		};
	});
