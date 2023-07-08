package io.github.romvoid95

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.provider.Property

import io.github.romvoid95.git.JGit

class MvnCentralExtension {
    final Property<String> devName
    final Property<String> devEmail
    final Property<String> githubOwner
    final Property<String> githubRepo

    final Property<String> keyId
    final Property<String> secretKeyRingFile
    final Property<String> password

    @Inject
    public MvnCentralExtension(Project target, JGit jgit) {
        def factory = target.objects
        this.devName = factory.property(String.class)
        this.devEmail = factory.property(String.class)
        this.githubOwner = factory.property(String.class).convention(jgit.getRepositoryOwner())
        this.githubRepo = factory.property(String.class).convention(jgit.getRepositoryName())
        this.keyId = factory.property(String.class)
        this.secretKeyRingFile = factory.property(String.class)
        this.password = factory.property(String.class)
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

    boolean isDevInfoSet() {
        this.devEmail.present && this.devName.present
    }
}
