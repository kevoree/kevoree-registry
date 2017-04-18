'use strict';

angular.module('kevoreeRegistryApp')
	.factory('Register', function ($resource) {
		return $resource('api/register', {}, {});
	});
