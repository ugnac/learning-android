package com.learn.kotlin

class FunctionApi {
    fun test() {
        val list = listOf(1, 2, 3, 4)

        // filter 过滤
        println(list.filter { it % 2 == 0 }) // [2, 4]

        // map 映射
        println(list.map { it * it }) // [1, 4, 9, 16]

        // all 是否所有都符合
        println(list.all { it == 3 }) // false

        // any 是否有一个符合
        println(list.any { it == 3 }) // true

        // none 是否都不符合
        println(list.none { it == 3 }) // false

        // count 统计符合条件的数据数量
        println(list.count { it >= 3 }) // 2

        // find 查找符合条件的第一个数据
        println(list.find { it >= 3 }) // 3

        // findLast 查找符合条件的最后一个数据
        println(list.findLast { it >= 3 }) // 4

        // first 查找符合条件的第一个数据
        println(list.first { it >= 3 }) // 3

        // last 查找符合条件的最后一个数据
        println(list.last { it >= 3 }) // 4

        // groupBy 按条件分组数据成一个map
        println(list.groupBy { it % 2 == 0 }) // [even = [2, 4], odd = [1, 3, 5]]

        val list2 = listOf("a", "ab", "b")

        // groupBy 按条件分组数据成一个map
        println(list2.groupBy(String::first)) // [a = ["a", "ab"], b = ["b"]]

        val list3 = listOf("abc", "def")

        // flatMap 映射并平铺
        println(list3.flatMap { it.toList() }) // [a, b, c, d, e, f]

        // flatten 平铺
        println(list3.map { it.toList() }.flatten()) // [a, b, c, d, e, f]


    }
}

fun main() {
    val functionApi = FunctionApi()
    functionApi.test()
}