(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.controller('UserManagementDialogController', UserManagementDialogController);

	UserManagementDialogController.$inject = ['$uibModalInstance', 'entity', 'User', 'Language'];

	function UserManagementDialogController($uibModalInstance, entity, User, Language) {
		var vm = this;

		vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
		vm.clear = clear;
		vm.languages = null;
		vm.save = save;
		vm.user = entity;


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
			if (vm.user.id !== null) {
				User.update(vm.user, onSaveSuccess, onSaveError);
			} else {
				User.save(vm.user, onSaveSuccess, onSaveError);
			}
		}
	}
})();
