'use strict';

angular.module('kevoreeRegistryApp').factory('Tracker', function($rootScope) {
  var stompClient = null;
  function sendActivity() {
    stompClient.send('/websocket/activity', {}, JSON.stringify({ 'page': $rootScope.toState.name }));

  }
  return {
    connect: function() {
      var basePath = window.location.pathname;
      if (!basePath.endsWith('/')) {
        basePath += '/';
      }
      var socket = new SockJS(basePath + 'websocket/activity');
      stompClient = Stomp.over(socket);
      stompClient.debug = function () {};
      stompClient.connect({}, function() {
        sendActivity();
        $rootScope.$on('$stateChangeStart', function() {
          sendActivity();
        });
      });
    },
    sendActivity: function() {
      console.log(stompClient);
      if (stompClient != null) {
        sendActivity();
      }
    },
    disconnect: function() {
      if (stompClient != null) {
        stompClient.disconnect();
        stompClient = null;
      }
    }
  };
});
