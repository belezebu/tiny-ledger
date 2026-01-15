plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "8.1.0"

}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Sample implementation of a ledger"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


spotless {
    java {
        importOrder()

        removeUnusedImports()
        forbidWildcardImports()
        forbidModuleImports()

        cleanthat()

        palantirJavaFormat("2.39.0").formatJavadoc(true)

        formatAnnotations()
    }
}