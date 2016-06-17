(function() {
    'use strict';

    angular
        .module('jHipsterPlaygroundApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('refresh-token', {
            parent: 'entity',
            url: '/refresh-token?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jHipsterPlaygroundApp.refreshToken.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/refresh-token/refresh-tokens.html',
                    controller: 'RefreshTokenController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('refreshToken');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('refresh-token-detail', {
            parent: 'entity',
            url: '/refresh-token/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jHipsterPlaygroundApp.refreshToken.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/refresh-token/refresh-token-detail.html',
                    controller: 'RefreshTokenDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('refreshToken');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'RefreshToken', function($stateParams, RefreshToken) {
                    return RefreshToken.get({id : $stateParams.id});
                }]
            }
        })
        .state('refresh-token.new', {
            parent: 'refresh-token',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/refresh-token/refresh-token-dialog.html',
                    controller: 'RefreshTokenDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                token: null,
                                expired: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('refresh-token', null, { reload: true });
                }, function() {
                    $state.go('refresh-token');
                });
            }]
        })
        .state('refresh-token.edit', {
            parent: 'refresh-token',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/refresh-token/refresh-token-dialog.html',
                    controller: 'RefreshTokenDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RefreshToken', function(RefreshToken) {
                            return RefreshToken.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('refresh-token', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('refresh-token.delete', {
            parent: 'refresh-token',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/refresh-token/refresh-token-delete-dialog.html',
                    controller: 'RefreshTokenDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RefreshToken', function(RefreshToken) {
                            return RefreshToken.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('refresh-token', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
