'use strict';

angular.module('kevoreeRegistryApp')
	.controller('ResetRequestController', ResetRequestController);

ResetRequestController.$inject= ['$timeout', 'Auth'];

function ResetRequestController($timeout, Auth) {
  var vm = this;
  vm.success = null;
  vm.error = null;
  vm.reset = reset;
  vm.email = null;

  $timeout(function () {
    angular.element('[ng-model="vm.email"]').focus();
  });

  function reset() {
    vm.doNotMatch = null;
    vm.error = null;
    vm.errorEmailUnknown = null;

    Auth.resetPassword(vm.email).then(function () {
      vm.success = 'OK';
    }).catch(function (response) {
      vm.success = null;
      if (response.status === 400 && response.data.message === 'unknown e-mail address') {
        vm.errorEmailUnknown = 'ERROR';
      } else {
        vm.error = 'ERROR';
      }
    });
  }
}
