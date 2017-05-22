(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
      .state('health', {
        parent: 'admin',
        url: '/health',
        data: {
          authorities: ['ROLE_ADMIN']
        },
        views: {
          'content@': {
            templateUrl: 'app/admin/health/health.html',
            controller: 'HealthController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('health');
            return $translate.refresh();
          }]
        }
      });
  }
})();
