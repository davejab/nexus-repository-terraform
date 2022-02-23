package org.sonatype.nexus.plugins.terraform.internal.util.json.model;

import java.util.HashMap;

public class MirrorArchives {

  private HashMap<String,MirrorUrl> archives;

  public MirrorArchives(HashMap archives) {
    setArchives(archives);
  }

  public HashMap<String, MirrorUrl> getArchives() {
    return this.archives;
  }

  public void setArchives(HashMap<String, MirrorUrl> archives) {
    this.archives = archives;
  }

}
