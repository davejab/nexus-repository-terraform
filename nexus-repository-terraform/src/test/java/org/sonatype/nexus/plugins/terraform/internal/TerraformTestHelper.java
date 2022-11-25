package org.sonatype.nexus.plugins.terraform.internal;

import java.util.HashMap;
import java.util.Map;

public class TerraformTestHelper
{

  public String discovery, api, provider, module, providerPath, modulesPath, namespace, name, hostname, type, version,
          os, arch, providerVersionsPath, providerVersionPath, providerArchivePath, providerVersionDownloadPath;

  public TerraformTestHelper(){
    discovery = "/.well-known/terraform.json";
    api = "v1";
    provider = "provider";
    module = "module";
    providerPath = String.format("%s/%ss", api, provider);
    modulesPath = String.format("%s/%ss", api, module);
    namespace = "namespace";
    name = "name";
    hostname = "hostname";
    type = "type";
    version = "version";
    os = "os";
    arch = "arch";

    providerVersionsPath = String.format("/%s/%s/%s/%s/index.json", providerPath, hostname, namespace, type);
    providerVersionPath = String.format("/%s/%s/%s/%s/%s.json", providerPath, hostname, namespace, type, version);
    providerArchivePath = String.format("/%s/%s/%s/%s/%s-%s_%s_%s_%s.zip",
            providerPath, hostname, namespace, type, provider, type, version, os, arch);
    providerVersionDownloadPath = String.format("/%s/%s/%s/%s/download/%s/%s",
            providerPath, namespace, type, version, os, arch);
  }

  public Map<String, String> getMatcherTokens() {
    Map<String, String> tokens = new HashMap<>();
    tokens.put(namespace, namespace);
    tokens.put(name, name);
    tokens.put(provider, provider);
    tokens.put(hostname, hostname);
    tokens.put(type, type);
    tokens.put(version, version);
    tokens.put(os, os);
    tokens.put(arch, arch);
    return tokens;
  }
}
