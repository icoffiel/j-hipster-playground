(function() {
    'use strict';
    angular
        .module('jHipsterPlaygroundApp')
        .factory('RefreshToken', RefreshToken);

    RefreshToken.$inject = ['$resource'];

    function RefreshToken ($resource) {
        var resourceUrl =  'api/refresh-tokens/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
