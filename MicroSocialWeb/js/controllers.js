var microSocialApp = angular.module('microSocial', ['ngAnimate']);

microSocialApp.controller('LoginController', function ($scope, $http, $log, $location)
{
    var baseUrl = $location.absUrl();
    baseUrl = baseUrl.substring(0, baseUrl.length - 1);

    $scope.user = {};
    $scope.registerForm = false;

    $scope.submitForm = function()
    {
        // try to login with the provided data
        $http({
              method: "POST",
              url: baseUrl + ":9000" + "/session",
//              port: 9000,
              data: {
                  "username": $scope.user.username,
                  "password": $scope.user.password
              }
            })
            .success(function(data, status, headers, config) {
//                $scope.data = data;
                $log.info(data);

            }).error(function(data, status, headers, config) {
//                $scope.status = status;
                $log.info(data);
            });
    }

    $scope.showRegister = function()
    {
        if ($scope.registerForm) $scope.registerForm = false;
        else $scope.registerForm = true;
    }
});