package org.sonatype.nexus.plugins.terraform.internal.util.json.model;

import java.util.HashMap;

public class MirrorVersions
{

  private HashMap<String,MirrorVersion> versions;

  public MirrorVersions(HashMap<String,MirrorVersion> versions) {
    setVersions(versions);
  }

  public void setVersions(HashMap<String, MirrorVersion> versions) {
    this.versions = versions;
  }

  public HashMap<String, MirrorVersion> getVersions() {
    return versions;
  }
}
