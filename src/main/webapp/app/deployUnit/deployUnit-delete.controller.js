angular
  .module('kevoreeRegistryApp')
  .controller('DeployUnitDeleteController', DeployUnitDeleteController);

DeployUnitDeleteController.$inject = ['$state', '$stateParams', '$uibModalInstance', 'DeployUnits', 'AlertService'];

function DeployUnitDeleteController($state, $stateParams, $uibModalInstance, DeployUnits, AlertService) {
  var vm = this;
  vm.du = null;
  vm.confirmDelete = confirmDelete;

  DeployUnits.get({ id: $stateParams.id })
    .$promise
    .then(function (du) {
      vm.du = du;
    })
    .catch(function () {
      $state.go('dus');
    });

  function confirmDelete() {
    DeployUnits.delete({ id: $stateParams.id })
      .$promise
      .then(function () {
        AlertService.success('deployUnit.delete.success', vm.du);
        $uibModalInstance.close(true);
      })
      .catch(function (resp) {
        if (resp.status === 404) {
          $state.go('dus').then(function () {
            AlertService.error('deployUnit.errors.notfound', { id: $stateParams.id });
          });
        }
        $uibModalInstance.close(false);
      });
  }
}
