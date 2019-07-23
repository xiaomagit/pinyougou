app.controller('contentController',function ($scope, contentService) {

    $scope.contentList = [];//广告集合

    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        );
    }

    //跳转到查询页
    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
})