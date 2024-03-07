package com.example.sharedews

object TestFile {
    val testList: List<Task> by lazy {
        mutableListOf(
            Task("Test Task 1", "Description 1:" +
                    "testtestestestestestestestest" +
                    "\ntestsetsatstestsetsettes", false, "testListDocumentId1"),
            Task("Test Task 2", "Description 2", false, "testListDocumentId2"),
            Task("Test Task 3", "Description 3", false, "testListDocumentId3")
        )
    }
}