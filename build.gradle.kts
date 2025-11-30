plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.17.0"
	id("com.diffplug.spotless") version "8.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Paste API for Spring Boot 4"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

sourceSets {
	main {
		java {
			srcDir("$buildDir/generated/src/main/java")
		}
	}
	create("integrationTest") {
		java {
			srcDir("src/integration-test/java")
		}
		resources {
			srcDir("src/integration-test/resources")
		}
		compileClasspath += sourceSets.main.get().output
		runtimeClasspath += sourceSets.main.get().output
	}
}

configurations {
	getByName("integrationTestImplementation") {
		extendsFrom(configurations.testImplementation.get())
	}
	getByName("integrationTestRuntimeOnly") {
		extendsFrom(configurations.testRuntimeOnly.get())
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	implementation("org.openapitools:jackson-databind-nullable:0.2.8")
	implementation("org.xerial:sqlite-jdbc:3.47.2.0")
	implementation("org.hibernate.orm:hibernate-community-dialects:7.0.0.Final")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("net.jqwik:jqwik:1.9.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed", "standardOut", "standardError")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
		showStandardStreams = true
	}
}

tasks.register<Test>("integrationTest") {
	description = "Runs integration tests."
	group = "verification"
	testClassesDirs = sourceSets["integrationTest"].output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath
	shouldRunAfter("test")
	testLogging {
		events("passed", "skipped", "failed", "standardOut", "standardError")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
		showStandardStreams = true
	}
}

tasks.named("check") {
	dependsOn("integrationTest")
}

openApiGenerate {
	generatorName = "spring"
	inputSpec = "$rootDir/spec.yml"
	outputDir = "$buildDir/generated"
	apiPackage = "com.example.paste.api"
	modelPackage = "com.example.paste.model"
	configOptions = mapOf(
		"delegatePattern" to "true",
		"interfaceOnly" to "true",
		"useSpringBoot3" to "true",
		"useTags" to "true"
	)
}

tasks.named("compileJava") {
	dependsOn("openApiGenerate")
}

spotless {
	java {
		importOrder()
		removeUnusedImports()
		forbidWildcardImports()
		forbidModuleImports()
		googleJavaFormat()
		targetExclude("build/**")
	}
}
