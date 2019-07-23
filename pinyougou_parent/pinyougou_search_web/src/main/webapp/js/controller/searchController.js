app.controller('searchController', function ($scope, searchService, $location) {
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//转换为数字
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPageLabel();
            }
        );
    }

    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sortField': '',
        'sort': ''
    };//搜索对象

    //添加面包屑查询条件选项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //添加条件时重新搜索
        $scope.search();
    }

    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        //删除条件时重新搜索
        $scope.search();
    }

    //构建分页标签
    buildPageLabel = function () {
        var maxPageNo = $scope.resultMap.totalPages;//得到页码数
        var firstPage = 1;//起始页码
        var lastPage = maxPageNo;//结束页码
        $scope.firstDot = true;//前边加点
        $scope.lastDot = true;//后边加点

        $scope.pageLabel = [];//页码标签

        if (maxPageNo > 5) {
            if ($scope.searchMap.pageNo <= 3) {//如果当前页码小于等于3设置结束页码
                lastPage = 5;//前五页
                $scope.firstDot = false;//前边没点
            } else if ($scope.searchMap.pageNo >= maxPageNo - 2) {//如果当前页码大于等于最大页码-2
                firstPage = maxPageNo - 4;//后五页
                $scope.lastDot = false; //后边没点
            } else {//当前页在中间时
                firstPage = $scope.searchMap.pageNo - 2;//前边两页
                lastPage = $scope.searchMap.pageNo + 2;//后边两页
            }
        } else {
            $scope.firstDot = false;//前边没点
            $scope.lastDot = false; //后边没点
        }

        //将页码存入页码标签数组
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        // var pageNo = parseInt(pageNo);
        //如果页码小于1或者大于总页码数直接返回
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        //设置页码
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }

    //判断当前页为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        }
    }

    //判断当前页为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        }
    }


    //排序查询
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        //调用查询方法
        $scope.search();
    }

    //判断关键字
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }

    //从主页跳转搜索页keywords
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
})