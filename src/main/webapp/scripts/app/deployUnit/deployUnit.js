'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dus', {
                parent: 'site',
                url: '/dus/:namespace?/:tdef?/:tdefVersion?/:name?',
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
                parent: 'entity',
                url: '/dus/:namespace/:tdef/:tdefVersion/:name?/:version?',
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
