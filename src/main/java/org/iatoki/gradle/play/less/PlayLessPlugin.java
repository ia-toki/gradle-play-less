package org.iatoki.gradle.play.less;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.base.internal.SourceTransformTaskConfig;
import org.gradle.language.base.internal.registry.LanguageTransform;
import org.gradle.language.base.internal.registry.LanguageTransformContainer;
import org.gradle.language.base.sources.BaseLanguageSourceSet;
import org.gradle.model.Each;
import org.gradle.model.Finalize;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.Path;
import org.gradle.model.RuleSource;
import org.gradle.platform.base.BinarySpec;
import org.gradle.platform.base.ComponentType;
import org.gradle.platform.base.TransformationFileType;
import org.gradle.platform.base.TypeBuilder;
import org.gradle.platform.base.internal.DefaultComponentSpecIdentifier;
import org.gradle.play.PlayApplicationBinarySpec;
import org.gradle.play.PlayApplicationSpec;
import org.gradle.play.internal.PlayApplicationBinarySpecInternal;

public class PlayLessPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {}

    private static String getCssSourceSetName(LessSourceSet lessSourceSet) {
        return lessSourceSet.getName() + "Css";
    }

    static class Rules extends RuleSource {
        @ComponentType
        void registerLess(TypeBuilder<LessSourceSet> builder) {}

        @Finalize
        void createLessSourceSets(@Each PlayApplicationSpec playComponent) {
            playComponent.getSources().create("less", LessSourceSet.class, lessSourceSet -> {
                lessSourceSet.getSource().srcDir("app/assets");
                lessSourceSet.getSource().include("**/*.less");
            });
        }

        @Mutate
        void createGeneratedCssSourceSets(
                @Path("binaries") ModelMap<PlayApplicationBinarySpecInternal> binarySpecs,
                SourceDirectorySetFactory sourceDirectorySetFactory) {
            binarySpecs.all(binarySpec -> {
                List<CssSourceSet> cssSourceSets = new ArrayList<>();
                for (LessSourceSet lessSourceSet : binarySpec.getInputs().withType(LessSourceSet.class)) {
                    String cssSourceSetName = getCssSourceSetName(lessSourceSet);
                    CssSourceSet cssSourceSet = BaseLanguageSourceSet.create(
                            CssSourceSet.class,
                            DefaultCssSourceSet.class,
                            new DefaultComponentSpecIdentifier(binarySpec.getProjectPath(), cssSourceSetName),
                            sourceDirectorySetFactory);

                    cssSourceSets.add(cssSourceSet);
                }
                binarySpec.getInputs().addAll(cssSourceSets);
            });
        }

        @Mutate
        void registerLanguageTransform(LanguageTransformContainer languages) {
            languages.add(new Less());
        }
    }

    private interface CssSourceCode extends TransformationFileType {}

    private static class Less implements LanguageTransform<LessSourceSet, CssSourceCode> {
        @Override
        public String getLanguageName() {
            return "LESS";
        }

        @Override
        public Class<LessSourceSet> getSourceSetType() {
            return LessSourceSet.class;
        }

        @Override
        public Class<CssSourceCode> getOutputType() {
            return CssSourceCode.class;
        }

        @Override
        public Map<String, Class<?>> getBinaryTools() {
            return Collections.emptyMap();
        }

        @Override
        public SourceTransformTaskConfig getTransformTask() {
            return new SourceTransformTaskConfig() {
                @Override
                public String getTaskPrefix() {
                    return "compile";
                }

                @Override
                public Class<? extends DefaultTask> getTaskType() {
                    return LessCompile.class;
                }

                @Override
                public void configureTask(
                        Task task,
                        BinarySpec binarySpec,
                        LanguageSourceSet sourceSet,
                        ServiceRegistry serviceRegistry) {

                    PlayApplicationBinarySpecInternal playBinarySpec = (PlayApplicationBinarySpecInternal) binarySpec;

                    LessSourceSet lessSourceSet = (LessSourceSet) sourceSet;
                    CssSourceSet cssSourceSet = (CssSourceSet) playBinarySpec.getInputs()
                            .matching(inputSourceSet ->
                                    inputSourceSet.getName().equals(getCssSourceSetName(lessSourceSet)))
                            .iterator()
                            .next();

                    LessCompile lessCompile = (LessCompile) task;
                    lessCompile.setDescription("Compiles the " + lessSourceSet.getDisplayName() + ".");

                    File generatedSourceDir = playBinarySpec.getNamingScheme().getOutputDirectory(
                            task.getProject().getBuildDir(),
                            "src");
                    File cssSourceSetDir = new File(generatedSourceDir, cssSourceSet.getName());
                    cssSourceSet.getSource().srcDir(cssSourceSetDir);
                    playBinarySpec.getAssets().addAssetDir(cssSourceSetDir);

                    lessCompile.setOutputDirectory(cssSourceSetDir);
                    lessCompile.setSource(lessSourceSet.getSource());
                    cssSourceSet.builtBy(lessCompile);
                    playBinarySpec.getAssets().builtBy(lessCompile);
                }
            };
        }

        @Override
        public boolean applyToBinary(BinarySpec binarySpec) {
            return binarySpec instanceof PlayApplicationBinarySpec;
        }
    }
}
