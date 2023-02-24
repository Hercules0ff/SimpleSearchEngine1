package search

import java.io.File

const val PEOPLE_NOT_FOUND = "No matching people found."
const val ENTER_DATA_TO_SEARCH = "\nEnter a name or email to search all suitable people."
fun main(args: Array<String>) {
    if(args.contains("--data")) {
        val peopleList = File(args.last()).readLines().toMutableList()
        while (true) {
            menuPrint()
            val str = readln()
            if (str == "1") {
                println("\nSelect a matching strategy: ALL, ANY, NONE")
                val strategy = readln().uppercase()
                println(ENTER_DATA_TO_SEARCH)
                createIndexMap(peopleList)
                peopleSearch(peopleList, createIndexMap(peopleList), readln(), strategy)
            } else if (str == "2") {
                printAllPeople(peopleList)
            } else if (str == "0") {
                println("\nBye!")
                break
            } else {
                println("\nIncorrect option! Try again.")
            }
        }
    }
}

fun peopleSearch(_data: MutableList<String>, _peopleList: Map<String, MutableSet<Int>>, _searchingWord: String, _searchStrategy: String) {
    val wordsArray = _searchingWord.split(" ").map { it.lowercase() }
    when(_searchStrategy) {
        Strategies.ANY.name -> {
            var result = mutableSetOf<Int>()
            for (i in wordsArray.indices) {
                var found = mutableSetOf<Int>()
                if (_peopleList.containsKey(wordsArray[i])) {
                    found = _peopleList[wordsArray[i]]!!
                }
                if (i == 0) {
                    result.addAll(found)
                } else {
                    result = result.union(found).toMutableSet()
                }
            }
            if (result.isEmpty()) println(PEOPLE_NOT_FOUND)
            else {
                println("${result.size} persons found:")
                result.forEach { println(_data[it]) }
            }
        }

        Strategies.ALL.name -> {
            var result = mutableSetOf<Int>()
            for (i in wordsArray.indices) {
                var found = mutableSetOf<Int>()
                if (_peopleList.containsKey(wordsArray[i])) {
                    found = _peopleList[wordsArray[i]]!!
                }
                if (i == 0) {
                    result.addAll(found)
                } else {
                    result = result.intersect(found).toMutableSet()
                }
            }
            if (result.isEmpty()) println(PEOPLE_NOT_FOUND)
            else {
                println("${result.size} persons found:")
                result.forEach { println(_data[it]) }
            }
        }

        Strategies.NONE.name -> {
            var result = _peopleList.values.flatten().toMutableSet()
            for (i in wordsArray.indices) {
                var found = mutableSetOf<Int>()
                if (_peopleList.containsKey(wordsArray[i])) {
                    found = _peopleList[wordsArray[i]]!!
                }
                result = result.subtract(found).toMutableSet()
            }
            if (result.isEmpty()) println(PEOPLE_NOT_FOUND)
            else {
                println("${result.size} persons found:")
                result.forEach { println(_data[it]) }
            }
        }
    }
}

fun printAllPeople(_peopleList: MutableList<String>) {
    println("\n=== List of people ===")
    println(_peopleList.joinToString("\n"))
}

fun menuPrint() {
    println("\n=== Menu ===\n" +
            "1. Find a person\n" +
            "2. Print all people\n" +
            "0. Exit")
}

private fun createIndexMap(_peopleList: MutableList<String>): Map<String, MutableSet<Int>> {
    val indexMap = mutableMapOf<String, MutableSet<Int>>()
    _peopleList.forEachIndexed() { index, entry ->
        entry.split(" ")
                .forEach { indexMap.getOrPut(it.lowercase()) { mutableSetOf() }.add(index) }
    }
    return indexMap
}


enum class Strategies {
    ALL, ANY, NONE
}