angular.module('kevoreeRegistryApp')
	.controller('NamespaceNewController', function ($stateParams, $uibModalInstance, namespace, Namespace, Language) {
		var vm = this;

		vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
		vm.clear = clear;
		vm.languages = null;
		vm.save = save;
		vm.namespace = namespace;

		Language.getAll().then(function (languages) {
			vm.languages = languages;
		});

		function clear() {
			$uibModalInstance.dismiss('cancel');
		}

		function onSaveSuccess(result) {
			vm.isSaving = false;
			$uibModalInstance.close(result);
		}

		function onSaveError() {
			vm.isSaving = false;
		}

		function save() {
			vm.isSaving = true;
			Namespace.save(vm.namespace, onSaveSuccess, onSaveError);
		}
	});
