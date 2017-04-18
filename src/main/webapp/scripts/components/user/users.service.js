'use strict';

angular.module('kevoreeRegistryApp')
	.service('Users', function ($resource) {
		return $resource('api/users', {}, {});
	});
