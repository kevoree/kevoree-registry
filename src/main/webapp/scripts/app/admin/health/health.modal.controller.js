'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('HealthModalController', function ($uibModalInstance, currentHealth, baseName, subSystemName) {
		var vm = this;

		vm.cancel = cancel;
		vm.currentHealth = currentHealth;
		vm.baseName = baseName;
		vm.subSystemName = subSystemName;

		function cancel() {
			$uibModalInstance.dismiss('cancel');
		}
	});
