package space.tscg.gradle

import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository

import space.tscg.gradle.git.JGit

class GitInfo
{
    static Map<Object, Object> git(JGit jgit) {
        def git = jgit.gitInstance()
        def tag
        try {
            tag = git.describe().setLong(true).setTags(true).setMatch(new String[0]).call()
        } catch (Exception e) {
            tag = '0.0.0'
        }
        def head = git.repository.exactRef('HEAD')
        def longBranch = head.symbolic ? head?.target?.name : null // matches Repository.getFullBranch() but returning null when on a detached HEAD

        def ret = [:]
        ret.tag = tag
        ret.branch = longBranch != null ? Repository.shortenRefName(longBranch) : null
        ret.commit = ObjectId.toString(head.objectId)
        ret.abbreviatedId = head.objectId.abbreviate(8).name()

        // Remove any lingering null values
        ret.removeAll {it.value == null }

        return ret
    }
}
