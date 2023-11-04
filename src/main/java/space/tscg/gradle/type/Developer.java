package space.tscg.gradle.type;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder(builderMethodName = "of", builderClassName = "Builder")
public class Developer
{
    @NotNull final String id;
    @Nullable String name;
    @Nullable String email;
    @Nullable @Singular List<Role> roles;
}
