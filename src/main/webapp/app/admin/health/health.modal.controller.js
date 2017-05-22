'use strict';

angular
  .module('kevoreeRegistryApp')
  .controller('HealthModalController', HealthModalController);

HealthModalController.$inject = ['$uibModalInstance', 'currentHealth', 'baseName', 'subSystemName'];

function HealthModalController($uibModalInstance, currentHealth, baseName, subSystemName) {
  var vm = this;

  vm.cancel = cancel;
  vm.currentHealth = currentHealth;
  vm.baseName = baseName;
  vm.subSystemName = subSystemName;

  if (vm.currentHealth && vm.currentHealth.name === 'diskSpace') {
    Object.keys(vm.currentHealth.details).forEach(function (key) {
      vm.currentHealth.details[key] = bytesToSize(vm.currentHealth.details[key]);
    });
  }

  function cancel() {
    $uibModalInstance.dismiss('cancel');
  }

  function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return '0 Byte';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
  }
}
