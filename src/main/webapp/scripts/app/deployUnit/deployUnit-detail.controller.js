'use strict';

angular.module('kevoreeRegistryApp')
	.controller('DeployUnitDetailController', function ($state, $stateParams, DeployUnits) {
		var vm = this;
		vm.du = null;
		DeployUnits.get({ id: $stateParams.id },
			function (du) {
				vm.du = du;
				vm.du.model = JSON.stringify(JSON.parse(du.model), null, 2);
			},
			function () {
				$state.go('dus');
			}
		);
	});
