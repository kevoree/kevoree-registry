'use strict';

angular.module('kevoreeRegistryApp')
	.controller('HomeController', function ($q, TypeDefinitions, DeployUnits) {
		var vm = this;

		$q.all([
			TypeDefinitions.latest({ size: 5, sort: 'lastModifiedDate,desc' }),
			DeployUnits.latest({ size: 5, sort: 'lastModifiedDate,desc' }),
		]).then(function (results) {
			vm.tdefs = results[0].content;
			vm.dus = results[1].content;
		});
	});
