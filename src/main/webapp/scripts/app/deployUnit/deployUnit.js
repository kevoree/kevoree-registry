'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dus', {
                parent: 'site',
                url: '/dus',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/deployUnit/deployUnit.html',
                        controller: 'DeployUnitController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deployUnit');
                        return $translate.refresh();
                    }]
                }
            })
            .state('duDetail', {
                parent: 'dus',
                url: '/:namespace/:tdefName/:tdefVersion/:name/:version/:platform',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/deployUnit/deployUnit-detail.html',
                        controller: 'DeployUnitDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deployUnit');
                        return $translate.refresh();
                    }]
                }
            });
    });
