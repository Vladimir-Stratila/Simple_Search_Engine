package search

import java.io.File
import java.io.FileNotFoundException
import java.util.Scanner
import kotlin.collections.ArrayList

enum class Strategy() {
    ALL, ANY, NONE
}

fun main(args: Array<String>) {
    val people = ArrayList<List<String>>()
    if (args.size == 2) {
        if (args[0] == "--data") {
            val dataFile = File(args[1])
            try {
                val scanner = Scanner(dataFile)
                while (scanner.hasNextLine()) {
                    val line: String = scanner.nextLine()
                    people.add(line.split(" "))
                }
                scanner.close()
            } catch (e: FileNotFoundException) {
                println("File not found")
            }
        }
    } else {
        println("Enter the number of people:")
        val peopleNumber = readln().toInt()
        println("Enter all people:")
        for (i in 0 until peopleNumber) {
            people.add(readln().split(" "))
        }
    }
    val invertedIndex = buildIndex(people)
    while (true) {
        println("\n=== Menu ===")
        println("1. Find a person.")
        println("2. Print all people.")
        println("0. Exit.")

        when (readln().toInt()) {
            1 -> searchData(people, invertedIndex)
            2 -> printAll(people)
            0 -> break
            else -> println("\nIncorrect option! Try again.")
        }
    }
    println("\nBye!")
}

fun searchData(people: ArrayList<List<String>>, index: MutableMap<String, MutableList<Int>>) {
    println("\nSelect a matching strategy: ALL, ANY, NONE")
    var strategy = ""
    while (true) {
        strategy = readln()
        if (Strategy.values().joinToString().contains(strategy)) { // ?
            break
        }
    }

    println("\nEnter a name or email to search all matching people.")
    val query = readln().lowercase().split(" ")
    var resultSet = mutableSetOf<Int>()

    when (Strategy.valueOf(strategy)) {
        Strategy.ALL -> {
            for (word in query) {
                if (resultSet.isEmpty()) {
                    resultSet.addAll(index.filter { it.key == word }.values.flatten().toSet())
                } else {
                    resultSet = (resultSet intersect index.filter { it.key == word }.values.flatten().toSet()) as MutableSet<Int>
                }
            }
        }
        Strategy.ANY -> {
            for (word in query) {
                resultSet.addAll(index.filter { it.key == word }.values.flatten().toSet())
            }
        }
        Strategy.NONE -> {
            resultSet.addAll(index.values.flatten().toSet())
            for (word in query) {
                resultSet.removeAll(index.filter { it.key == word }.values.flatten().toSet())
            }
        }
    }
    printSearchResult(resultSet, people)
}

fun printSearchResult(resultSet: MutableSet<Int>, people: ArrayList<List<String>>) {
    val s = resultSet.size
    if (s == 0) {
        println("No matching people found.")
    } else {
        print("\n$s person")
        if (s > 1) print("s")
        println(" found:")
        for (i in resultSet) {
            println(people[i].joinToString(" "))
        }
    }
}

fun printAll(people: ArrayList<List<String>>) {
    println("\n=== List of people ===")
    for (p in people) println(p.joinToString(" "))
}

fun buildIndex(list: ArrayList<List<String>>): MutableMap<String, MutableList<Int>> {
    val index = mutableMapOf<String, MutableList<Int>>()
    for (i in list.indices) {
        for (j in list[i].indices) {
            if (index[list[i][j].lowercase()] == null) index[list[i][j].lowercase()] = mutableListOf(i) else index.getValue(list[i][j].lowercase()).add(i)
        }
    }
    return index
}