'use strict';

angular
	.module('kevoreeRegistryApp')
	.config(function (uibPaginationConfig, paginationConstants) {
		uibPaginationConfig.itemsPerPage = paginationConstants.itemsPerPage;
		uibPaginationConfig.maxSize = 5;
		uibPaginationConfig.boundaryLinks = true;
		uibPaginationConfig.firstText = '«';
		uibPaginationConfig.previousText = '‹';
		uibPaginationConfig.nextText = '›';
		uibPaginationConfig.lastText = '»';
	});
