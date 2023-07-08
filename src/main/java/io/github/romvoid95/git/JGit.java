package io.github.romvoid95.git;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.jgit.api.Git;
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
    
    private final Git git;
    private final Website website;
    private Optional<URL> webUrl;

    private JGit(File file)
    {
        try
        {
            this.git = Git.open(file);
            final var remoteUrl = this.git.getRepository().getConfig().getString("remote", "origin", "url");
            this.website = Website.get(remoteUrl);
            this.webUrl = new UrlWrapper(remoteUrl).getUrl();
        } catch (IOException e)
        {
            throw new RuntimeException();
        }
    }

    private JGit(Project project)
    {
        this(project.getRootDir());
    }
    
    public Website getRepositoryWebsite()
    {
        return this.website;
    }
    
    public String getRepositoryOwner()
    {
        var owner = "unspecified";
        if(webUrl.isPresent())
        {
            var path = webUrl.get().getPath().replace(".git", "").substring(1);
            owner = path.split("/")[0];
        }
        return owner;
    }
    
    public String getRepositoryName()
    {
        var name = "unspecified";
        if(webUrl.isPresent())
        {
            var path = webUrl.get().getPath().replace(".git", "").substring(1);
            name = path.split("/")[1];
        }
        return name;
    }

    public static class UrlWrapper {
        private boolean isValid;
        private URL url;
        
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
    
    public enum Website {
        NONE(null),
        GITHUB((Predicate<String>) s -> s.contains("github.com"));

        Predicate<String> predicate;

        Website(Predicate<String> predicate)
        {
            this.predicate = predicate;
        }

        public static Website get(String hostname)
        {
            for(Website site : Website.values())
            {
                if(!site.equals(NONE) && site.predicate.test(hostname))
                    return site;
            }
            return NONE;
        }
    }
}
