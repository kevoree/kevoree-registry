'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tdefs', {
                parent: 'site',
                url: '/tdefs/:namespace?/:name?',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/typeDefinition/typeDefinitions.html',
                        controller: 'TypeDefinitionController'
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
                parent: 'entity',
                url: '/tdefs/:namespace/:name?/:version?',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/typeDefinition/typeDefinition-detail.html',
                        controller: 'TypeDefinitionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('typeDefinition');
                        return $translate.refresh();
                    }]
                }
            });
    });
