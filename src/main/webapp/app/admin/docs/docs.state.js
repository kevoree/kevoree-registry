(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(stateConfig);

  stateConfig.$inject = ['$stateProvider'];

  function stateConfig($stateProvider) {
    $stateProvider
			.state('docs', {
  parent: 'admin',
  url: '/docs',
  data: {
    authorities: ['ROLE_ADMIN']
  },
  views: {
    'content@': {
      templateUrl: 'app/admin/docs/docs.html'
    }
  }
});
  }
})();
