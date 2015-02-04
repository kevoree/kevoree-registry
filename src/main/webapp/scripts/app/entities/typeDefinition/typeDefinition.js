'use strict';

angular.module('kevoreeRegistryApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tdef', {
                parent: 'entity',
                url: '/tdef',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/typeDefinition/typeDefinitions.html',
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
                url: '/tdef/:namespace/:name/:version',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/typeDefinition/typeDefinition-detail.html',
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
