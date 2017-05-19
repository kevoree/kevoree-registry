'use strict';

angular.module('kevoreeRegistryApp')
	.controller('DeployUnitController', DeployUnitController);

DeployUnitController.$inject = ['$q', '$rootScope', '$state', '$stateParams', 'paginationConstants', 'DeployUnits', 'Principal'];

function DeployUnitController($q, $rootScope, $state, $stateParams, paginationConstants, DeployUnits, Principal) {
  var vm = this;
  vm.search = {
    name: '',
    version: '',
    platform: '',
  };
  vm.page = null;
  vm.orderColumn = 'name';
  vm.reverse = false;
  vm.orderClasses = {
    'glyphicon-chevron-up': vm.reverse,
    'glyphicon-chevron-down': !vm.reverse
  };

  vm.loadAll = function () {
    $stateParams.page = $stateParams.page || 0;
    $stateParams.size = $stateParams.size || paginationConstants.itemsPerPage;
    $q.all([
      Principal.identity(),
      DeployUnits.get(Object.assign({ id: 'page' }, $stateParams)).$promise
    ]).then(function (results) {
      vm.user = results[0];
      vm.page = results[1];
    });
  };
  vm.loadAll();

  vm.canDelete = function (du) {
    if (vm.user) {
      return vm.user.authorities.indexOf('ROLE_ADMIN') !== -1 ||
        vm.user.namespaces.indexOf(du.namespace) !== -1;
    }
    return false;
  };

  vm.isMember = function (deployUnit) {
    return $rootScope.user && deployUnit.typeDefinition.namespace.members.some(function (member) {
      return $rootScope.user.login === member.login;
    });
  };

  vm.clear = function () {
    vm.du = null;
    vm.search = {
      name: '',
      version: '',
      platform: ''
    };
  };

  vm.asArray = function (val) {
    return new Array(val);
  };

  vm.changeOrderBy = function (prop) {
    if (prop === vm.orderColumn) {
      vm.reverse = !vm.reverse;
      vm.orderClasses = {
        'glyphicon-chevron-up': vm.reverse,
        'glyphicon-chevron-down': !vm.reverse
      };
    }
    vm.orderColumn = prop;
  };

  vm.transition = function () {
    $state.transitionTo($state.$current, {
      page: vm.page.number,
      size: vm.page.size,
      sort: vm.page.sort
    });
  };

  vm.onLimitChanged = function (limit) {
    vm.page.size = limit;
    vm.transition();
  };
}
