(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
      .state('tdefs', {
        parent: 'site',
        url: '/tdefs?page?size?sort',
        data: {
          authorities: []
        },
        views: {
          'content@': {
            templateUrl: 'app/typeDefinition/typeDefinitions.html',
            controller: 'TypeDefinitionController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('typeDefinition');
            return $translate.refresh();
          }]
        }
      })
      .state('tdefDetail', {
        parent: 'tdefs',
        url: '/:id',
        data: {
          authorities: []
        },
        views: {
          'content@': {
            templateUrl: 'app/typeDefinition/typeDefinition-detail.html',
            controller: 'TypeDefinitionDetailController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('typeDefinition');
            return $translate.refresh();
          }]
        }
      })
      .state('tdefs.delete', {
        parent: 'tdefs',
        url: '/delete/:id',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN']
        },
        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
          $uibModal.open({
            templateUrl: 'app/typeDefinition/typeDefinition-delete.html',
            controller: 'TypeDefinitionDeleteController',
            controllerAs: 'vm',
            bindToController: true,
            backdrop: 'static',
            size: 'md'
          }).result.then(function () {
            $state.go('tdefs', $stateParams, { reload: false });
          }, function () {
            $state.go('tdefs');
          });
        }]
      });
  }
})();
