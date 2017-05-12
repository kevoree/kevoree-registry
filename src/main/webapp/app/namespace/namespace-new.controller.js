angular.module('kevoreeRegistryApp')
	.controller('NamespaceNewController', NamespaceNewController);

NamespaceNewController.$inject = ['$uibModalInstance', 'namespace', 'Namespace', 'AlertService'];

function NamespaceNewController($uibModalInstance, namespace, Namespace, AlertService) {
	var vm = this;

	vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
	vm.clear = clear;
	vm.languages = null;
	vm.save = save;
	vm.namespace = namespace;

	function clear() {
		$uibModalInstance.dismiss('cancel');
	}

	function onSaveSuccess(result) {
		vm.isSaving = false;
		AlertService.success('namespace.creation.success', { name: vm.namespace.name });
		$uibModalInstance.close(result);
	}

	function onSaveError() {
		vm.isSaving = false;
	}

	function save() {
		vm.isSaving = true;
		Namespace.save(vm.namespace, onSaveSuccess, onSaveError);
	}
}
