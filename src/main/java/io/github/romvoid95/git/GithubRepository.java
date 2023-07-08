package io.github.romvoid95.git;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "node_id", "name", "full_name", "private", "owner", "html_url", "description", "fork", "url", "forks_url", "keys_url", "collaborators_url", "teams_url", "hooks_url", "issue_events_url", "events_url", "assignees_url", "branches_url", "tags_url", "blobs_url", "git_tags_url", "git_refs_url", "trees_url", "statuses_url", "languages_url", "stargazers_url", "contributors_url", "subscribers_url", "subscription_url", "commits_url", "git_commits_url", "comments_url", "issue_comment_url", "contents_url", "compare_url", "merges_url", "archive_url", "downloads_url", "issues_url", "pulls_url", "milestones_url", "notifications_url", "labels_url", "releases_url", "deployments_url", "created_at", "updated_at", "pushed_at", "git_url", "ssh_url", "clone_url", "svn_url", "homepage", "size", "stargazers_count", "watchers_count", "language", "has_issues", "has_projects", "has_downloads", "has_wiki", "has_pages", "has_discussions", "forks_count", "mirror_url", "archived", "disabled", "open_issues_count", "license", "allow_forking", "is_template", "web_commit_signoff_required", "topics", "visibility", "forks", "open_issues", "watchers", "default_branch", "temp_clone_token", "network_count", "subscribers_count"
})
@ToString
public class GithubRepository
{

    @JsonProperty("id")
    public int          id;
    @JsonProperty("node_id")
    public String       nodeId;
    @JsonProperty("name")
    public String       name;
    @JsonProperty("full_name")
    public String       fullName;
    @JsonProperty("private")
    public boolean      _private;
    @JsonProperty("owner")
    public Owner        owner;
    @JsonProperty("html_url")
    public String       htmlUrl;
    @JsonProperty("description")
    public String       description;
    @JsonProperty("fork")
    public boolean      fork;
    @JsonProperty("url")
    public String       url;
    @JsonProperty("forks_url")
    public String       forksUrl;
    @JsonProperty("keys_url")
    public String       keysUrl;
    @JsonProperty("collaborators_url")
    public String       collaboratorsUrl;
    @JsonProperty("teams_url")
    public String       teamsUrl;
    @JsonProperty("hooks_url")
    public String       hooksUrl;
    @JsonProperty("issue_events_url")
    public String       issueEventsUrl;
    @JsonProperty("events_url")
    public String       eventsUrl;
    @JsonProperty("assignees_url")
    public String       assigneesUrl;
    @JsonProperty("branches_url")
    public String       branchesUrl;
    @JsonProperty("tags_url")
    public String       tagsUrl;
    @JsonProperty("blobs_url")
    public String       blobsUrl;
    @JsonProperty("git_tags_url")
    public String       gitTagsUrl;
    @JsonProperty("git_refs_url")
    public String       gitRefsUrl;
    @JsonProperty("trees_url")
    public String       treesUrl;
    @JsonProperty("statuses_url")
    public String       statusesUrl;
    @JsonProperty("languages_url")
    public String       languagesUrl;
    @JsonProperty("stargazers_url")
    public String       stargazersUrl;
    @JsonProperty("contributors_url")
    public String       contributorsUrl;
    @JsonProperty("subscribers_url")
    public String       subscribersUrl;
    @JsonProperty("subscription_url")
    public String       subscriptionUrl;
    @JsonProperty("commits_url")
    public String       commitsUrl;
    @JsonProperty("git_commits_url")
    public String       gitCommitsUrl;
    @JsonProperty("comments_url")
    public String       commentsUrl;
    @JsonProperty("issue_comment_url")
    public String       issueCommentUrl;
    @JsonProperty("contents_url")
    public String       contentsUrl;
    @JsonProperty("compare_url")
    public String       compareUrl;
    @JsonProperty("merges_url")
    public String       mergesUrl;
    @JsonProperty("archive_url")
    public String       archiveUrl;
    @JsonProperty("downloads_url")
    public String       downloadsUrl;
    @JsonProperty("issues_url")
    public String       issuesUrl;
    @JsonProperty("pulls_url")
    public String       pullsUrl;
    @JsonProperty("milestones_url")
    public String       milestonesUrl;
    @JsonProperty("notifications_url")
    public String       notificationsUrl;
    @JsonProperty("labels_url")
    public String       labelsUrl;
    @JsonProperty("releases_url")
    public String       releasesUrl;
    @JsonProperty("deployments_url")
    public String       deploymentsUrl;
    @JsonProperty("created_at")
    public String       createdAt;
    @JsonProperty("updated_at")
    public String       updatedAt;
    @JsonProperty("pushed_at")
    public String       pushedAt;
    @JsonProperty("git_url")
    public String       gitUrl;
    @JsonProperty("ssh_url")
    public String       sshUrl;
    @JsonProperty("clone_url")
    public String       cloneUrl;
    @JsonProperty("svn_url")
    public String       svnUrl;
    @JsonProperty("homepage")
    public Object       homepage;
    @JsonProperty("size")
    public int          size;
    @JsonProperty("stargazers_count")
    public int          stargazersCount;
    @JsonProperty("watchers_count")
    public int          watchersCount;
    @JsonProperty("language")
    public String       language;
    @JsonProperty("has_issues")
    public boolean      hasIssues;
    @JsonProperty("has_projects")
    public boolean      hasProjects;
    @JsonProperty("has_downloads")
    public boolean      hasDownloads;
    @JsonProperty("has_wiki")
    public boolean      hasWiki;
    @JsonProperty("has_pages")
    public boolean      hasPages;
    @JsonProperty("has_discussions")
    public boolean      hasDiscussions;
    @JsonProperty("forks_count")
    public int          forksCount;
    @JsonProperty("mirror_url")
    public Object       mirrorUrl;
    @JsonProperty("archived")
    public boolean      archived;
    @JsonProperty("disabled")
    public boolean      disabled;
    @JsonProperty("open_issues_count")
    public int          openIssuesCount;
    @JsonProperty("license")
    public License      license;
    @JsonProperty("allow_forking")
    public boolean      allowForking;
    @JsonProperty("is_template")
    public boolean      isTemplate;
    @JsonProperty("web_commit_signoff_required")
    public boolean      webCommitSignoffRequired;
    @JsonProperty("topics")
    public List<String> topics;
    @JsonProperty("visibility")
    public String       visibility;
    @JsonProperty("forks")
    public int          forks;
    @JsonProperty("open_issues")
    public int          openIssues;
    @JsonProperty("watchers")
    public int          watchers;
    @JsonProperty("default_branch")
    public String       defaultBranch;
    @JsonProperty("temp_clone_token")
    public Object       tempCloneToken;
    @JsonProperty("network_count")
    public int          networkCount;
    @JsonProperty("subscribers_count")
    public int          subscribersCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"login", "id", "node_id", "avatar_url", "gravatar_id", "url", "html_url", "followers_url", "following_url", "gists_url", "starred_url", "subscriptions_url", "organizations_url", "repos_url", "events_url", "received_events_url", "type", "site_admin"
    })
    public static class Owner
    {

        @JsonProperty("login")
        public String  login;
        @JsonProperty("id")
        public int     id;
        @JsonProperty("node_id")
        public String  nodeId;
        @JsonProperty("avatar_url")
        public String  avatarUrl;
        @JsonProperty("gravatar_id")
        public String  gravatarId;
        @JsonProperty("url")
        public String  url;
        @JsonProperty("html_url")
        public String  htmlUrl;
        @JsonProperty("followers_url")
        public String  followersUrl;
        @JsonProperty("following_url")
        public String  followingUrl;
        @JsonProperty("gists_url")
        public String  gistsUrl;
        @JsonProperty("starred_url")
        public String  starredUrl;
        @JsonProperty("subscriptions_url")
        public String  subscriptionsUrl;
        @JsonProperty("organizations_url")
        public String  organizationsUrl;
        @JsonProperty("repos_url")
        public String  reposUrl;
        @JsonProperty("events_url")
        public String  eventsUrl;
        @JsonProperty("received_events_url")
        public String  receivedEventsUrl;
        @JsonProperty("type")
        public String  type;
        @JsonProperty("site_admin")
        public boolean siteAdmin;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"key", "name", "spdx_id", "url", "node_id"})
    public static class License
    {

        @JsonProperty("key")
        public String key;
        @JsonProperty("name")
        public String name;
        @JsonProperty("spdx_id")
        public String spdxId;
        @JsonProperty("url")
        public String url;
        @JsonProperty("node_id")
        public String nodeId;
        
        public GithubLicense toGithubLicense()
        {
            return GithubCaller.call(this.url, GithubLicense.class);
        }
    }
}
