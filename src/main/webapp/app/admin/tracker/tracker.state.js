(function () {
  'use strict';

  angular
    .module('kevoreeRegistryApp')
    .config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
      .state('tracker', {
        parent: 'admin',
        url: '/tracker',
        data: {
          authorities: ['ROLE_ADMIN']
        },
        views: {
          'content@': {
            templateUrl: 'app/admin/tracker/tracker.html',
            controller: 'TrackerController',
            controllerAs: 'vm'
          }
        },
        resolve: {
          mainTranslatePartialLoader: function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('tracker');
            return $translate.refresh();
          }
        },
        onEnter: ['Tracker', function (Tracker) {
          Tracker.subscribe();
        }],
        onExit: ['Tracker', function (Tracker) {
          Tracker.unsubscribe();
        }]
      });
  }
})();
