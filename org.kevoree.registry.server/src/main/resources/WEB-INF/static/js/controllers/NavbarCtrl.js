// Created by leiko on 26/11/14 17:06

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('NavbarCtrl', ['$scope', function($scope) {
        $scope.isCollapsed = true;

        $scope.toggleDropdown = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
        };
    }]);