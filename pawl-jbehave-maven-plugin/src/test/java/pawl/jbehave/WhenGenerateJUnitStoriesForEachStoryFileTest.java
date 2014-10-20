/*
 * Copyright 2014 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pawl.jbehave;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class WhenGenerateJUnitStoriesForEachStoryFileTest {

    private GenerateJUnitStoriesMojo plugin;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;
    File expectedFilesDirectory;

    @Before
    public void setupPlugin() throws IOException {
        outputDirectory = temporaryFolder.newFolder("sample-output");
        plugin = new GenerateJUnitStoriesMojo();
        plugin.setProject(new MavenProject());
        plugin.setOutputDirectory(outputDirectory);
        plugin.setStoriesDirectory(getResourcesAt("/stories"));
        expectedFilesDirectory = getResourcesAt("/sample-output");
    }

    @Test
    public void should_create_junit_stories_class_for_each_story_file() throws MojoExecutionException, IOException {
        plugin.execute();
        Collection<File> generatedFiles = FileUtils.listFiles(outputDirectory, new String[]{"java"}, true);
        assertThat(generatedFiles.size(), is(2));
        for (File actualFile : generatedFiles) {
            File expectedFile = new File(expectedFilesDirectory, actualFile.getName());
            assertEquals(FileUtils.readLines(actualFile), FileUtils.readLines(expectedFile));
        }
    }

    private File getResourcesAt(String path) {
        return new File(getClass().getResource(path).getFile());
    }

}