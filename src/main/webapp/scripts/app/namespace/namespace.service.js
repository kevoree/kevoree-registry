'use strict';

angular.module('kevoreeRegistryApp')
	.factory('Namespace', function ($resource) {
		var service = $resource('api/namespaces/:name/members/:member', {}, {});
		return {
			addMember: function (namespace, user) {
				return service.save({ name: namespace }, { name: user.login }).$promise;
			},
			deleteMember: function (namespace, user) {
				return service.delete({ name: namespace }, { member: user.login }).$promise;
			}
		};
	});
