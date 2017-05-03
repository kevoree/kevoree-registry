'use strict';

angular.module('kevoreeRegistryApp')
	.factory('AuthServerProvider', function loginService($q, $http, $localStorage, Base64) {
		return {
			login: function (credentials) {
				var data = "username=" + credentials.username + "&password=" +
					credentials.password + "&grant_type=password&scope=read%20write&" +
					"client_secret=kevoree_registryapp_secret&client_id=kevoree_registryapp";
				return $http.post('oauth/token', data, {
					headers: {
						"Content-Type": "application/x-www-form-urlencoded",
						"Accept": "application/json",
						"Authorization": "Basic " + Base64.encode("kevoree_registryapp" + ':' + "kevoree_registryapp_secret")
					}
				}).success(function (response) {
					var expiredAt = new Date();
					expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
					response.expires_at = expiredAt.getTime();
					$localStorage.token = response;
					return response;
				});
			},
			logout: function () {
				// logout from the server
				return $http.post('api/logout').then(function () {
					delete $localStorage.token;
				});
			},
			refresh: function () {
				var token = this.getToken();
				if (token) {
					var refresh_token = token.refresh_token;

					var data = "grant_type=refresh_token" +
						"&refresh_token=" + refresh_token +
						"&client_id=kevoree_registryapp" +
						"&client_secret=kevoree_registryapp_secret";
					return $http.post('oauth/token', data, {
						headers: {
							"Content-Type": "application/x-www-form-urlencoded",
							"Accept": "application/json",
							"Authorization": "Basic " + Base64.encode("kevoree_registryapp" + ':' + "kevoree_registryapp_secret")
						}
					}).success(function (response) {
						var expiredAt = new Date();
						expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
						response.expires_at = expiredAt.getTime();
						$localStorage.token = response;
						return response;
					});
				} else {
					return $q.reject();
				}
			},
			getToken: function () {
				return $localStorage.token;
			},
			hasValidToken: function () {
				var token = this.getToken();
				return token && token.expires_at && token.expires_at > new Date().getTime();
			}
		};
	});
