// Created by leiko on 26/11/14 17:06
var kevoree = require('kevoree-library').org.kevoree;

/**
 *
 */
angular.module('kevoreeRegistry')
    .controller('Model', ['$scope', 'modelFactory', function($scope, modelFactory) {
        $scope.relativePath = window.location.pathname;

        modelFactory.getModel($scope.relativePath)
            .success(function (data) {
                $scope.model = data;

                // process path
                var relativePath = $scope.relativePath;
                if (relativePath === '' ) {
                    relativePath = '/';
                } else {
                    if (!endsWith(relativePath, '/')) {
                        relativePath += '/';
                    }
                }
                var previousPath = relativePath;
                if (previousPath.length > 2) {
                    var previous = relativePath.substring(0, relativePath.length - 2);
                    previous = previous.substring(0, previous.lastIndexOf('/'));
                    previousPath = previous;
                }
                if (previousPath === '') {
                    previousPath = '/';
                }

                var currentPath = $scope.relativePath;
                if (!endsWith(currentPath, '/')) {
                    currentPath += '/';
                }

                // select path
                var paths = $scope.relativePath.split("/");
                var path = '/';
                for (var i=0; i < paths.length; i++) {
                    if (paths[i] !== "") {
                        path = '/*[' + path + ']';
                    }
                }

                var factory = new kevoree.factory.DefaultKevoreeFactory();
                var loader = factory.createJSONLoader();
                var model = loader.loadModelFromString(JSON.stringify(data)).get(0);
                var elements = model.select(path);

                $scope.elements = [];
                $scope.children = [];
                elements.array.forEach(function (elem) {
                    var elemData = {};
                    elemData['name'] = '['+elem.metaClassName()+'] '+elem.path();

                    var attributes = [];
                    var attrVisitor = new kevoree.modeling.api.util.ModelVisitor();
                    attrVisitor.visit = function (value, name) {
                        if (value === null) { value = ''; }
                        attributes.push(name + ': ' + value.toString());
                    };

                    var notContVisitor = new kevoree.modeling.api.util.ModelVisitor();
                    notContVisitor.visit = function (elem, refNameInParent) {
                        attributes.push(refNameInParent + ': ' + elem.path());
                    };

                    var contVisitor = new kevoree.modeling.api.util.ModelVisitor();
                    contVisitor.visit = function (elem) {
                        $scope.children.push({
                            key: elem.getRefInParent() + '[' + elem.internalGetKey() + ']',
                            link: currentPath + elem.internalGetKey()
                        });
                    };

                    elem.visitAttributes(attrVisitor);
                    elem.visitContained(contVisitor);
                    elem.visitNotContained(notContVisitor);

                    elemData['attributes'] = attributes;
                    $scope.elements.push(elemData);
                });

                $scope.previousPath = previousPath;
            })
            .error(function (err, status) {
                $scope.model = null;
                if (err.error) {
                    $scope.error = err.error;
                } else {
                    $scope.error = 'Something went wrong (status code '+status+')';
                }
                console.error(err);
            });

        function endsWith(str, suffix) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        }
    }]);