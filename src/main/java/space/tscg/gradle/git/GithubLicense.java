
package space.tscg.gradle.git;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "key",
    "name",
    "spdx_id",
    "url",
    "node_id",
    "html_url",
    "description",
    "implementation",
    "permissions",
    "conditions",
    "limitations",
    "body",
    "featured"
})
public class GithubLicense {

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
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("description")
    public String description;
    @JsonProperty("implementation")
    public String implementation;
    @JsonProperty("permissions")
    public List<String> permissions;
    @JsonProperty("conditions")
    public List<String> conditions;
    @JsonProperty("limitations")
    public List<String> limitations;
    @JsonProperty("body")
    public String body;
    @JsonProperty("featured")
    public boolean featured;

}
