package org.iatoki.gradle.play.less;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.play.PlayApplicationSpec;
import org.gradle.play.plugins.PlayApplicationPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayLessPluginTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File projectDir;
    private ProjectInternal project;
    private PlayApplicationSpec play;

    @Before
    public void setUp() throws IOException {
        projectDir = temporaryFolder.newFolder();

        project = (ProjectInternal) ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build();

        project.getPluginManager().apply(PlayApplicationPlugin.class);
        project.getPluginManager().apply(PlayLessPlugin.class);
        project.bindAllModelRules();

        play = (PlayApplicationSpec) project.getModelRegistry().find("components", ComponentSpecContainer.class).get("play");
    }

    @Test
    public void addsLessSourceSet() {
        LessSourceSet less = (LessSourceSet) play.getSources().get("less");
        assertThat(less).isNotNull();

        assertThat(less.getSource().getSrcDirs()).containsExactly(project.file("app/assets"));
        assertThat(less.getSource().getIncludes()).containsExactly("**/*.less");
    }
}
