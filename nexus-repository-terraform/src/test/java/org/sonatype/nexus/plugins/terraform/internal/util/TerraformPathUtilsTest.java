/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2022-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.terraform.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class TerraformPathUtilsTest
    extends TestSupport
{
  private TerraformPathUtils underTest;

  @Mock
  TokenMatcher.State state;

  private Map<String, String> tokens;

  private String discovery, api, providerPath, modulesPath, provider,
          module, namespace, name, hostname, type, version, os, arch,
          providerVersionsPath, providerVersionPath,
          providerArchivePath, providerVersionDownloadPath;

  @Before
  public void setUp() {
    underTest = new TerraformPathUtils();

    discovery = ".well-known/terraform.json";
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

    tokens = setupTokens();
    when(state.getTokens()).thenReturn(tokens);

    providerVersionsPath = String.format("%s/%s/%s/%s/index.json", providerPath, hostname, namespace, type);
    providerVersionPath = String.format("%s/%s/%s/%s/%s.json", providerPath, hostname, namespace, type, version);
    providerArchivePath = String.format("%s/%s/%s/%s/%s-%s_%s_%s_%s.zip",
            providerPath, hostname, namespace, type, provider, type, version, os, arch);
    providerVersionDownloadPath = String.format("%s/%s/%s/%s/download/%s/%s",
            providerPath, namespace, type, version, os, arch);
  }

  @Test
  public void discoveryPath() {
    String result = underTest.discoveryPath(state);
    assertThat(result, is(equalTo(discovery)));
  }
  @Test
  public void modulesPath() {
    String expect = String.format("%s/index.json", modulesPath);
    String result = underTest.modulesPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void moduleVersionsPath() {
    String expect = String.format("%s/%s/%s/%s/index.json", modulesPath, namespace, name, provider);
    String result = underTest.moduleVersionsPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void providersPath() {
    String expect = String.format("%s/index.json", providerPath);
    String result = underTest.providersPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void providerVersionsPath() {
    String result = underTest.providerVersionsPath(state);
    assertThat(result, is(equalTo(providerVersionsPath)));
  }

  @Test
  public void providerVersionPath() {
    String result = underTest.providerVersionPath(state);
    assertThat(result, is(equalTo(providerVersionPath)));
  }

  @Test
  public void providerArchivePath() {
    String result = underTest.providerArchivePath(state);
    assertThat(result, is(equalTo(providerArchivePath)));
  }

  @Test
  public void toProviderVersionsPath() {
    String expect = String.format("%s/%s/%s", providerPath, namespace, type);
    String result = underTest.toProviderVersionsPath(providerVersionsPath, state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void toProviderVersionDownloadPath() {
    String result = underTest.toProviderVersionDownloadPath(providerVersionPath, os, arch, state);
    assertThat(result, is(equalTo(providerVersionDownloadPath)));
  }

  @Test
  public void toProviderArchiveDownloadPath() {
    String result = underTest.toProviderArchiveDownloadPath(providerArchivePath, state);
    assertThat(result, is(equalTo(providerVersionDownloadPath)));
  }

  private Map<String, String> setupTokens() {
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
