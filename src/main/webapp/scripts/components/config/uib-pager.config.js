'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function (uibPagerConfig, paginationConstants) {
		uibPagerConfig.itemsPerPage = paginationConstants.itemsPerPage;
		uibPagerConfig.previousText = '«';
		uibPagerConfig.nextText = '»';
	});
