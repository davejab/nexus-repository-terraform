package org.sonatype.nexus.plugins.terraform.internal.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugins.terraform.internal.util.json.MirrorVersionSerializer;
import org.sonatype.nexus.plugins.terraform.internal.util.json.model.*;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.payloads.StringPayload;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Data utilities for Terraform
 *
 * @since 0.0.1
 */
@Named
@Singleton
public class TerraformDataUtils
{
  private HashMap<String,String[]> platformMap = new HashMap<>();

  private ObjectMapper objectMapper = new ObjectMapper();

  public TerraformDataUtils() {
    getPlatformMap().put("linux", new String[] {"amd64", "arm", "arm64"});
    getPlatformMap().put("darwin", new String[] {"amd64", "arm64"});
    getPlatformMap().put("windows", new String[] {"amd64"});

    SimpleModule module = new SimpleModule();
    module.addSerializer(MirrorVersion.class, new MirrorVersionSerializer());
    getObjectMapper().registerModule(module);
  }

  public HashMap<String,String[]> getPlatformMap(){
    return this.platformMap;
  }

  private ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  public Content providerVersionsJson(final Content content) throws IOException {
    String json = contentToString(content);
    TerraformVersions terraformVersions = getObjectMapper().readValue(json, TerraformVersions.class);
    String mirrorVersionsJson = getObjectMapper().writeValueAsString(terraformVersions.toMirrorVersions());
    return stringToContent(mirrorVersionsJson);
  }

  public Content providerVersionJson(final ArrayList<String> downloads) throws IOException {
    HashMap<String, MirrorUrl> archives = new HashMap<>();
    for(String json : downloads) {
      if (json == null){
        continue;
      }
      TerraformDownload terraformDownload = getObjectMapper().readValue(json, TerraformDownload.class);
      archives.put(terraformDownload.getPlatform(), new MirrorUrl(terraformDownload.getFilename()));
    }
    String mirrorVersionsJson = getObjectMapper().writeValueAsString(new MirrorArchives(archives));
    return stringToContent(mirrorVersionsJson);
  }

  public String getDownloadUrl(final Content content) throws IOException {
    String json = contentToString(content);
    TerraformDownload terraformDownload = getObjectMapper().readValue(json, TerraformDownload.class);
    return terraformDownload.getDownloadUrl();
  }

  private Content stringToContent(final String string) {
    return new Content(new StringPayload(string, "text/plain"));
  }

  public String contentToString(final Content content) throws IOException {
    InputStreamReader inputStreamReader = new InputStreamReader(content.openInputStream(), StandardCharsets.UTF_8);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String result = bufferedReader.lines().collect(Collectors.joining("\n"));
    bufferedReader.close();
    inputStreamReader.close();
    return result;
  }

}
