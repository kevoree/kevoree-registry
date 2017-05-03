angular.module('kevoreeRegistryApp')
	.factory('Users', function ($resource) {
		return $resource('api/users', {}, {});
	});
