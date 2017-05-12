'use strict';

angular.module('kevoreeRegistryApp')
	.controller('ActivationController', ActivationController);

ActivationController.$inject = ['$stateParams', 'Auth'];

function ActivationController($stateParams, Auth) {
	var vm = this;
	vm.key = $stateParams.key;
	vm.success = false;
	vm.error = false;
	vm.doActivate = angular.isUndefined(vm.key) && !vm.success && !vm.error;
	vm.activate = activate;

	if (angular.isDefined(vm.key)) {
		vm.activate();
	} else {
		vm.key = null;
	}

	function activate() {
		Auth.activateAccount({ key: vm.key }).then(function () {
			vm.error = null;
			vm.success = 'OK';
		}).catch(function () {
			vm.success = null;
			vm.error = 'ERROR';
		});
	}
}
