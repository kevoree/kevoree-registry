(function () {
  'use strict';

  angular
		.module('kevoreeRegistryApp')
		.config(config);

  config.$inject = ['uibPaginationConfig', 'paginationConstants'];

  function config(uibPaginationConfig, paginationConstants) {
    uibPaginationConfig.itemsPerPage = paginationConstants.itemsPerPage;
    uibPaginationConfig.maxSize = 5;
    uibPaginationConfig.boundaryLinks = true;
    uibPaginationConfig.firstText = '«';
    uibPaginationConfig.previousText = '‹';
    uibPaginationConfig.nextText = '›';
    uibPaginationConfig.lastText = '»';
  }
})();
