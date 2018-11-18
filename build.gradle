apply plugin: 'java'
apply plugin: 'application'
apply plugin: 'com.github.ben-manes.versions'
apply plugin: 'kotlin'

version = '0.0.2'



def defaultEncoding = 'UTF-8'

mainClassName = "maildeleter.Main"


buildscript {
  ext.kotlin_version = '1.3.10'

  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.github.ben-manes:gradle-versions-plugin:0.20.0'
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
  }
}

compileJava {
  options.compilerArgs = ["-proc:none"]
}


test {
  testLogging.showStandardStreams = true
}

repositories {
  jcenter()
}

dependencies {
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1"
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
  implementation 'com.sun.mail:javax.mail:1.6.2'
  implementation 'org.apache.logging.log4j:log4j-api:2.11.1'
  implementation 'org.apache.logging.log4j:log4j-core:2.11.1'
}




compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
compileTestKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
