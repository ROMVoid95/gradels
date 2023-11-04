package space.tscg.gradle;

import java.util.List;
import java.util.stream.Collectors;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

import space.tscg.gradle.type.Developer;

public interface StellarDefaultsExtension
{
    @NotNull Property<Boolean> publishing();
    
    default void publishing(final boolean doPublishing)
    {
        this.publishing().set(doPublishing);
    }
    
    @NotNull Property<Boolean> printProjectInfo();
    
    default void printProjectInfo(final boolean printInfo)
    {
        this.printProjectInfo().set(printInfo);
    }
    
    @NotNull ListProperty<Developer> developers();
    
    default void developers(final List<Developer.Builder> builders)
    {
        this.developers().set(builders.stream().map(Developer.Builder::build).collect(Collectors.toList()));
    }
    
    default void developers(final Developer... list)
    {
        this.developers().addAll(list);
    }
    
    @NotNull Property<String> keyId();
    
    default void keyId(final String keyId)
    {
        this.keyId().set(keyId);
    }
    
    @NotNull Property<String> keyRingFile();
    
    default void keyRingFile(final String keyRingFile)
    {
        this.keyRingFile().set(keyRingFile);
    }
    
    @NotNull Property<String> password();
    
    default void password(final String password)
    {
        this.password().set(password);
    }
}
