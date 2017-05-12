'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LoginController', LoginController);

LoginController.$inject = ['$rootScope', '$state', '$sessionStorage', '$timeout', 'Auth', 'AlertService'];

function LoginController($rootScope, $state, $sessionStorage, $timeout, Auth, AlertService) {
	var vm = this;
	vm.user = {};
	vm.errors = {};
	vm.rememberMe = true;

	$timeout(function () {
		angular.element('[ng-model="vm.username"]').focus();
	});

	vm.login = function () {
		vm.authenticationError = false;
		Auth.login({
			username: vm.username,
			password: vm.password,
			rememberMe: vm.rememberMe
		}).then(function (user) {
			vm.authenticationError = false;
			$rootScope.$broadcast('authenticationSuccess', user);

			var prevState;
			if ($sessionStorage.previousState) {
				prevState = $sessionStorage.previousState;
			} else {
				prevState = angular.extend($rootScope.previousState);
			}
			$state.go(prevState.name || 'home', prevState.params)
				.then(function () {
					AlertService.success('global.messages.account.login', { login: vm.username });
				});
		}).catch(function (resp) {
			AlertService.error('login.messages.error.authentication');
			vm.authenticationError = true;
			vm.password = "";
		});
	};
}
