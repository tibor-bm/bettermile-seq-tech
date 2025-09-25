import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

plugins {
    alias(libs.plugins.kotlin)
    application
}

application {
    mainClass.set("vm.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.kotter)
    implementation(libs.bundles.logging)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.hamkrest)
    testImplementation(libs.bundles.mockito)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(21)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")

            addTestListener(object : TestListener {
                override fun beforeSuite(suite: TestDescriptor) {}
                override fun beforeTest(testDescriptor: TestDescriptor) {}
                override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
                override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                    if (suite.parent == null) {
                        println(
                            "\n${result.resultType}: ${result.testCount} tests, " +
                                    "${result.successfulTestCount} passed, " +
                                    "${result.failedTestCount} failed, " +
                                    "${result.skippedTestCount} skipped " +
                                    "in ${(result.endTime - result.startTime).toFloat() / 1000}s"
                        )
                    }
                }
            })
        }
    }
}
