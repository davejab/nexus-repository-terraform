package org.sonatype.nexus.plugins.terraform.internal.util.json.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerraformVersions {

  private String[] versions;

  protected TerraformVersions() {}

  public String[] getVersions() {
    return versions;
  }

  public void setVersions(String[] versions) {
    this.versions = versions;
  }

  public MirrorVersions toMirrorVersions() {
    HashMap<String,MirrorVersion> versions = new HashMap<>();
    for (String version : getVersions()) {
      versions.put(version, new MirrorVersion());
    }
    return new MirrorVersions(versions);
  }

}
