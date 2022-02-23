package org.sonatype.nexus.plugins.terraform.internal.util.json.model;

public class MirrorUrl {

  private String url;

  public MirrorUrl(String url) {
    setUrl(url);
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
