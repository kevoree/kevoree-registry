'use strict';

angular.module('kevoreeRegistryApp')
	.controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth) {
		$scope.user = {};
		$scope.errors = {};
		$scope.rememberMe = true;

		$timeout(function () {
			angular.element('[ng-model="username"]').focus();
		});

		$scope.login = function () {
			$scope.authenticationError = false;
			Auth.login({
				username: $scope.username,
				password: $scope.password,
				rememberMe: $scope.rememberMe
			}).then(function () {
				$scope.authenticationError = false;
				$rootScope.$broadcast('authenticationSuccess');
				$state.go($rootScope.fromState.name);
				// $state.back();
				// previousState was set in the authExpiredInterceptor before being redirected to login modal.
				// since login is successful, go to stored previousState and clear previousState
				if (Auth.getPreviousState()) {
						var previousState = Auth.getPreviousState();
						Auth.resetPreviousState();
						$state.go(previousState.name, previousState.params);
				}
			}).catch(function () {
				$scope.authenticationError = true;
				$scope.password = "";
			});
		};
	});
