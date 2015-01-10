(function () {
    var app = angular.module("settings", [ "ngResource", "ngRoute" ]);

    app.config(["$routeProvider", "$httpProvider",
      function($routeProvider, $httpProvider) {
        $httpProvider.interceptors.push('errorInterceptor');

        $routeProvider.
          when("/", {}).
          when("/:id", {controller:'detailController'}).
          otherwise({
            redirectTo: "/"
          });
      }]);

    app.controller("mainController", ["$scope", "$route", "$routeParams", "$location", "listService", "detailService",
        function ($scope, $route, $routeParams, $location, listService, detailService) {
        //TODO: $route excess. ngView missing.
        var vm = this;
        vm.items = [];
        vm.selectedId = null;
        vm.showDeleted = null;

        $scope.$on('$routeChangeSuccess', function(e, route, prev) {
            //TODO: double call (invalid routes)
            vm.selectedId = $routeParams.id;
            vm.showDeleted = Boolean($routeParams.deleted);
            refresh();

            $scope.$watch(function () {
                return vm.showDeleted;
            }, function (v) {
                var p = $location.search();
                if (v)
                {
                    p["deleted"] = +v;
                }
                else
                {
                    delete p["deleted"];
                }
                $location.path( "/" ).search(p);
            });
        });

        function refresh() {
            listService.query({deleted: +vm.showDeleted}, function (data) {
                vm.items = data.items;
            });
        };

        //$scope.$on("refresh", refresh);

        vm.Edit = function (item) {
            var p = $location.search();
            p["id"] = item.id;
            $location.path( "/" ).search(p);
        };

        vm.Delete = function (item) {
            if (confirm("Delete " + item.id + "?"))
            {
                detailService.delete({ id: item.id },
                    function (data) {
                        refresh();
                        $location.path( "/" );
                        toastr["info"](item.id, "Deleted");
                    });
            }
        };
    }]);

     app.controller("detailController", ["$scope", "$routeParams", "$location", "detailService",
        function ($scope, $routeParams, $location, detailService) {
        var vm = this;
        vm.item = {};

        $scope.$on('$routeChangeSuccess', function() {
            if ($routeParams.id){
                detailService.get({id: $routeParams.id}, function (data) {
                    vm.item = data;
                });
            }
            else
            {
                vm.item = {};
            }
        });

        vm.New = function() {
            var p = $location.search();
            delete p["id"];
            $location.path("/").search(p);
        };

        vm.Save = function () {
            detailService.save(vm.item, function (data) {
                //$scope.$emit("refresh");
                $location.path("/" + data.id);
                toastr["info"](data.id, "Saved");
            });
        };
    }]);

    app.controller("logController", ["$scope", "logService", "detailService",
        function ($scope, logService) {
        var vm = this;
        vm.log = [];
        vm.source = null;
        vm.sources = [{id: 1, value: "API"}]
        vm.lastDate = new Date().getTime()/1000;

        $scope.$watch(
            function() { return vm.source; },
            function() {
                vm.log = [];
                vm.lastDate = new Date().getTime()/1000;
                loadFromDate();
            }
            );

        function loadFromDate() {
            logService.query({date: vm.lastDate, limit: 50, source: vm.source}, function (data) {
                if (data.items.length > 0){
                    data.items.forEach(function (item) { vm.log.push(item); });
                    vm.lastDate = data.items[data.items.length - 1].date;
                }
            });
        };

        //loadFromDate();

        vm.loadMore = function() {
            loadFromDate();
        };
    }]);

    app.factory("listService", ["$resource",
      function ($resource) {
          return $resource("/api/list", {}, {
              query: { method: "GET", isArray: false, cache: false },
          });
      }]);

    app.factory("detailService", ["$resource",
      function ($resource) {
          return $resource("/api/detail", {}, {
              query: { method: "GET", isArray: false, cache: false },
              save: { method: "POST", isArray: false, cache: false },
              delete: { method: "DELETE", isArray: false, cache: false },
          });
      }]);

     app.factory("logService", ["$resource",
      function ($resource) {
          return $resource("/api/log", {}, {
              query: { method: "GET", isArray: false, cache: false },
          });
      }]);

    app.factory('errorInterceptor', ["$q", function($q) {
    return {
        'requestError': function(rejection) {
            toastr["error"](rejection.data, rejection.statusText);
            return $q.reject(rejection);
        },

        'responseError': function(rejection) {
            if (!rejection.status)
            {
                toastr["error"]("Failed to connect");
            }
            else
            {
                toastr["error"](rejection.data, rejection.statusText);
            }
            return $q.reject(rejection);
        }
        };
    }]);
})();