package space.tscg.gradle;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.plugins.JavaPlugin;

public class ExtendedConfigurationPlugin implements Plugin<Project> {
    public static final String IMPLEMENTATION_SNAPSHOT_CONFIGURATION_NAME = "implSnapshot";

    public static final String IMPLEMENTATION_RELEASE_CONFIGURATION_NAME = "implRelease";

    public static final String COMPILE_ONLY_SNAPSHOT_CONFIGURATION_NAME = "compileSnapshot";

    public static final String COMPILE_ONLY_RELEASE_CONFIGURATION_NAME = "compileRelease";

    public static final String API_SNAPSHOT_CONFIGURATION_NAME = "apiSnapshot";

    public static final String API_RELEASE_CONFIGURATION_NAME = "apiRelease";

    @Override
    public void apply(Project target) {
        var implSn = target.getConfigurations().create(IMPLEMENTATION_SNAPSHOT_CONFIGURATION_NAME);
        var implRl = target.getConfigurations().create(IMPLEMENTATION_RELEASE_CONFIGURATION_NAME);
        
        var cmplSn = target.getConfigurations().create(COMPILE_ONLY_SNAPSHOT_CONFIGURATION_NAME);
        var cmplRl = target.getConfigurations().create(COMPILE_ONLY_RELEASE_CONFIGURATION_NAME);
        
        var apiSn = target.getConfigurations().create(API_SNAPSHOT_CONFIGURATION_NAME);
        var apiRl = target.getConfigurations().create(API_RELEASE_CONFIGURATION_NAME);
        
        Collection<Configuration> apis = Arrays.asList(apiSn, apiRl);
        
        target.getGradle().afterProject(p -> {
            
            var impl = p.getConfigurations().getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME);
            var compile = p.getConfigurations().getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);
            
            this.createImplementationConfigurations(p.getConfigurations());
            impl.extendsFrom(implSn, implRl);
            
            this.createCompileOnlyConfigurations(p.getConfigurations());
            compile.extendsFrom(cmplSn, cmplRl);
            
            if (p.getPluginManager().hasPlugin("java-library")) {
                var api = p.getConfigurations().getByName(JavaPlugin.API_CONFIGURATION_NAME);
                this.createApiConfigurations(p.getConfigurations());
                api.extendsFrom(apiSn, apiRl);
            } else {
                p.getConfigurations().removeAll(apis);
            }
        });
    }

    private void createImplementationConfigurations(ConfigurationContainer configurations) {
        configurations.getByName(IMPLEMENTATION_SNAPSHOT_CONFIGURATION_NAME, i -> {
            i.resolutionStrategy(this.snapshot);
            i.setCanBeResolved(true);
            i.setCanBeConsumed(true);
        });
        configurations.getByName(IMPLEMENTATION_RELEASE_CONFIGURATION_NAME, i -> {
            i.resolutionStrategy(this.release);
            i.setCanBeResolved(true);
            i.setCanBeConsumed(true);
        });
    }

    private void createCompileOnlyConfigurations(ConfigurationContainer configurations) {
        configurations.getByName(COMPILE_ONLY_SNAPSHOT_CONFIGURATION_NAME, i -> {
            i.resolutionStrategy(this.snapshot);
            i.setCanBeResolved(true);
            i.setCanBeConsumed(true);
        });
        configurations.getByName(COMPILE_ONLY_RELEASE_CONFIGURATION_NAME, i -> {
            i.resolutionStrategy(this.release);
            i.setCanBeResolved(true);
            i.setCanBeConsumed(true);
        });
    }

    private void createApiConfigurations(ConfigurationContainer configurations) {
        if (configurations.findByName(API_SNAPSHOT_CONFIGURATION_NAME) != null) {
            configurations.getByName(API_SNAPSHOT_CONFIGURATION_NAME, i -> {
                i.resolutionStrategy(this.snapshot);
                i.setCanBeResolved(true);
                i.setCanBeConsumed(true);
            });
        }
        if (configurations.findByName(API_RELEASE_CONFIGURATION_NAME) != null) {
            configurations.getByName(API_RELEASE_CONFIGURATION_NAME, i -> {
                i.resolutionStrategy(this.release);
                i.setCanBeResolved(true);
                i.setCanBeConsumed(true);
            });
        }
    }

    private Action<ResolutionStrategy> snapshot = rs -> {
        rs.cacheChangingModulesFor(10, TimeUnit.SECONDS);
        rs.cacheDynamicVersionsFor(10, TimeUnit.SECONDS);
    };

    private Action<ResolutionStrategy> release = rs -> {
        rs.cacheChangingModulesFor(6, TimeUnit.HOURS);
        rs.cacheDynamicVersionsFor(6, TimeUnit.HOURS);
    };
}
