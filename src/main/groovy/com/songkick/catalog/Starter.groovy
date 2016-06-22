package com.songkick.catalog


class Starter {
    String className, testName

    Starter(String className, String testName) {
        this.className = className
        this.testName = testName
    }

    String getClassName() {
        return className
    }

    String getTestName() {
        return testName
    }
}
