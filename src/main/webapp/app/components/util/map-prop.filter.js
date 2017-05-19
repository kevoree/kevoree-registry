'use strict';

/**
 * @param  {Array}  inputs an array of elements to reduce
 * @param  {string} prop   reduce elements using this property
 * @return {Array}
 */
function reducer(inputs, prop) {
  return inputs.map(function (input) {
    return input[prop];
  });
}

/**
 * mapProp([{ name: 'foo'}, { name: 'bar' }], 'name') => ['foo', 'bar']
 *
 * @return {Array} an array of the given property value
 */
function mapPropFilter() {
  return reducer;
}

angular.module('kevoreeRegistryApp')
	.filter('mapProp', mapPropFilter);
