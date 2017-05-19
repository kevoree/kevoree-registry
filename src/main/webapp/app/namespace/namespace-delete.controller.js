angular
  .module('kevoreeRegistryApp')
  .controller('NamespaceDeleteController', NamespaceDeleteController);

NamespaceDeleteController.$inject = ['$state', '$stateParams', '$uibModalInstance', 'Namespaces', 'AlertService'];

function NamespaceDeleteController($state, $stateParams, $uibModalInstance, Namespaces, AlertService) {
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
        AlertService.success('namespace.delete.success', { name: $stateParams.name });
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
}
