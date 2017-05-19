(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
			.state('logout', {
  parent: 'account',
  url: '/logout',
  data: {
    authorities: []
  },
  views: {
    'content@': {
      templateUrl: 'app/home/home.html',
      controller: 'LogoutController',
      controllerAs: 'vm'
    }
  }
});
  }
})();
