package io.mvnpm.maven.locker.mojos;

import io.mvnpm.maven.locker.model.Artifacts;
import io.mvnpm.maven.locker.pom.LockerPomFileAccessor;
import io.mvnpm.maven.locker.model.GAV;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static io.mvnpm.maven.locker.LockerConstants.LOCKER_POM_PATH;

public abstract class AbstractDependencyLockMojo extends AbstractMojo {


  @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
  protected File basedir;

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  protected MavenProject project;

  protected LockerPomFileAccessor lockFile() {
    return LockerPomFileAccessor.fromBasedir(basedir, LOCKER_POM_PATH);
  }

  protected Artifacts projectDependencies() {
    return Artifacts.fromMavenArtifacts(project.getArtifacts());
  }

  protected GAV pomMinimums() {
    return GAV.from(project);
  }

  protected String projectVersion() {
    return project.getVersion();
  }
}
