package io.github.romvoid95

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

import io.github.romvoid95.git.GithubCaller
import io.github.romvoid95.git.GithubRepository
import io.github.romvoid95.git.JGit

class MvnCentralPlugin implements Plugin<Project> {

    boolean configureRepositoriesCompleted = false
    boolean configurePomCompleted = false

    @Override
    void apply(Project target) {
        def jgit = JGit.open(target)

        def extension = target.extensions.create("mvn", MvnCentralExtension.class, jgit)

        target.gradle.afterProject {

            target.pluginManager.apply("maven-publish")
            def publishing = target.extensions.getByType(PublishingExtension.class)
            configureRepositories(target, publishing)
            configurePom(target, publishing, extension)

            if(this.configurePomCompleted && this.configureRepositoriesCompleted) {
                Provider<String> group = target.provider { target.group.toString() }
                extension.keyId.convention("%s.signing.keyId".formatted(group.get()))
                extension.password.convention("%s.signing.password".formatted(group.get()))
                extension.secretKeyRingFile.convention("%s.signing.secretKeyRingFile".formatted(group.get()))

                enableSigning(target, publishing, extension);
            } else {
                target.logger.lifecycle("**** Publishing Setup Incomplete - Skipping Signing Configuration ****")
            }

            // sources/javadoc
            target.pluginManager.withPlugin("java", {
                def java = target.extensions.getByType(JavaPluginExtension.class)
                java.withJavadocJar()
                java.withSourcesJar()
            });
        }
    }

    void configureRepositories(Project target, PublishingExtension publishing) {

        def missing = []
        if(!target.providers.gradleProperty("OSSRH_USERNAME").present)
            missing.add("OSSRH_USERNAME")
        if(!target.providers.gradleProperty("OSSRH_PASSWORD").present)
            missing.add("OSSRH_PASSWORD")

        if(missing.isEmpty()) {
            def releasesUrl = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/");
            def snapshotsUrl = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/");

            Provider<String> version = target.provider { target.version == null ? target.DEFAULT_VERSION : String.valueOf(target.version) };
            publishing.repositories {
                if(!version.get().equals(target.DEFAULT_VERSION)) {
                    if(version.get().endsWith('SNAPSHOT')) {
                        it.maven { MavenArtifactRepository repo ->
                            repo.name = "CentralSnapshots"
                            repo.url = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                            repo.credentials {
                                it.username = target.providers.gradleProperty("OSSRH_USERNAME").get()
                                it.password = target.providers.gradleProperty("OSSRH_PASSWORD").get()
                            }
                        }
                    } else {
                        it.maven { MavenArtifactRepository repo ->
                            repo.name = "CentralReleases"
                            repo.url = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                            repo.credentials {
                                it.username = target.providers.gradleProperty("OSSRH_USERNAME").get()
                                it.password = target.providers.gradleProperty("OSSRH_PASSWORD").get()
                            }
                        }
                    }
                }
            }

            publishing.publications {
                it.create("mavenJava", MavenPublication.class) {
                    it.setGroupId(target.group.toString())
                    it.setArtifactId(target.name.toLowerCase())
                    it.setVersion(version.get())

                    it.from target.components.findByName("java")
                }
            }

            this.configureRepositoriesCompleted = true
        } else {
            target.logger.lifecycle("")
            target.logger.lifecycle("**** Cannot Configure Repositories | Missing Properties: " + missing.toListString())
            target.logger.lifecycle("")
        }
    }

    void configurePom(Project target, PublishingExtension publishing, MvnCentralExtension extension) {

        if(this.configureRepositoriesCompleted) {
            def missing = []
            if(!extension.getGithubOwner().present)
                missing.add("githubOwner")
            if(!extension.getGithubRepo().present)
                missing.add("githubRepo")

            if(missing.isEmpty()) {
                GithubRepository repository = GithubCaller.info(extension.getGithubOwner().get(), extension.getGithubRepo().get());

                if((target.description == null || target.description.isBlank()) && repository.description == null) {
                    target.logger.lifecycle("")
                    target.logger.lifecycle("**** Cannot Configure Pom | No description for project can be set")
                    target.logger.lifecycle("****                      | One of the following must be set:")
                    target.logger.lifecycle("****                      | Github Repository Description")
                    target.logger.lifecycle("****                      | project.description")
                    target.logger.lifecycle("")

                    this.configurePomCompleted = false
                    return
                }

                def description = target.provider { target.description }.isPresent() ? target.description : repository.description

                publishing.publications.withType(MavenPublication.class, { MavenPublication publication ->
                    target.pluginManager.withPlugin("java-base`", { publication.versionMapping { it.usage("java-api", { it.fromResolutionOf("runtimeClasspath")})}})

                    publication.pom {
                        it.name.set(repository.name.toLowerCase())
                        it.description.set(description)
                        it.url.set(repository.htmlUrl)

                        if(extension.isDevInfoSet()) {
                            it.developers {
                                it.developer {
                                    it.name.set(extension.getDevName())
                                    it.email.set(extension.getDevEmail())
                                }
                            }
                        }

                        it.licenses {
                            it.license {
                                it.name.set(repository.license.name)
                                it.url.set(repository.license.toGithubLicense().htmlUrl)
                            }
                        }

                        it.scm {
                            it.url.set(repository.htmlUrl)
                            it.connection.set("scm:git:%s".formatted(repository.gitUrl))
                            it.developerConnection.set("scm:git:ssh:%s".formatted(repository.gitUrl))
                        }
                    }
                })
                this.configurePomCompleted = true
            } else {
                target.logger.lifecycle("")
                target.logger.lifecycle("**** Cannot Configure Pom | Missing Properties: " + missing.toListString())
                target.logger.lifecycle("")
            }
        } else {
            target.logger.lifecycle("")
            target.logger.lifecycle("**** Repositories Setup Incomplete - Skipping Pom Configuration ****")
            target.logger.lifecycle("")
        }
    }

    void enableSigning(Project target, PublishingExtension publishing, MvnCentralExtension extension) {
        def missing = []
        if(target.findProperty(extension.getKeyId().get()) == null)
            missing.add("keyId")
        if(target.findProperty(extension.getPassword().get()) == null)
            missing.add("password")
        if(target.findProperty(extension.getSecretKeyRingFile().get()) == null) {
            missing.add("secretKeyRingFile")
        } else {
            File keyRingFile = target.file(target.findProperty(extension.getSecretKeyRingFile().get()))
            if(!keyRingFile.exists())
                missing.add("secretKeyRingFile specified does not exist")
        }

        if(missing.isEmpty()) {
            target.pluginManager.apply("signing");
            def signing = target.extensions.getByType(SigningExtension.class);

            target.setProperty("signing.keyId", target.findProperty(extension.getKeyId().get()))
            target.setProperty("signing.password", target.findProperty(extension.getPassword().get()))
            target.setProperty("signing.secretKeyRingFile", target.findProperty(extension.getSecretKeyRingFile().get()))
            signing.setRequired(true)
            signing.sign(publishing.publications)
        } else {
            target.logger.lifecycle("")
            target.logger.lifecycle("**** Cannot Configure Signing | Missing Properties: " + missing.toListString())
            target.logger.lifecycle("")
        }
    }

    void configureCiSigning(Project target, SigningExtension signing, PublishingExtension publishing) {
        def signingKey = target.providers.gradleProperty("signingKey").getOrNull()
        def signingPassphrase = target.providers.gradleProperty("signingPassphrase").getOrNull()


        signing.useInMemoryPgpKeys(signingKey, signingPassphrase)
        signing.sign(publishing.publications)
    }
}
