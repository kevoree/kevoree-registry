(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(config);

  config.$inject = ['uibPagerConfig', 'paginationConstants'];

  function config(uibPagerConfig, paginationConstants) {
    uibPagerConfig.itemsPerPage = paginationConstants.itemsPerPage;
    uibPagerConfig.previousText = '«';
    uibPagerConfig.nextText = '»';
  }
})();
