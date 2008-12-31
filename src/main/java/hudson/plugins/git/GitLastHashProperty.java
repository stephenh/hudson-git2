package hudson.plugins.git;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.scm.SCM;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class GitLastHashProperty extends JobProperty<AbstractProject<?, ?>> {

	private String lastHashBuilt;

	@DataBoundConstructor
	public GitLastHashProperty(String lastHashBuilt) {
		this.setLastHashBuilt(lastHashBuilt);
	}

	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {
		SCM genericScm = build.getProject().getScm();
		// Make sure we can cast to GitSCM; it's possible that this will be hudson.scm.NullSCM
		if (!(genericScm instanceof GitSCM))
			return true;
		GitSCM scm = (GitSCM)genericScm;
		GitAPI git = new GitAPI(scm.getDescriptor(), launcher, build.getParent().getWorkspace(), listener);
		String tipHash = git.revParse(scm.getRemoteBranch());

		listener.getLogger().println("Setting last build to " + tipHash);
		this.setLastHashBuilt(tipHash);
		build.getProject().save();

		return true;
	}

	public JobPropertyDescriptor getDescriptor() {
		return DescriptorImpl.DESCRIPTOR;
	}

	public static final class DescriptorImpl extends JobPropertyDescriptor {
		public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

		protected DescriptorImpl() {
			super(GitLastHashProperty.class);
			this.load();
		}

		public boolean isApplicable(Class<? extends Job> job) {
			return true;
		}

		public String getDisplayName() {
			return "Last Hash Built";
		}
	}

	public String getLastHashBuilt() {
		return StringUtils.trimToNull(this.lastHashBuilt);
	}

	public void setLastHashBuilt(String hash) {
		this.lastHashBuilt = StringUtils.trimToNull(hash);
	}

}
