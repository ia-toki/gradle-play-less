package org.iatoki.gradle.play.less;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.internal.impldep.org.junit.Assert.fail;
import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;

import java.io.IOException;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildResultException;
import org.junit.Before;
import org.junit.Test;

public class LessCompileIntegrationTest extends AbstractIntegrationTest {
    @Before
    public void setUp() throws IOException {
        setUpProject();

        writeToFile("app/assets/stylesheet/main.less", ".some-class { float: left; }");
    }

    @Test
    public void outputIncludedInOutputSourceSet() throws IOException {
        runTask("compilePlayBinaryPlayLess");

        assertThat(readFromFile("build/src/play/binary/lessCss/stylesheet/main.css")).contains(".some-class");
    }

    @Test
    public void assetsJarDependsOnLessCompile() {
        BuildResult result = runTask("createPlayBinaryAssetsJar");

        assertThat(result.task(":compilePlayBinaryPlayLess").getOutcome()).isEqualTo(SUCCESS);
    }

    @Test
    public void outputIncludedInAssetsJar() throws IOException {
        runTask("createPlayBinaryAssetsJar");

        assertThat(getAssetsJar().getEntry("public/stylesheet/main.css")).isNotNull();
    }

    @Test
    public void recompilesWhenInputChanged() throws IOException {
        runTask("compilePlayBinaryPlayLess");

        deleteFile("build/src/play/binary/lessCss/stylesheet/main.css");
        BuildResult result = runTask("compilePlayBinaryPlayLess");

        assertThat(result.task(":compilePlayBinaryPlayLess").getOutcome()).isEqualTo(SUCCESS);
    }

    @Test
    public void recompilesWhenOutputDeleted() throws IOException {
        runTask("compilePlayBinaryPlayLess");

        writeToFile("app/assets/stylesheet/main.less", ".some-class { float: right; }");
        BuildResult result = runTask("compilePlayBinaryPlayLess");

        assertThat(result.task(":compilePlayBinaryPlayLess").getOutcome()).isEqualTo(SUCCESS);
    }

    @Test
    public void doesNotRecompileWhenInputDidNotChange() throws IOException {
        runTask("compilePlayBinaryPlayLess");

        BuildResult result = runTask("compilePlayBinaryPlayLess");

        assertThat(result.task(":compilePlayBinaryPlayLess").getOutcome()).isEqualTo(UP_TO_DATE);
    }

    @Test
    public void failsForBadInput() throws IOException {
        writeToFile("app/assets/stylesheet/main.less", "bogus");

        try {
            runTask("compilePlayBinaryPlayLess");
            fail();
        } catch (UnexpectedBuildResultException e) {
            assertThat(e.getBuildResult().task(":compilePlayBinaryPlayLess").getOutcome()).isEqualTo(FAILED);
        }
    }
}
