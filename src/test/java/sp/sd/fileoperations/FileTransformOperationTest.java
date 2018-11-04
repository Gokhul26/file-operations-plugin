package sp.sd.fileoperations;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

public class FileTransformOperationTest {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    @WithoutJenkins
    public void testDefaults() {
        FileTransformOperation fto = new FileTransformOperation("NewFileName.config", "**/*.xml");
        assertEquals("NewFileName.config", fto.getIncludes());
        assertEquals("**/*.xml", fto.getExcludes());
    }
}
