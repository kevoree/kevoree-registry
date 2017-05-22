(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .controller('MetricsModalController', MetricsModalController);

  MetricsModalController.$inject = ['$uibModalInstance', 'threadDump'];

  function MetricsModalController($uibModalInstance, threadDump) {
    var vm = this;

    vm.cancel = cancel;
    vm.getLevel = getLevel;
    vm.threadDump = threadDump;
    vm.threadDumpAll = 0;
    vm.threadDumpBlocked = 0;
    vm.threadDumpRunnable = 0;
    vm.threadDumpTimedWaiting = 0;
    vm.threadDumpWaiting = 0;

    angular.forEach(threadDump, function (value) {
      if (value.threadState === 'RUNNABLE') {
        vm.threadDumpRunnable += 1;
      } else if (value.threadState === 'WAITING') {
        vm.threadDumpWaiting += 1;
      } else if (value.threadState === 'TIMED_WAITING') {
        vm.threadDumpTimedWaiting += 1;
      } else if (value.threadState === 'BLOCKED') {
        vm.threadDumpBlocked += 1;
      }
    });

    vm.threadDumpAll = vm.threadDumpRunnable + vm.threadDumpWaiting +
      vm.threadDumpTimedWaiting + vm.threadDumpBlocked;

    function cancel() {
      $uibModalInstance.dismiss('cancel');
    }

    function getLevel(threadState) {
      if (threadState === 'RUNNABLE') {
        return 'success';
      } else if (threadState === 'WAITING') {
        return 'info';
      } else if (threadState === 'TIMED_WAITING') {
        return 'warning';
      } else if (threadState === 'BLOCKED') {
        return 'danger';
      }
    }
  }
})();
