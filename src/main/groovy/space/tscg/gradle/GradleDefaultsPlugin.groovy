package space.tscg.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost

import space.tscg.gradle.git.GithubCaller
import space.tscg.gradle.git.GithubRepository

class GradleDefaultsPlugin implements PluginProject {

    Project target
    GradleDefaultsExtension extension

    @Override
    public void project(Project target) {
        this.target = target
        this.extension = target.extensions.create("tscg", GradleDefaultsExtension.class)

        target.pluginManager.apply(ExtendedConfigurationPlugin.class)

        target.tasks.withType(Javadoc) {
            failOnError false
            options { StandardJavadocDocletOptions opts ->
                opts.addStringOption('encoding', 'UTF-8')
                opts.addStringOption('charSet', 'UTF-8')
                opts.addBooleanOption("html5", true)
            }
        }

        target.repositories {
            it.mavenCentral()
            it.maven {
                it.name = 'sonatype-snapshots'
                it.url = 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
            }
            it.maven {
                it.name = 'EzGradle'
                it.url = 'https://ezgradle.site/global'
            }
        }

        target.gradle.afterProject {

            target.pluginManager.withPlugin('java', {
                def jvaExt = target.extensions.getByType(JavaPluginExtension.class)
                jvaExt.toolchain.languageVersion = JavaLanguageVersion.of 17 //this.extension.getJdkVersion().get()
            })

            if(extension.doMavenPublishing()) {
                Provider<String> version = target.provider { target.version == null ? target.DEFAULT_VERSION : String.valueOf(target.version) }
                Provider<String> group = target.provider { target.group.toString() }

                extension.keyId.convention("%s.signing.keyId".formatted(group.get()))
                extension.password.convention("%s.signing.password".formatted(group.get()))
                extension.secretKeyRingFile.convention("%s.signing.secretKeyRingFile".formatted(group.get()))

                target.pluginManager.withPlugin("com.vanniktech.maven.publish", {
                    def maven = target.extensions.getByType(MavenPublishBaseExtension.class)
                    maven.coordinates(
                            target.group.toString(),
                            target.name.toLowerCase(),
                            version.get())
                    maven.pom(this.pom())
                    maven.signAllPublications()
                    maven.publishToMavenCentral(SonatypeHost.S01, true)
                })

                target.pluginManager.apply(MavenPublishPlugin.class)
                def mvn = target.extensions.getByType(PublishingExtension.class)
                mvn.repositories({
                    if(target.hasProperty('EZG_USER') && target.hasProperty('EZG_PASSWORD')) {
                        it.maven {
                            name "EzGradle"
                            url "https://ezgradle.site/global"
                            authentication {
                                basic(BasicAuthentication)
                            }
                            credentials {
                                it.username = target.property("EZG_USER")
                                it.password = target.property("EZG_PASSWORD")
                            }
                        }
                    } else {
                        it.maven {
                            name "LocalFile"
                            url "file://" + target.rootProject.file('repo').getAbsolutePath()
                        }
                    }
                })

                if(!System.getenv().CI) {
                    setSigningProperties()
                }

                target.tasks.withType(GenerateModuleMetadata) {
                    dependsOn 'simpleJavadocJar'
                }
            }
        }
    }

    void log(String s) {
        target.logger.lifecycle(s)
    }

    Action<MavenPom> pom() {
        def passed = runChecks()
        if(!passed) {
            return {} }

        def repository = GithubCaller.info(extension.getGithubOwner().get(), extension.getGithubRepo().get());
        def description = extension.getMavenDescription().isPresent()
                ? extension.getMavenDescription().get()
                : target.provider { target.description }.isPresent()
                ? target.description
                : repository.description

        return {
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
                it.tag.set("HEAD")
            }
        }
    }

    boolean runChecks() {
        def allGood = true
        if(extension.gitInfoSet) {
            def missing = []
            if(!extension.getGithubOwner().present)
                missing.add("githubOwner")
            if(!extension.getGithubRepo().present)
                missing.add("githubRepo")
            if(missing.isEmpty()) {
                GithubRepository repository = GithubCaller.info(extension.getGithubOwner().get(), extension.getGithubRepo().get());
                if((target.description == null || target.description.isBlank()) && repository.description == null) {
                    log("")
                    log("**** Cannot Configure Pom | No description for project can be set")
                    log("****                      | One of the following must be set:")
                    log("****                      | Github Repository Description")
                    log("****                      | project.description")
                    log("")

                    allGood = false
                }
            } else {
                allGood = false
            }
        } else {
            allGood = false
        }

        return allGood
    }

    void setSigningProperties() {
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
            target.setProperty("signing.keyId", target.findProperty(extension.getKeyId().get()))
            target.setProperty("signing.password", target.findProperty(extension.getPassword().get()))
            target.setProperty("signing.secretKeyRingFile", target.findProperty(extension.getSecretKeyRingFile().get()))
        } else {
            log("")
            log("**** Cannot Configure Signing | Missing Properties: " + missing.toListString())
            log("")
        }
    }
}
