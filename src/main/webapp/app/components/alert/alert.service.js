(function () {
	angular
		.module('kevoreeRegistryApp')
		.provider('AlertService', function () {
			var defaultTimeout = 15000;

			this.setDefaultTimeout = function (timeout) {
				defaultTimeout = timeout;
			};

			this.$get = AlertService;
			this.$get.$inject = ['$timeout', '$sce', '$translate'];

			function AlertService($timeout, $sce, $translate) {
				var alerts = [];
				var timeout = defaultTimeout; // timeout defined by provider

				return {
					add: addAlert,
					clear: clear,
					get: get,
					success: success,
					error: error,
					info: info,
					warning: warning
				};

				function clear() {
					alerts = [];
				}

				function get() {
					return alerts;
				}

				function success(msg, params) {
					return addAlert('success', msg, params);
				}

				function error(msg, params) {
					return addAlert('danger', msg, params);
				}

				function warning(msg, params) {
					return addAlert('warning', msg, params);
				}

				function info(msg, params) {
					return addAlert('info', msg, params);
				}

				function addAlert(type, msg, params) {
					var alert = {
						type: type,
						msg: $sce.trustAsHtml($translate.instant(msg, params))
					};
					var timeoutPromise = $timeout(function () {
						alert.close();
					}, timeout);

					alert.close = function () {
						$timeout.cancel(timeoutPromise);
						alerts.splice(0);
					};

					if (alerts.length > 0) {
						alerts[0].close();
						alerts.splice(0);
					}
					alerts.push(alert);
					return alert;
				}
			}
		});
})();
