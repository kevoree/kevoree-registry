(function () {
	'use strict';

	angular.module('kevoreeRegistryApp')
		.factory('Register', Register);

	Register.$inject = ['$resource'];

	function Register($resource) {
		return $resource('api/register', {}, {});
	}
})();
