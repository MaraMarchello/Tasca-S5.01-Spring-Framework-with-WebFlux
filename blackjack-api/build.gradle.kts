plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.blackjack"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["testcontainersVersion"] = "1.19.6"
extra["lombokVersion"] = "1.18.30"
extra["junitVersion"] = "5.10.2"
extra["springdocVersion"] = "2.3.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	
	// OpenAPI/Swagger dependencies
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${property("springdocVersion")}")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
	implementation("io.swagger.core.v3:swagger-core-jakarta:2.2.20")
	
	compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
	annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
	
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("io.asyncer:r2dbc-mysql")
	
	testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.testcontainers:r2dbc")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
