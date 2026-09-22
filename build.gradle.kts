import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import java.io.File

plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	id("org.springframework.boot") version "4.1.1"
}

group = "com.test"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc:4.1.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.3.21")
	implementation("tools.jackson.module:jackson-module-kotlin:3.1.5")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:4.1.1")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.3.21")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("prepareOfflineMaven") {
	group = "distribution"
	description = "Exports the resolved Gradle dependencies to offline-maven in Maven layout."

	doLast {
		val cacheRoot = File(gradle.gradleUserHomeDir, "caches/modules-2/files-2.1")
		val destinationRoot = layout.projectDirectory.dir("offline-maven").asFile
		val components = linkedSetOf<ModuleComponentIdentifier>()
		val resolvableConfigurations = configurations.filter { it.isCanBeResolved } +
			buildscript.configurations.filter { it.isCanBeResolved }

		resolvableConfigurations.forEach { configuration ->
			configuration.incoming.resolutionResult.allComponents
				.mapNotNull { it.id as? ModuleComponentIdentifier }
				.forEach(components::add)
		}

		val missingComponents = mutableListOf<String>()
		components.forEach { component ->
			val cachedComponentDirectory = File(
				cacheRoot,
				"${component.group}/${component.module}/${component.version}",
			)
			val cachedFiles = cachedComponentDirectory.walkTopDown()
				.filter { it.isFile }
				.toList()

			if (cachedFiles.isEmpty()) {
				missingComponents += component.displayName
			} else {
				val destinationDirectory = File(
					destinationRoot,
					"${component.group.replace('.', '/')}/${component.module}/${component.version}",
				)
				destinationDirectory.mkdirs()
				cachedFiles.forEach { cachedFile ->
					cachedFile.copyTo(File(destinationDirectory, cachedFile.name), overwrite = true)
				}
			}
		}

		check(missingComponents.isEmpty()) {
			"Missing Gradle cache files for: ${missingComponents.joinToString()}"
		}
		println("Exported ${components.size} resolved components to $destinationRoot")
	}
}
