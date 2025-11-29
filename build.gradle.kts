plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.17.0"
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	implementation("org.xerial:sqlite-jdbc:3.47.2.0")
	implementation("org.hibernate.orm:hibernate-community-dialects:7.0.0.Final")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
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

sourceSets {
	main {
		java {
			srcDir("$buildDir/generated/src/main/java")
		}
	}
}

tasks.named("compileJava") {
	dependsOn("openApiGenerate")
}
