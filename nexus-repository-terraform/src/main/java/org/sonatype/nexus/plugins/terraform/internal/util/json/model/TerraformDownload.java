package org.sonatype.nexus.plugins.terraform.internal.util.json.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerraformDownload {

  private String os;
  private String arch;
  private String filename;
  @JsonProperty("download_url")
  private String downloadUrl;

  public TerraformDownload() {}

  public String getOs() {
    return this.os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getArch() {
    return this.arch;
  }

  public void setArch(String arch) {
    this.arch = arch;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getDownloadUrl() {
    return this.downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getPlatform() {
    return getOs() + "_" + getArch();
  }

}
