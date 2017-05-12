(function () {
	'use strict';

	angular
		.module('kevoreeRegistryApp')
		.factory('Tracker', Tracker);

		Tracker.$inject = ['$rootScope', '$window', '$q', '$localStorage'];

		function Tracker($rootScope, $window, $q, $localStorage) {
			var REGEX = /:([^/]+)/g;
			var stompClient = null;
			var subscriber = null;
			var listener = $q.defer();
			var connected = $q.defer();
			var alreadyConnectedOnce = false;

			var service = {
				connect: connect,
				disconnect: disconnect,
				receive: receive,
				sendActivity: sendActivity,
				subscribe: subscribe,
				unsubscribe: unsubscribe
			};

			return service;

			function connect() {
				//building absolute path so that websocket doesnt fail when deploying with a context path
				var loc = $window.location;
				var url = '//' + loc.host + loc.pathname + 'websocket/tracker';
				var authToken = angular.fromJson($localStorage.token).access_token;
				url += '?access_token=' + authToken;
				var socket = new SockJS(url);
				stompClient = Stomp.over(socket);
				var stateChangeStart;
				var headers = {};
				stompClient.connect(headers, function () {
					connected.resolve('success');
					sendActivity();
					if (!alreadyConnectedOnce) {
						stateChangeStart = $rootScope.$on('$stateChangeStart', function () {
							sendActivity();
						});
						alreadyConnectedOnce = true;
					}
				});
				$rootScope.$on('$destroy', function () {
					if (angular.isDefined(stateChangeStart) && stateChangeStart !== null) {
						stateChangeStart();
					}
				});
			}

			function disconnect() {
				if (stompClient !== null) {
					stompClient.disconnect();
					stompClient = null;
				}
			}

			function receive() {
				return listener.promise;
			}

			function sendActivity() {
				if (stompClient !== null && stompClient.connected) {
					var page = $rootScope.toState.name;
					var url = $rootScope.toState.url;
					if (REGEX.test(url)) {
						REGEX.lastIndex = 0;
						var matches = [];
						var match = REGEX.exec(url);
						while (match !== null) {
							matches.push(match[1]);
							match = REGEX.exec(url);
						}
						matches.forEach(function (paramName) {
							page += url.replace(':' + paramName, $rootScope.toStateParams[paramName]);
						});
					}
					stompClient
						.send('/topic/activity', {},
							angular.toJson({
								'page': page
							}));
				}
			}

			function subscribe() {
				connected.promise.then(function () {
					subscriber = stompClient.subscribe('/topic/tracker', function (data) {
						listener.notify(angular.fromJson(data.body));
					});
				}, null, null);
			}

			function unsubscribe() {
				if (subscriber !== null) {
					subscriber.unsubscribe();
				}
				listener = $q.defer();
			}
		}
})();
