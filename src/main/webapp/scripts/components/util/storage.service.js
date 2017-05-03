angular.module('kevoreeRegistryApp')
	.factory('StorageService', function ($window) {
		return {
			get: function (key) {
				return angular.fromJson($window.localStorage.getItem(key));
			},

			save: function (key, data) {
				$window.localStorage.setItem(key, angular.toJson(data));
			},

			remove: function (key) {
				$window.localStorage.removeItem(key);
			},

			clearAll: function () {
				$window.localStorage.clear();
			}
		};
	});
