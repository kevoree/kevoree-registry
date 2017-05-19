(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
			.state('error', {
  parent: 'site',
  url: '/error',
  data: {
    authorities: []
  },
  views: {
    'content@': {
      templateUrl: 'app/error/error.html'
    }
  },
  resolve: {
    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
      $translatePartialLoader.addPart('error');
      return $translate.refresh();
    }]
  }
})
			.state('accessdenied', {
  parent: 'site',
  url: '/accessdenied',
  data: {
    authorities: []
  },
  views: {
    'content@': {
      templateUrl: 'app/error/accessdenied.html'
    }
  },
  resolve: {
    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
      $translatePartialLoader.addPart('error');
      return $translate.refresh();
    }]
  }
});
  }
})();
