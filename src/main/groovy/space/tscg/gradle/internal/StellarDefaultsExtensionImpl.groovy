package space.tscg.gradle.internal

import javax.inject.Inject

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import space.tscg.gradle.StellarDefaultsExtension
import space.tscg.gradle.type.Developer

class StellarDefaultsExtensionImpl implements StellarDefaultsExtension
{
    final Property<Boolean> addMavenPublish
    final Property<Boolean> printProjectInfo

    final Property<String> keyId
    final Property<String> secretKeyRingFile
    final Property<String> password

    final ListProperty<Developer> developers

    private transient final ProviderFactory providers;
    private transient final ProjectLayout layout;
    private transient @Nullable Action<SigningExtension> signingAction;
    private transient SigningExtension signingExtension;

    @Inject
    public StellarDefaultsExtensionImpl(final Project target, final ProviderFactory providers, final ProjectLayout layout)
    {
        def objects = target.objects
        this.providers = providers;
        this.layout = layout;
        this.addMavenPublish = objects.property(Boolean).convention(true)
        this.printProjectInfo = objects.property(Boolean).convention(true)

        this.keyId = objects.property(String).convention("%s.signing.keyId")
        this.password = objects.property(String).convention("%s.signing.password")
        this.secretKeyRingFile = objects.property(String).convention("%s.signing.secretKeyRingFile")
    }

    @Override
    public @NotNull Property<Boolean> publishing()
    {
        return null;
    }

    @Override
    public @NotNull Property<Boolean> printProjectInfo()
    {
        return null;
    }

    @Override
    public @NotNull ListProperty<Developer> developers()
    {
        return null;
    }

    @Override
    public @NotNull Property<String> keyId()
    {
        return null;
    }

    @Override
    public @NotNull Property<String> keyRingFile()
    {
        return null;
    }

    @Override
    public @NotNull Property<String> password()
    {
        return null;
    }
}
