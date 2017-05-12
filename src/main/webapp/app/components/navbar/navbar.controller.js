'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('NavbarController', NavbarController);

NavbarController.$inject = ['$rootScope','$scope', '$state', 'Principal'];

function NavbarController($rootScope, $scope, $state, Principal) {
	var vm = this;
	vm.user = null;
	vm.isAuthenticated = Principal.isAuthenticated;
	vm.$state = $state;

	Principal.identity()
		.then(function (user) {
			vm.user = user;
		})
		.catch(function () {
			vm.user = null;
		});

	var authSuccessHandler = $rootScope.$on('authenticationSuccess', function () {
		Principal.identity().then(function (user) {
			vm.user = user;
			vm.isAuthenticated = Principal.isAuthenticated;
		});
	});

	$scope.$on('$destroy', authSuccessHandler);
}
