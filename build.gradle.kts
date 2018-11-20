plugins {
  application
  kotlin("jvm") version "1.3.10"
  id("com.github.ben-manes.versions") version "0.20.0"
}

application {
  mainClassName = "maildeleter.Main"
}

val version = "0.0.3"

buildscript {
  dependencies {
    classpath("com.github.ben-manes:gradle-versions-plugin:0.20.0")
  }
}

repositories {
  jcenter()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.10")
  implementation("com.sun.mail:javax.mail:1.6.2")
  implementation("org.apache.logging.log4j:log4j-api:2.11.1")
  implementation("org.apache.logging.log4j:log4j-core:2.11.1")
}

