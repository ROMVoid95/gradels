package space.tscg.gradle

import com.vanniktech.maven.publish.MavenPublishBaseExtension

import org.gradle.api.*
import org.gradle.api.provider.Property

import javax.inject.Inject

import space.tscg.gradle.git.JGit

class GradleDefaultsExtension {
    final Property<Boolean> addMavenPublish
    final Property<Boolean> printProjectInfo

    final Property<String> devName
    final Property<String> devEmail

    final Property<Boolean> onCentral

    final Property<String> githubOwner
    final Property<String> githubRepo
    final Property<String> mavenDescription
    final Property<String> keyId
    final Property<String> secretKeyRingFile
    final Property<String> password

    final Map<Object, Object> git

    final Property<Action<MavenPublishBaseExtension>> mavenPublishing;

    @Inject
    public GradleDefaultsExtension(Project target) {
        def jgit = JGit.open(target)
        def factory = target.objects
        this.git = GitInfo.git(jgit)
        this.addMavenPublish = factory.property(Boolean.class).convention(true)
        this.printProjectInfo = factory.property(Boolean.class).convention(true)
        this.onCentral = factory.property(Boolean.class).convention(false)
        this.devName = factory.property(String.class)
        this.devEmail = factory.property(String.class)
        this.githubOwner = factory.property(String.class).convention(jgit.getRepositoryOwner())
        this.githubRepo = factory.property(String.class).convention(jgit.getRepositoryName())
        this.mavenDescription = factory.property(String.class)
        this.keyId = factory.property(String.class)
        this.secretKeyRingFile = factory.property(String.class)
        this.password = factory.property(String.class)
        this.mavenPublishing = factory.property(Action.class)
    }

    Map<Object, Object> git() {
        this.git
    }

    void mavenPublishing(Action<MavenPublishBaseExtension> action) {
        this.mavenPublishing.set(action)
    }

    void onCentral(boolean onCentral) {
        this.onCentral.set(onCentral)
    }

    void devName(String devName) {
        this.devName.set(devName)
    }

    void devEmail(String devEmail) {
        this.devEmail.set(devEmail)
    }

    void githubOwner(String githubOwner) {
        this.githubOwner.set(githubOwner)
    }

    void githubRepo(String githubRepo) {
        this.githubRepo.set(githubRepo)
    }

    void keyId(String keyId) {
        this.keyId.set(keyId)
    }

    void keyRing(String keyRing) {
        this.secretKeyRingFile.set(keyRing)
    }

    void password(String password) {
        this.password.set(password)
    }

    void printProjectInfo(boolean printInfo) {
        this.addMavenPublish.set(printInfo)
    }

    void disableMavenPublish() {
        this.addMavenPublish.set(false)
    }

    boolean doMavenPublishing() {
        this.addMavenPublish.get().equals(true)
    }

    boolean isOnCentral() {
        this.onCentral.get().equals(true)
    }

    boolean isGitInfoSet() {
        !this.githubOwner.get().equals("unspecified") || !this.githubRepo.get().equals("unspecified")
    }

    boolean isDevInfoSet() {
        this.devEmail.present && this.devName.present
    }
}
