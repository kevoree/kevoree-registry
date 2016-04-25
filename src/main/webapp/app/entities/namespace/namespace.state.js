(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('namespace', {
            parent: 'entity',
            url: '/namespace',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.namespace.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/namespace/namespaces.html',
                    controller: 'NamespaceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('namespace');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('namespace-detail', {
            parent: 'entity',
            url: '/namespace/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.namespace.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/namespace/namespace-detail.html',
                    controller: 'NamespaceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('namespace');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Namespace', function($stateParams, Namespace) {
                    return Namespace.get({id : $stateParams.id});
                }]
            }
        })
        .state('namespace.new', {
            parent: 'namespace',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/namespace/namespace-dialog.html',
                    controller: 'NamespaceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('namespace', null, { reload: true });
                }, function() {
                    $state.go('namespace');
                });
            }]
        })
        .state('namespace.edit', {
            parent: 'namespace',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/namespace/namespace-dialog.html',
                    controller: 'NamespaceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Namespace', function(Namespace) {
                            return Namespace.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('namespace', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('namespace.delete', {
            parent: 'namespace',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/namespace/namespace-delete-dialog.html',
                    controller: 'NamespaceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Namespace', function(Namespace) {
                            return Namespace.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('namespace', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
