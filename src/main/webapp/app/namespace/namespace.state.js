(function () {
  'use strict';

  angular.module('kevoreeRegistryApp')
    .config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
      .state('namespaces', {
        parent: 'site',
        url: '/namespaces',
        data: {
          authorities: []
        },
        views: {
          'content@': {
            templateUrl: 'app/namespace/namespaces.html',
            controller: 'NamespaceController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('namespace');
            return $translate.refresh();
          }]
        }
      })
      .state('namespaces.new', {
        parent: 'namespaces',
        url: '/new',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN']
        },
        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
          $uibModal.open({
            templateUrl: 'app/namespace/namespace-new.html',
            controller: 'NamespaceNewController',
            controllerAs: 'vm',
            bindToController: true,
            backdrop: 'static',
            size: 'md',
            resolve: {
              namespace: function () {
                return { name: null };
              }
            }
          }).result.then(function () {
            $state.go('namespaces', null, {
              reload: true
            });
          }, function () {
            $state.go('namespaces');
          });
        }]
      })
      .state('namespaces.delete', {
        parent: 'namespaces',
        url: '/delete/:name',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN']
        },
        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
          $uibModal.open({
            templateUrl: 'app/namespace/namespace-delete.html',
            controller: 'NamespaceDeleteController',
            controllerAs: 'vm',
            bindToController: true,
            backdrop: 'static',
            size: 'md'
          }).result.then(function () {
            $state.go('namespaces', null, { reload: true });
          }, function () {
            $state.go('namespaces');
          });
        }]
      })
      .state('namespaces.detail', {
        parent: 'namespaces',
        url: '/detail/:name',
        data: {
          authorities: []
        },
        views: {
          'content@': {
            templateUrl: 'app/namespace/namespace-detail.html',
            controller: 'NamespaceDetailController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('namespace');
            return $translate.refresh();
          }]
        }
      })
      .state('namespaces.detail.add-member', {
        parent: 'namespaces.detail',
        url: '/members/add',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN']
        },
        onEnter: ['$q', '$stateParams', '$state', '$uibModal', 'Principal', 'Namespace', 'AlertService', function ($q, $stateParams, $state, $uibModal, Principal, Namespace, AlertService) {
          $q.all([
            Principal.identity(),
            Namespace.get({ name: $stateParams.name }).$promise
          ]).then(function (results) {
            var user = results[0];
            var namespace = results[1];
            if (namespace.owner !== user.login) {
              $state.go('^', null, { reload: false })
                .then(function () {
                  AlertService.error('Only the owner of the namespace <strong>' + namespace.name + '</strong> can add/remove members');
                });
            } else {
              $uibModal.open({
                templateUrl: 'app/namespace/namespace-add-member.html',
                controller: 'NamespaceAddMemberController',
                controllerAs: 'vm',
                bindToController: true,
                backdrop: 'static',
                size: 'md',
                resolve: {
                  namespace: ['Namespace', function (Namespace) {
                    return Namespace.get({ name: $stateParams.name }).$promise;
                  }]
                }
              }).result.then(function () {
                $state.go('^', null, { reload: true });
              }, function () {
                $state.go('^');
              });
            }
          });
        }]
      })
      .state('namespaces.detail.remove-member', {
        parent: 'namespaces.detail',
        url: '/members/remove',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN']
        },
        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
          $uibModal.open({
            templateUrl: 'app/namespace/namespace-remove-member.html',
            controller: 'NamespaceRemoveMemberController',
            controllerAs: 'vm',
            bindToController: true,
            backdrop: 'static',
            size: 'md',
            resolve: {
              namespace: ['Namespace', function (Namespace) {
                return Namespace.get({ name: $stateParams.name }).$promise;
              }]
            }
          }).result.then(function () {
            $state.go('^', null, { reload: true });
          }, function () {
            $state.go('^');
          });
        }]
      });
  }
})();
