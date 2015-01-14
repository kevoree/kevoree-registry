'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('namespace', {
                parent: 'entity',
                url: '/namespace',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/namespace/namespaces.html',
                        controller: 'NamespaceController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('namespace');
                        return $translate.refresh();
                    }]
                }
            })
            .state('namespaceDetail', {
                parent: 'entity',
                url: '/namespace/:fqn',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/namespace/namespace-detail.html',
                        controller: 'NamespaceDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('namespace');
                        return $translate.refresh();
                    }]
                }
            });
    });
