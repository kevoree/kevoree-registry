'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('namespace', {
                parent: 'entity',
                url: '/ns',
                data: {
                    roles: []
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
                url: '/ns/:name',
                data: {
                    roles: []
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
