import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

group = "com.github.erfangc"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	// CSV processor and data class mapper
	implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.10.4")
	implementation("com.vhl.blackmo:kotlin-grass-jvm:0.3.0")

	// MongoDB & DSL
	implementation("org.litote.kmongo:kmongo:4.0.2")

	// YearQuarter addon
	implementation("org.threeten:threeten-extra:1.5.0")

	// HttpClient from Apache
	implementation("org.apache.httpcomponents:httpclient:4.5.12")

	//	Spring Boot Dependencies
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
