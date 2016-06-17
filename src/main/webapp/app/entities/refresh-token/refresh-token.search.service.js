(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .factory('RefreshTokenSearch', RefreshTokenSearch);

    RefreshTokenSearch.$inject = ['$resource'];

    function RefreshTokenSearch($resource) {
        var resourceUrl =  'api/_search/refresh-tokens/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
