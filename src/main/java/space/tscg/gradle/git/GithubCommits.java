package space.tscg.gradle.git;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"commits"})
public class GithubCommits
{
    @JsonProperty("commits")
    public List<Commit> commits;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"sha", "node_id", "commitData", "url", "html_url", "comments_url", "author", "committer", "parents"})
    public class Commit
    {
        @JsonProperty("sha")
        public String       sha;
        @JsonProperty("node_id")
        public String       nodeId;
        @JsonProperty("commit")
        public CommitData   commit;
        @JsonProperty("url")
        public String       url;
        @JsonProperty("html_url")
        public String       htmlUrl;
        @JsonProperty("comments_url")
        public String       commentsUrl;
        @JsonProperty("author")
        public UserInfo     author;
        @JsonProperty("committer")
        public UserInfo     committer;
        @JsonProperty("parents")
        public List<Parent> parents;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonPropertyOrder({"author", "committer", "message", "tree", "url", "comment_count", "verification"})
        public class CommitData
        {
            @JsonProperty("author")
            public Author       author;
            @JsonProperty("committer")
            public Committer    committer;
            @JsonProperty("message")
            public String       message;
            @JsonProperty("tree")
            public Tree         tree;
            @JsonProperty("url")
            public String       url;
            @JsonProperty("comment_count")
            public int          commentCount;
            @JsonProperty("verification")
            public Verification verification;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonPropertyOrder({"name", "email", "date"})
            public class Author
            {
                @JsonProperty("name")
                public String name;
                @JsonProperty("email")
                public String email;
                @JsonProperty("date")
                public String date;
            }

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonPropertyOrder({"name", "email", "date"})
            public class Committer
            {
                @JsonProperty("name")
                public String name;
                @JsonProperty("email")
                public String email;
                @JsonProperty("date")
                public String date;
            }

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonPropertyOrder({"sha", "url"})
            public class Tree
            {
                @JsonProperty("sha")
                public String sha;
                @JsonProperty("url")
                public String url;
            }

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonPropertyOrder({"verified", "reason", "signature", "payload"})
            public class Verification
            {
                @JsonProperty("verified")
                public boolean verified;
                @JsonProperty("reason")
                public String  reason;
                @JsonProperty("signature")
                public Object  signature;
                @JsonProperty("payload")
                public Object  payload;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonPropertyOrder({"sha", "url", "html_url"})
        public class Parent
        {
            @JsonProperty("sha")
            public String sha;
            @JsonProperty("url")
            public String url;
            @JsonProperty("html_url")
            public String htmlUrl;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonPropertyOrder({"login", "id", "node_id", "avatar_url", "gravatar_id", "url", "html_url", "followers_url", "following_url", "gists_url", "starred_url", "subscriptions_url", "organizations_url", "repos_url", "events_url", "received_events_url", "type", "site_admin"})
        public class UserInfo
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

    }
}
