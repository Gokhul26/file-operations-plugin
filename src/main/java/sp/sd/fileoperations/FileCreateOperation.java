package sp.sd.fileoperations;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.Serializable;

public class FileCreateOperation extends FileOperation implements Serializable {
    private final String fileName;
    private final String fileContent;

    @DataBoundConstructor
    public FileCreateOperation(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public boolean runOperation(Run<?, ?> run, FilePath buildWorkspace, Launcher launcher, TaskListener listener) {
        boolean result = false;
        try {
            listener.getLogger().println("File Create Operation:");
            EnvVars envVars = run.getEnvironment(listener);
            try {
                FilePath ws = new FilePath(buildWorkspace, ".");
                result = ws.act(new TargetFileCallable(listener, envVars.expand(fileName), envVars.expand(fileContent), envVars));
            } catch (Exception e) {
                listener.fatalError(e.getMessage());
                return false;
            }
        } catch (Exception e) {
            listener.fatalError(e.getMessage());
        }
        return result;
    }

    private static final class TargetFileCallable implements FileCallable<Boolean> {
        private static final long serialVersionUID = 1;
        private final TaskListener listener;
        private final EnvVars environment;
        private final String resolvedFileName;
        private final String resolvedFileContent;

        public TargetFileCallable(TaskListener Listener, String ResolvedFileName, String ResolvedFileContent, EnvVars environment) {
            this.listener = Listener;
            this.resolvedFileName = ResolvedFileName;
            this.resolvedFileContent = ResolvedFileContent;
            this.environment = environment;
        }

        @Override
        public Boolean invoke(File ws, VirtualChannel channel) {
            boolean result;
            try {
                FilePath fpWS = new FilePath(ws);
                FilePath fpTL = new FilePath(fpWS, resolvedFileName);
                if (fpTL.exists()) {
                    listener.getLogger().println(resolvedFileName + " file already exists, replacing the content with the provided content.");
                }
                listener.getLogger().println("Creating file: " + fpTL.getRemote());
                fpTL.write(resolvedFileContent, "UTF-8");
                result = true;
            } catch (RuntimeException e) {
                listener.fatalError(e.getMessage());
                throw e;
            } catch (Exception e) {
                listener.fatalError(e.getMessage());
                result = false;
            }
            return result;
        }

        @Override
        public void checkRoles(RoleChecker checker) throws SecurityException {

        }
    }

    @Extension
    @Symbol("fileCreateOperation")
    public static class DescriptorImpl extends FileOperationDescriptor {
        public String getDisplayName() {
            return "File Create";
        }

    }
}
