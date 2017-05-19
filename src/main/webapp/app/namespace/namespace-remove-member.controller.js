angular.module('kevoreeRegistryApp')
	.controller('NamespaceRemoveMemberController', NamespaceRemoveMemberController);

NamespaceRemoveMemberController.$inject = ['$uibModalInstance', 'namespace', 'Namespace', 'AlertService', 'Language'];

function NamespaceRemoveMemberController($uibModalInstance, namespace, Namespace, AlertService, Language) {
  var vm = this;

  vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
  vm.clear = clear;
  vm.selectedMember = null;
  vm.languages = null;
  vm.removeMember = removeMember;
  vm.namespace = namespace;
  vm.members = vm.namespace.members.filter(function (member) {
    return member !== vm.namespace.owner;
  });
  vm.selectedMember = vm.members[0];

  Language.getAll().then(function (languages) {
    vm.languages = languages;
  });

  function clear() {
    $uibModalInstance.dismiss('cancel');
  }

  function onRemoveSuccess(result) {
    vm.isProcessing = false;
    AlertService.success('Member "<strong>' + vm.selectedMember + '</strong>" removed from namespace <strong>' + vm.namespace.name + '</strong>');
    $uibModalInstance.close(result);
  }

  function onRemoveError() {
    vm.isProcessing = false;
  }

  function removeMember() {
    if (vm.selectedMember) {
      vm.isProcessing = true;
      Namespace.removeMember(vm.namespace.name, vm.selectedMember)
				.then(onRemoveSuccess)
				.catch(onRemoveError);
    }
  }
}
