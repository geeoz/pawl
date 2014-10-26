/*
 * Copyright 2014 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pawl.jbehave;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Goal which generates JUnitStory classes for each *.story file.
 */
@Mojo(name = "generate-test-runners",
        defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateJUnitStoriesMojo
        extends AbstractMojo {
    /**
     * Directory with story files.
     */
    @Parameter(property = "project.stories.directory",
            defaultValue = "${project.basedir}/src/test/resources",
            required = true)
    private File storiesDirectory;

    /**
     * Location of the file.
     */
    @Parameter(property = "project.junit.stories.directory",
            defaultValue = "${project.build.directory}"
                    + "/generated-test-sources/jbehave",
            required = true)
    private File outputDirectory;

    /**
     * Maven project instance.
     */
    @Component
    private MavenProject project;


    /**
     * Execute maven plugin goal.
     *
     * @throws MojoExecutionException maven plugin exception
     */
    public void execute() throws MojoExecutionException {
        Collection<File> files = FileUtils.listFiles(getStoriesDirectory(),
                new String[]{"story"}, true);

        JCodeModel codeModel = new JCodeModel();
        for (File storyFile : files) {
            try {
                String relative = getStoriesDirectory().toURI()
                        .relativize(storyFile.getParentFile()
                                .toURI()).getPath()
                        .replace("/", ".")
                        .replace("\\", ".");
                String name = getClassNameFrom(storyFile.getName());
                JDefinedClass runnerClass = codeModel._class(
                        relative + name + "IT");
                runnerClass._extends(AbstractWebStory.class);
            } catch (JClassAlreadyExistsException e) {
                getLog().error(e);
            }
        }
        File outputDir = getOutputDirectory();
        if (!outputDir.exists()
                && outputDir.mkdirs()) {
            outputDir = getOutputDirectory();
        }
        try {
            codeModel.build(new FileCodeWriter(outputDir));
        } catch (IOException e) {
            getLog().error(e);
        }
        addOutputToSourceRoot();
    }

    /**
     * Convert file name to class name.
     *
     * @param name of file
     * @return class name
     */
    public String getClassNameFrom(final String name) {
        int extensionIndex = name.lastIndexOf('.');
        String nameWithOutExtension = name.substring(0, extensionIndex);
        String[] words = nameWithOutExtension.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return builder.toString();
    }

    /**
     * Setter for stories directory.
     *
     * @param storiesDir directory
     */
    public void setStoriesDirectory(final File storiesDir) {
        this.storiesDirectory = storiesDir;
    }

    /**
     * Setter for output directory.
     *
     * @param outputDir directory
     */
    public void setOutputDirectory(final File outputDir) {
        this.outputDirectory = outputDir;
    }

    /**
     * Setter for maven project.
     *
     * @param mavenProject maven
     */
    public void setProject(final MavenProject mavenProject) {
        this.project = mavenProject;
    }

    /**
     * Getter for stories directory.
     *
     * @return stories directory
     */
    public File getStoriesDirectory() {
        return storiesDirectory;
    }

    /**
     * Getter for output directory.
     *
     * @return runners directory
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Getter for maven project.
     *
     * @return maven project
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * Add generated runners to test compile source root.
     */
    public void addOutputToSourceRoot() {
        File source = getOutputDirectory();
        getProject().addTestCompileSourceRoot(source.getAbsolutePath());
        if (getLog().isInfoEnabled()) {
            getLog().info("Test Source directory: " + source + " added.");
        }
    }
}
