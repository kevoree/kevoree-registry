'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('namespaces', {
                parent: 'site',
                url: '/ns',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/namespace/namespaces.html',
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
            .state('nsDetail', {
                parent: 'namespaces',
                url: '/:name',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/namespace/namespace-detail.html',
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