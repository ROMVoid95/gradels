package space.tscg.gradle.type;

import lombok.Getter;

@Getter
public final class Role
{
    public static final Role DEVELOPER      = new Role("developer");
    public static final Role LEAD_DEVELOPER = new Role("lead-developer");
    public static final Role MAINTAINER     = new Role("maintainer");
    public static final Role OWNER          = new Role("owner");
    public static final Role ADMINISTRATOR  = new Role("administrator");
    public static final Role CONTRIBUTOR    = new Role("contributor");
    public static final Role SUPPORT        = new Role("support");
    public static final Role LEAD_SUPPORT   = new Role("lead-support");

    private final String     name;

    private Role(String name) throws RuntimeException
    {
        if (name.contains(" "))
            throw new RuntimeException("Role name cannot contain spaces");
        this.name = name.toLowerCase();
    }
}
