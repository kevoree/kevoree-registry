'use strict';

angular.module('kevoreeRegistryApp')
	.controller('TypeDefinitionController', function ($q, $rootScope, $state, $stateParams, $resource, paginationConstants, TypeDefinitions, Principal) {
		var vm = this;
		vm.page = null;
		vm.filter = { namespace: '', name: '', version: '' };
		vm.search = '';
		vm.orderColumn = 'name';
		vm.reverse = false;
		vm.orderClasses = { 'glyphicon-chevron-up': vm.reverse, 'glyphicon-chevron-down': !vm.reverse };
		vm.loadAll = loadAll;
		vm.isMember = isMember;
		vm.delete = deleteTdef;
		vm.confirmDelete = confirmDelete;
		vm.clear = clear;
		vm.clearDeleteError = clearDeleteError;
		vm.clearCreateError = clearCreateError;
		vm.asArray = asArray;
		vm.changeOrderBy = changeOrderBy;
		vm.canDelete = canDelete;
		vm.transition = transition;
		vm.onLimitChanged = onLimitChanged;

		function loadAll() {
			$stateParams.page = $stateParams.page || 0;
			$stateParams.size = $stateParams.size || paginationConstants.itemsPerPage;

			$q.all([
				Principal.identity(),
				TypeDefinitions.get(Object.assign({ id: 'page' }, $stateParams)).$promise
			]).then(function (results) {
				vm.user = results[0];
				vm.page = results[1];
			});
		}

		vm.loadAll();

		function isMember(typeDef) {
			return $rootScope.user && typeDef.namespace.members.some(function (member) {
				return member.login === $rootScope.user.login;
			});
		}

		function deleteTdef(id, event) {
			event.stopPropagation();
			event.preventDefault();
			vm.tdef = TypeDefinitions.get({ id: id });
			angular.element('#deleteTypeDefinitionConfirmation').modal('show');
		}

		function confirmDelete(id) {
			TypeDefinitions.delete({ id: id })
				.$promise
				.then(function () {
					vm.loadAll();
					angular.element('#deleteTypeDefinitionConfirmation').modal('hide');
					vm.clear();
				})
				.catch(function (resp) {
					vm.deleteError = resp.data.message;
				});
		}

		function clear() {
			vm.tdef = null;
			vm.search = {
				namespace: '',
				name: '',
				version: ''
			};
		}

		function clearDeleteError() {
			vm.deleteError = null;
		}

		function clearCreateError() {
			vm.createError = null;
		}

		function asArray(val) {
			return new Array(val);
		}

		function changeOrderBy(prop) {
			if (prop === vm.orderColumn) {
				vm.reverse = !vm.reverse;
				vm.orderClasses = {
					'glyphicon-chevron-up': vm.reverse,
					'glyphicon-chevron-down': !vm.reverse
				};
			}
			vm.orderColumn = prop;
		}

		function canDelete(tdef) {
			if (vm.user) {
				return vm.user.authorities.indexOf('ROLE_ADMIN') !== -1
					|| vm.user.namespaces.indexOf(tdef.namespace) !== -1;
			}
			return false;
		}

		function transition() {
			$state.transitionTo($state.$current, {
				page: vm.page.number,
				size: vm.page.size,
				sort: vm.page.sort
			});
		}

		function onLimitChanged(limit) {
			vm.page.size = limit;
			vm.transition();
		}
	});
