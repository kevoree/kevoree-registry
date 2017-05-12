angular.module('kevoreeRegistryApp')
	.controller('NamespaceAddMemberController', NamespaceAddMemberController);

NamespaceAddMemberController.$inject = ['$uibModalInstance', 'namespace', 'Namespace', 'Users', 'AlertService', 'Language'];

function NamespaceAddMemberController($uibModalInstance, namespace, Namespace, Users, AlertService, Language) {
	var vm = this;

	vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
	vm.clear = clear;
	vm.languages = null;
	vm.selectedUser = null;
	vm.addMember = addMember;
	vm.namespace = namespace;

	Users.query().$promise.then(function (users) {
		vm.users = users.filter(function (user) {
			return user.authorities.indexOf('ROLE_ADMIN') === -1 &&
				user.authorities.indexOf('ROLE_ANONYMOUS') === -1 &&
				vm.namespace.members.indexOf(user.login) === -1;
		});
		vm.selectedUser = vm.users[0];
	});

	Language.getAll().then(function (languages) {
		vm.languages = languages;
	});

	function clear() {
		$uibModalInstance.dismiss('cancel');
	}

	function onAddSuccess(result) {
		vm.isProcessing = false;
		AlertService.success('New member "<strong>' + vm.selectedUser.login + '</strong>" added to namespace <strong>' + vm.namespace.name + '</strong>');
		$uibModalInstance.close(result);
	}

	function onAddError() {
		vm.isProcessing = false;
	}

	function addMember() {
		vm.isProcessing = true;
		Namespace.addMember(vm.namespace.name, vm.selectedUser)
			.then(onAddSuccess)
			.catch(onAddError);
	}
}
