(function() {
    'use strict';

    angular
        .module('kevoreeRegistryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('type-definition', {
            parent: 'entity',
            url: '/type-definition?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.typeDefinition.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-definition/type-definitions.html',
                    controller: 'TypeDefinitionController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeDefinition');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('type-definition-detail', {
            parent: 'entity',
            url: '/type-definition/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'kevoreeRegistryApp.typeDefinition.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-definition/type-definition-detail.html',
                    controller: 'TypeDefinitionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeDefinition');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TypeDefinition', function($stateParams, TypeDefinition) {
                    return TypeDefinition.get({id : $stateParams.id});
                }]
            }
        })
        .state('type-definition.new', {
            parent: 'type-definition',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-definition/type-definition-dialog.html',
                    controller: 'TypeDefinitionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                serializedModel: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('type-definition', null, { reload: true });
                }, function() {
                    $state.go('type-definition');
                });
            }]
        })
        .state('type-definition.edit', {
            parent: 'type-definition',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-definition/type-definition-dialog.html',
                    controller: 'TypeDefinitionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TypeDefinition', function(TypeDefinition) {
                            return TypeDefinition.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-definition', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('type-definition.delete', {
            parent: 'type-definition',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-definition/type-definition-delete-dialog.html',
                    controller: 'TypeDefinitionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TypeDefinition', function(TypeDefinition) {
                            return TypeDefinition.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-definition', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
