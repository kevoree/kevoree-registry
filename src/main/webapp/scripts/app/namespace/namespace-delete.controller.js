angular
	.module('kevoreeRegistryApp')
	.controller('NamespaceDeleteController', function ($state, $stateParams, $uibModalInstance, Namespaces, AlertService) {
		var vm = this;
		vm.namespace = $stateParams.name;
		vm.confirmDelete = confirmDelete;

		if (!vm.namespace) {
			$state.go('namespaces');
		}

		function confirmDelete() {
			Namespaces.delete({ name: vm.namespace })
				.$promise
				.then(function () {
					$uibModalInstance.close(true);
				})
				.catch(function (resp) {
					if (resp.status === 404) {
						$state.go('namespaces').then(function () {
							AlertService.error('namespace.errors.notfound', { name: $stateParams.name });
						});
					}
					$uibModalInstance.close(false);
				});
		}
	});
