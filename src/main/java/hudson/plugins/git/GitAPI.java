package hudson.plugins.git;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class GitAPI {

	private final String gitExe;
	private final Launcher launcher;
	private final FilePath workspace;
	private final TaskListener listener;

	public GitAPI(GitSCM.DescriptorImpl descriptor, Launcher launcher, FilePath workspace, TaskListener listener) {
		this.gitExe = descriptor.getGitExe();
		this.launcher = launcher;
		this.workspace = workspace;
		this.listener = listener;
	}

	public boolean hasGitRepo() throws InterruptedException, IOException {
		return this.workspace.child(".git").exists();
	}

	public boolean hasGitModules() throws InterruptedException, IOException {
		return this.workspace.child(".gitmodules").exists();
	}

	public String revParse(String revName) throws InterruptedException, IOException {
		ArgumentListBuilder args = new ArgumentListBuilder();
		args.add(this.gitExe, "rev-parse", revName.replace(' ', '_'));

		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		int code = this.launcher.launch(args.toCommandArray(), this.createEnvVarMap(), fos, this.workspace).join();
		fos.close();
		if (code != 0) {
			this.listener.getLogger().println("Error doing rev-parse: " + fos.toString());
			return null;
		}

		return StringUtils.trimToNull(fos.toString());
	}

	public void log(String revFrom, String revTo, File file) throws InterruptedException, IOException {
		ArgumentListBuilder args = new ArgumentListBuilder();
		args.add(this.gitExe, "log", "--numstat", "-M", "--summary", "--pretty=raw", revFrom + ".." + revTo);

		file.delete();
		FileOutputStream fos = new FileOutputStream(file);
		// fos.write("<data><![CDATA[".getBytes());
		int code = this.launcher.launch(args.toCommandArray(), this.createEnvVarMap(), fos, this.workspace).join();
		// fos.write("]]></data>".getBytes());
		fos.close();
		if (code != 0) {
			this.listener.getLogger().print("Error launching git log: " + FileUtils.readFileToString(file));
			throw new RuntimeException("Error launching git log");
		}
	}

	/** Start from scratch and clone the whole repository. */
	public void clone(String source) throws InterruptedException, IOException {
		this.workspace.deleteRecursive();
		this.launch("Failed to clone " + source, this.gitExe, "clone", source, this.workspace.getRemote());
	}

	public void fetch() throws InterruptedException, IOException {
		this.launch("Failed to fetch", this.gitExe, "fetch");
	}

	public void merge(String revSpec) throws InterruptedException, IOException {
		this.launch("Error in merging " + revSpec, this.gitExe, "merge", revSpec);
	}

	public void submoduleInit() throws InterruptedException, IOException {
		this.launch("Error in submodule init", this.gitExe, "submodule", "init");
	}

	public void submoduleUpdate() throws InterruptedException, IOException {
		this.launch("Error in submodule update", this.gitExe, "submodule", "update");
	}

	public void checkout(String ref) throws InterruptedException, IOException {
		this.launch("Error checking out " + ref, this.gitExe, "checkout", "-f", ref);
	}

	private void launch(String error, String... args) throws InterruptedException, IOException {
		int code = this.launcher.launch(args, this.createEnvVarMap(), this.listener.getLogger(),
				this.workspace.exists() ? this.workspace : null).join();
		if (code != 0) {
			throw new RuntimeException(error);
		}
	}

	private final Map<String, String> createEnvVarMap() {
		Map<String, String> env = new HashMap<String, String>();
		return env;
	}

}
