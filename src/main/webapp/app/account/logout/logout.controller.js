'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LogoutController', LogoutController);

LogoutController.$inject = ['$state', 'Auth', 'AlertService'];

function LogoutController($state, Auth, AlertService) {
  Auth.logout()
		.then(function () {
  $state.go('home').then(function () {
    AlertService.success('Successfully logged-out');
  });
});
}
