(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
      .state('reset-request', {
        parent: 'account',
        url: '/reset/request',
        data: {
          authorities: []
        },
        views: {
          'content@': {
            templateUrl: 'app/account/reset/request/reset-request.html',
            controller: 'ResetRequestController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('reset');
            return $translate.refresh();
          }]
        }
      });
  }
})();
