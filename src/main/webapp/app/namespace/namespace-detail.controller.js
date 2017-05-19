angular
	.module('kevoreeRegistryApp')
	.controller('NamespaceDetailController', NamespaceDetailController);

NamespaceDetailController.$inject = ['$q', '$state', '$stateParams', 'TypeDefinition', 'Principal', 'Namespaces', 'AlertService'];

function NamespaceDetailController($q, $state, $stateParams, TypeDefinition, Principal, Namespaces, AlertService) {
  var vm = this;
  vm.user = null;
  vm.namespace = null;
  vm.selectedUser = null;
  vm.removableMembers = [];
  vm.canModify = false;
  vm.load = load;

  if ($stateParams.name) {
    vm.load($stateParams.name);
  } else {
    $state.go('namespaces');
  }

  function load(name) {
    $q.all([
      Principal.identity(),
      Namespaces.get({ name: name }).$promise,
      TypeDefinition.getLatest(name)
    ]).then(function (results) {
      vm.user = results[0];
      vm.namespace = results[1];
      vm.typeDefinitions = results[2];

      vm.canModify = vm.user && (vm.user.authorities.indexOf('ROLE_ADMIN') !== -1 ||
				vm.user.login === vm.namespace.owner);

      vm.removableMembers = vm.namespace.members.filter(function (member) {
        return member !== vm.namespace.owner;
      });
    }).catch(function (resp) {
      if (resp.status === 404) {
        $state.go('namespaces').then(function () {
          AlertService.error('namespace.errors.notfound', { name: $stateParams.name });
        });
      }
    });
  }
}
