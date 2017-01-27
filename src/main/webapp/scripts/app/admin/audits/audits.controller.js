'use strict';

angular
	.module('kevoreeRegistryApp')
	.controller('AuditsController', function ($scope, $translate, $filter, AuditsService, ParseLinks) {
		$scope.audits = null;
		$scope.fromDate = null;
		$scope.links = null;
		$scope.loadPage = loadPage;
		$scope.onChangeDate = onChangeDate;
		$scope.page = 1;
		$scope.previousMonth = previousMonth;
		$scope.toDate = null;
		$scope.today = today;
		$scope.totalItems = null;

		$scope.today();
		$scope.previousMonth();
		$scope.onChangeDate();

		function onChangeDate() {
			var dateFormat = 'yyyy-MM-dd';
			var fromDate = $filter('date')($scope.fromDate, dateFormat);
			var toDate = $filter('date')($scope.toDate, dateFormat);

			AuditsService.query({
				page: $scope.page - 1,
				size: 20,
				fromDate: fromDate,
				toDate: toDate
			}, function (result, headers) {
				$scope.audits = result;
				$scope.links = ParseLinks.parse(headers('link'));
				$scope.totalItems = headers('X-Total-Count');
			});
		}

		// Date picker configuration
		function today() {
			// Today + 1 day - needed if the current day must be included
			var today = new Date();
			$scope.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
		}

		function previousMonth() {
			var fromDate = new Date();
			if (fromDate.getMonth() === 0) {
				fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
			} else {
				fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
			}

			$scope.fromDate = fromDate;
		}

		function loadPage(page) {
			$scope.page = page;
			$scope.onChangeDate();
		}
	});
