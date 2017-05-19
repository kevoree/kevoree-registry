'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('HealthController', HealthController);

HealthController.$inject = ['HealthService', '$uibModal'];

function HealthController(HealthService, $uibModal) {
  var vm = this;

  vm.updatingHealth = true;
  vm.getLabelClass = getLabelClass;
  vm.refresh = refresh;
  vm.showHealth = showHealth;
  vm.baseName = HealthService.getBaseName;
  vm.subSystemName = HealthService.getSubSystemName;

  vm.refresh();

  function getLabelClass(statusState) {
    if (statusState === 'UP') {
      return 'label-success';
    } else {
      return 'label-danger';
    }
  }

  function refresh() {
    vm.updatingHealth = true;
    HealthService.checkHealth().then(function (response) {
      vm.healthData = HealthService.transformHealthData(response);
      vm.updatingHealth = false;
    }, function (response) {
      vm.healthData = HealthService.transformHealthData(response.data);
      vm.updatingHealth = false;
    });
  }

  function showHealth(health) {
    $uibModal.open({
      templateUrl: 'app/admin/health/health.modal.html',
      controller: 'HealthModalController',
      controllerAs: 'vm',
      size: 'lg',
      resolve: {
        currentHealth: function () {
          return health;
        },
        baseName: function () {
          return vm.baseName;
        },
        subSystemName: function () {
          return vm.subSystemName;
        }

      }
    });
  }
}
