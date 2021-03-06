(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
			.state('home', {
  parent: 'site',
  url: '/',
  data: {
    authorities: []
  },
  views: {
    'content@': {
      templateUrl: 'app/home/home.html',
      controller: 'HomeController',
      controllerAs: 'vm'
    }
  },
  resolve: {
    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
      $translatePartialLoader.addPart('home');
      return $translate.refresh();
    }]
  }
});
  }

})();
