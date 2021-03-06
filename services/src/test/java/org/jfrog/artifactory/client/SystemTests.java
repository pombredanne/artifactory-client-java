package org.jfrog.artifactory.client;

import org.jfrog.artifactory.client.model.Version;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author jryan
 * @since 7/9/13
 */
public class SystemTests extends ArtifactoryTestsBase {
    @Test
    public void testPingOnLiveServer() {
        assertTrue(artifactory.system().ping());
    }

    @Test
    public void testLoadVersion() {
        Version version = artifactory.system().version();
        assertNotNull(version.getVersion());
        assertTrue(version.getVersion().contains("."));
        String revision = version.getRevision();
        if (revision.equals("${buildNumber.prop}")) {
            assertTrue(version.getVersion().contains("-SNAPSHOT"));
        } else {
            int rev = Integer.parseInt(revision);
            assertTrue(rev > 0);
        }
        assertTrue(version.getAddons().size() > 5);
        assertNotNull(version.getLicense()); // Since to even perform REST API calls, we need a license
    }

    @Test
    public void testDownloadOfSystemConfiguration() {
        String xml = artifactory.system().configuration();
        assertTrue(xml.contains("backups"));
        assertTrue(xml.contains("localRepositories"));
        assertTrue(xml.contains("repoLayouts"));
    }

    @Test
    public void testUploadOfSystemConfiguration() {
        String oldXml = artifactory.system().configuration();
        String changedXml = oldXml.replace("<excludeBuilds>false</excludeBuilds>", "<excludeBuilds>true</excludeBuilds>");

        artifactory.system().configuration(changedXml);

        String updatedXml = artifactory.system().configuration();
        assertTrue(updatedXml.contains("backups"));
        assertTrue(updatedXml.contains("localRepositories"));
        assertTrue(updatedXml.contains("repoLayouts"));

        // Restore
        String restoredXml = updatedXml.replace("<excludeBuilds>true</excludeBuilds>", "<excludeBuilds>false</excludeBuilds>");
        artifactory.system().configuration(restoredXml);
    }
}
