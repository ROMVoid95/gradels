package space.tscg.gradle.git;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.Project;

public class JGit
{
    private static JGit jgit;
    
    public static JGit open(final Project project)
    {
        if (JGit.jgit == null)
        {
            JGit.jgit = new JGit(project);
        }

        return JGit.jgit;
    }
    
    private JGit(Project project)
    {
        this(project.getRootDir());
    }

    private final Website website;
    private Optional<URL> webUrl;
    private Repository    repository;
    private Git gitInstance;

    private JGit(File file)
    {
        var remoteUrl = "";
        try
        {
            var git = Git.open(file);
            this.gitInstance = git;
            this.repository = git.getRepository();
            remoteUrl = git.getRepository().getConfig().getString("remote", "origin", "url");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.website = Website.get(remoteUrl);
        this.webUrl = new UrlWrapper(remoteUrl).getUrl();
    }

    public Git gitInstance()
    {
        return this.gitInstance;
    }
    
    public Repository respository()
    {
        return this.repository;
    }

    String currentBranch()
    {
        try
        {
            return respository().getBranch();
        } catch (Exception e)
        {
            return "master";
        }
    }

    Optional<ObjectId> getHead()
    {
        try
        {
            return Optional.of(respository().resolve(Constants.HEAD));
        } catch (RevisionSyntaxException | IOException e)
        {
            return Optional.empty();
        }
    }

    public Website getRepositoryWebsite()
    {
        return this.website;
    }

    public String getRepositoryOwner()
    {
        var owner = "unspecified";
        if (webUrl.isPresent())
        {
            var path = webUrl.get().getPath().replace(".git", "").substring(1);
            owner = path.split("/")[0];
        }
        return owner;
    }

    public String getRepositoryName()
    {
        var name = "unspecified";
        if (webUrl.isPresent())
        {
            var path = webUrl.get().getPath().replace(".git", "").substring(1);
            name = path.split("/")[1];
        }
        return name;
    }

    public static class UrlWrapper
    {
        private boolean isValid;
        private URL     url;

        UrlWrapper(String urlString)
        {
            try
            {
                this.url = new URL(urlString);
                this.isValid = true;
            } catch (MalformedURLException e)
            {
                this.url = null;
                this.isValid = false;
            }
        }

        public boolean isValid()
        {
            return this.isValid;
        }

        public Optional<URL> getUrl()
        {
            return Optional.ofNullable(this.url);
        }
    }

    public enum Website
    {
        NONE(null),
        GITHUB((Predicate<String>) s -> s.contains("github.com"));

        Predicate<String> predicate;

        Website(Predicate<String> predicate)
        {
            this.predicate = predicate;
        }

        public static Website get(String hostname)
        {
            for (Website site : Website.values())
            {
                if (!site.equals(NONE) && site.predicate.test(hostname))
                    return site;
            }
            return NONE;
        }
    }
}
