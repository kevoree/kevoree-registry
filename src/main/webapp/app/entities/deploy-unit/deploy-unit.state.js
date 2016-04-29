(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('deploy-unit', {
            parent: 'entity',
            url: '/deploy-unit',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.deployUnit.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/deploy-unit/deploy-units.html',
                    controller: 'DeployUnitController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('deployUnit');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('deploy-unit-detail', {
            parent: 'entity',
            url: '/deploy-unit/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.deployUnit.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/deploy-unit/deploy-unit-detail.html',
                    controller: 'DeployUnitDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('deployUnit');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'DeployUnit', function($stateParams, DeployUnit) {
                    return DeployUnit.get({id : $stateParams.id});
                }]
            }
        })
        .state('deploy-unit.new', {
            parent: 'deploy-unit',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/deploy-unit/deploy-unit-dialog.html',
                    controller: 'DeployUnitDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                platform: null,
                                path: null,
                                priority: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('deploy-unit', null, { reload: true });
                }, function() {
                    $state.go('deploy-unit');
                });
            }]
        })
        .state('deploy-unit.edit', {
            parent: 'deploy-unit',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/deploy-unit/deploy-unit-dialog.html',
                    controller: 'DeployUnitDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DeployUnit', function(DeployUnit) {
                            return DeployUnit.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('deploy-unit', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('deploy-unit.delete', {
            parent: 'deploy-unit',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/deploy-unit/deploy-unit-delete-dialog.html',
                    controller: 'DeployUnitDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DeployUnit', function(DeployUnit) {
                            return DeployUnit.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('deploy-unit', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
