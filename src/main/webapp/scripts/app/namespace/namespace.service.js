'use strict';

angular.module('kevoreeRegistryApp')
	.factory('Namespace', function ($resource) {
		var service = $resource('api/namespaces/:name');
		var memberService = $resource('api/namespaces/:name/members/:member');

		angular.extend(service, {
			addMember: function (namespace, user) {
				return memberService
					.save({ name: namespace }, { name: user.login })
					.$promise;
			},
			removeMember: function (namespace, member) {
				return memberService
					.delete({ name: namespace, member: member })
					.$promise;
			}
		});

		return service;
	});
