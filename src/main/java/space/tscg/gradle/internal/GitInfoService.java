package space.tscg.gradle.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GitInfoService implements BuildService<GitInfoService.Parameters>, AutoCloseable {
  public static final String SERVICE_NAME = "gitInfoService";
  private static final Logger LOGGER = Logging.getLogger(GitInfoService.class);

  private volatile boolean open = true;
  private final Map<File, GitWrapper> projectRepos = new ConcurrentHashMap<>();

  public interface Parameters extends BuildServiceParameters {
    DirectoryProperty getBaseDirectory();
  }

  public @Nullable Git git(final File projectDir, final @NotNull String displayName) {
    if(!this.open) {
      throw new IllegalStateException("Tried to access git repository after close");
    }
    final @Nullable GitWrapper wrapper = this.projectRepos.get(projectDir);
    if(wrapper != null) return wrapper.git;

    final var rawProjectDir = projectDir;
    final File rootProjectDir;
    final File realProjectDir;
    try {
      rootProjectDir = this.getParameters().getBaseDirectory().get().getAsFile().getCanonicalFile();
      realProjectDir = rawProjectDir.getCanonicalFile();
      if(!realProjectDir.getPath().startsWith(rootProjectDir.getPath())) {
        throw new IllegalArgumentException("Project directory " + rawProjectDir + " was not within the root project!");
      }

      var targetDir = realProjectDir;
      do {
        if(isGitDir(targetDir)) {
          LOGGER.debug("Git-Info: Examining directory {} for {}", targetDir, displayName);
          final var potentialExisting = this.projectRepos.get(targetDir);
          if(potentialExisting != null) {
            LOGGER.info("Git-Info: Found existing git repository for {} starting in directory {} via {}", displayName, rawProjectDir, targetDir);
            this.projectRepos.put(rawProjectDir, potentialExisting);
            return potentialExisting.git;
          }

          try {
            final @Nullable File realGit = resolveGit(targetDir);
            if (realGit == null) continue;
            final var repo = new RepositoryBuilder().setWorkTree(targetDir).setGitDir(realGit).setMustExist(true).build();

            var repoWrapper = new GitWrapper(repo);
            final var existing = this.projectRepos.putIfAbsent(targetDir, repoWrapper);
            if(existing != null) {
              repo.close();
              repoWrapper = existing;
            } else {
              LOGGER.info("Git-Info: Located and initialized repository for project {} in {}, with git directory at {}", displayName, targetDir, repo.getDirectory());
            }

            this.projectRepos.put(rawProjectDir, repoWrapper);
            return repoWrapper.git;
          } catch(final RepositoryNotFoundException ex) {
            LOGGER.debug("Git-Info: Unable to open repository found in {} for {}", targetDir, displayName, ex);
          }
        } else {
          LOGGER.debug("Git-Info: Skipping directory {} while locating repository for {}", targetDir, displayName);
        }
      } while((!rootProjectDir.equals(targetDir)) && ((targetDir = targetDir.getParentFile()) != null));
      this.projectRepos.put(rawProjectDir, GitWrapper.NOT_FOUND);
    } catch(final IOException ex) {
      LOGGER.warn("Git-Info: Failed to open git repository for {}:", displayName, ex);
    }
    LOGGER.info("Git-Info: No git repository found for {}", displayName);
    return null;
  }

  private static final String GIT_DIR = ".git";
  private static final String GITDIR_PREFIX = "gitdir:";

  private static boolean isGitDir(final File file) {
    return new File(file, GIT_DIR).exists();
  }

  private static File resolveGit(File projectDir) throws IOException {
    if (!projectDir.getName().equals(GIT_DIR)) {
      return resolveGit(new File(projectDir, ".git"));
    } else {
      projectDir = projectDir.getCanonicalFile();
      if (projectDir.isDirectory()) {
        return projectDir;
      } else if (projectDir.isFile()) {
        try (final var reader = new BufferedReader(new InputStreamReader(new FileInputStream(projectDir), StandardCharsets.UTF_8))) {
          String line;
          while ((line = reader.readLine()) != null) {
            if (line.startsWith(GITDIR_PREFIX)) {
              return new File(projectDir.getParentFile(), line.substring(GITDIR_PREFIX.length()).trim());
            }
          }
        }
      }
      LOGGER.warn("Git-Info: Unable to determine actual git directory from {}", projectDir);
      return null;
    }
  }

  @Override
  public void close() {
    this.open = false;
    final Set<GitWrapper> repos = new HashSet<>(this.projectRepos.values());
    this.projectRepos.clear();
    for(final GitWrapper wrapper : repos) {
      if(wrapper.repository != null) {
        wrapper.repository.close();
      }
    }
  }

  private static class GitWrapper {
    static final GitWrapper NOT_FOUND = new GitWrapper(null);

    final @Nullable Git git;
    final @Nullable Repository repository;

    GitWrapper(final @Nullable Repository repo) {
      this.repository = repo;
      this.git = repo == null ? null : Git.wrap(repo);
    }
  }
}
