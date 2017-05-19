angular
  .module('kevoreeRegistryApp')
  .controller('TypeDefinitionDeleteController', TypeDefinitionDeleteController);

TypeDefinitionDeleteController.$inject = ['$state', '$stateParams', '$uibModalInstance', 'TypeDefinitions', 'AlertService'];

function TypeDefinitionDeleteController($state, $stateParams, $uibModalInstance, TypeDefinitions, AlertService) {
  var vm = this;
  vm.tdef = null;
  vm.confirmDelete = confirmDelete;

  TypeDefinitions.get({ id: $stateParams.id })
    .$promise
    .then(function (tdef) {
      vm.tdef = tdef;
    })
    .catch(function () {
      $state.go('tdefs');
    });

  function confirmDelete() {
    TypeDefinitions.delete({ id: $stateParams.id })
      .$promise
      .then(function () {
        AlertService.success('typeDefinition.delete.success', vm.tdef);
        $uibModalInstance.close(true);
      })
      .catch(function (resp) {
        if (resp.status === 404) {
          $state.go('tdefs').then(function () {
            AlertService.error('typeDefinition.errors.notfound', { id: $stateParams.id });
          });
        }
        $uibModalInstance.close(false);
      });
  }
}
