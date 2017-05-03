angular
	.module('kevoreeRegistryApp')
	.factory('errorHandlerInterceptor', function ($q, $window, AlertService) {
		var service = {
			responseError: responseError
		};

		return service;

		function responseError(response) {
			var headers;
			if (response.status >= 400 && response.status < 500) {
				if (!(response.status === 401 && (response.data === '' || (response.data.path && response.data.path.indexOf('/api/account') === 0)))) {
					switch (response.status) {
						// connection refused, server not reachable
						case 0:
							AlertService.error('error.server.not.reachable');
							break;

						case 401:
							AlertService.warning('global.messages.account.inactivy');
							break;

						case 400:
							headers = Object.keys(response.headers()).filter(function (header) {
								return header.indexOf('app-error', header.length - 'app-error'.length) !== -1 || header.indexOf('app-params', header.length - 'app-param'.length) !== -1;
							}).sort();

							if (headers.length > 0) {
								console.log(headers);
								// var errorParams = response.headers(headers[1]);
								AlertService.error(response.headers(headers[0]));
							}
							break;

						// case 404:
						// 	headers = Object.keys(response.headers()).filter(function (header) {
						// 		return header.indexOf('app-error', header.length - 'app-error'.length) !== -1 || header.indexOf('app-params', header.length - 'app-param'.length) !== -1;
						// 	}).sort();
						//
						// 	if (headers.length > 0) {
						// 		console.log(headers);
						// 		// var errorParams = response.headers(headers[1]);
						// 		AlertService.error(response.headers(headers[0]));
						// 	} else {
						// 		if (response.data && response.data.message) {
						// 			AlertService.error(response.data.message);
						// 		} else {
						// 			AlertService.error('error.code.404', { url: new URL($window.location.origin + '/' + response.config.url).pathname });
						// 		}
						// 	}
						// 	break;

						default:
							AlertService.error('error.code.' + response.status, { url: new URL($window.location.origin + '/' + response.config.url).pathname });
					}
				}
			}

			return $q.reject(response);
		}
	});
