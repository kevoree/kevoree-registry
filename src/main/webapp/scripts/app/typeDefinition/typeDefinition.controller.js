'use strict';

angular.module('kevoreeRegistryApp')
	.controller('TypeDefinitionController', function ($rootScope, $scope, $stateParams, $resource, TypeDefinitions) {
		$scope.page = {};
		$scope.search = {
			namespace: '',
			name: '',
			version: ''
		};
		$scope.sizes = [20, 50, 100, 250];
		$scope.selectedSize = $scope.sizes[0];
		$scope.orderColumn = 'name';
		$scope.reverse = false;
		$scope.orderClasses = {
			'glyphicon-chevron-up': $scope.reverse,
			'glyphicon-chevron-down': !$scope.reverse
		};

		$scope.loadAll = function () {
			$stateParams.page = $stateParams.page || 0;
			$stateParams.size = $stateParams.size || 100;
			TypeDefinitions.get(Object.assign({ id: 'page' }, $stateParams))
				.$promise.then(function (page) {
					$scope.page = page;
					$scope.selectedSize = $scope.sizes[$scope.sizes.indexOf(page.size)];
				});
		};

		$scope.loadAll();

		$scope.isMember = function (typeDef) {
			return $rootScope.user && typeDef.namespace.members.some(function (member) {
				return member.login === $rootScope.user.login;
			});
		};

		$scope.delete = function (id, event) {
			event.stopPropagation();
			event.preventDefault();
			$scope.tdef = TypeDefinitions.get({
				id: id
			});
			$('#deleteTypeDefinitionConfirmation').modal('show');
		};

		$scope.confirmDelete = function (id) {
			TypeDefinitions.delete({
				id: id
			}, function () {
				$scope.loadAll();
				$('#deleteTypeDefinitionConfirmation').modal('hide');
				$scope.clear();
			}, function (resp) {
				$scope.deleteError = resp.data.message;
			});
		};

		$scope.clear = function () {
			$scope.tdef = null;
			$scope.search = {
				namespace: {
					name: ''
				},
				name: '',
				version: ''
			};
		};

		$scope.clearDeleteError = function () {
			$scope.deleteError = null;
		};

		$scope.clearCreateError = function () {
			$scope.createError = null;
		};

		$scope.asArray = function (val) {
			return new Array(val);
		};

		$scope.changeOrderBy = function (prop) {
			if (prop === $scope.orderColumn) {
				$scope.reverse = !$scope.reverse;
				$scope.orderClasses = {
					'glyphicon-chevron-up': $scope.reverse,
					'glyphicon-chevron-down': !$scope.reverse
				};
			}
			$scope.orderColumn = prop;
		};
	});
