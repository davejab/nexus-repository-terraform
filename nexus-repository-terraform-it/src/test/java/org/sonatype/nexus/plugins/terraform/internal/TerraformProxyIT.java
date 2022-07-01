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
package org.sonatype.nexus.plugins.terraform.internal;

import org.junit.After;
import org.junit.Before;
import org.sonatype.goodies.httpfixture.server.fluent.Behaviours;
import org.sonatype.goodies.httpfixture.server.fluent.Server;
import org.sonatype.nexus.pax.exam.NexusPaxExamSupport;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.testsuite.testsupport.NexusITSupport;

import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.DISCOVERY_PATH;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.PROVIDERS_PATH;
import static org.sonatype.nexus.testsuite.testsupport.FormatClientSupport.status;

public class TerraformProxyIT
    extends TerraformITSupport
{
  private static final String FORMAT_NAME = "terraform";

  private static final String EXTENSION_JSON = ".json";

  private static final String EXTENSION_ZIP = ".zip";

  private static final String MIME_TYPE_JSON = "application/json";

  private static final String MIME_TYPE_ZIP = "application/zip";

  private static final String BAD_PATH = "this/path/is/bad";

  private static final String HOSTNAME = "registry.terraform.io";

  private static final String NAMESPACE = "hashicorp";

  private static final String TYPE = "terraform";

  private static final String VERSION = "0.1.0";

  private static final String OS = "linux";

  private static final String ARCH = "amd64";

  private static final String PROVIDER_PATH = PROVIDERS_PATH + "/" + HOSTNAME + "/" + NAMESPACE + "/" + TYPE;

  private static final String PROVIDER_INDEX = PROVIDER_PATH + "/index" + EXTENSION_JSON;

  private static final String PROVIDER_VERSION = PROVIDER_PATH + "/" + VERSION + EXTENSION_JSON;

  private static final String PROVIDER_ZIP = PROVIDER_PATH +
          "/terraform-provider-" + TYPE + "_" + VERSION + "_" + OS + "_" + ARCH + EXTENSION_ZIP;

  private static final String REMOTE_PROVIDER_PATH = PROVIDERS_PATH + "/" + NAMESPACE + "/" + TYPE;

  private static final String REMOTE_DOWNLOAD_PATH = REMOTE_PROVIDER_PATH + "/" + VERSION +
          "/download/" + OS + "/" + ARCH;

  private TerraformClient proxyClient;

  private Repository proxyRepo;

  private Server server;

  @Configuration
  public static Option[] configureNexus() {
    return NexusPaxExamSupport.options(
        NexusITSupport.configureNexusBase(),
        nexusFeature("org.sonatype.nexus.plugins", "nexus-repository-terraform")
    );
  }

  @Before
  public void setup() throws Exception {
    server = Server.withPort(0)
            .serve("/" + DISCOVERY_PATH)
            .withBehaviours(Behaviours.file(testData.resolveFile(FORMAT_NAME + EXTENSION_JSON)))
            .serve("/" + REMOTE_PROVIDER_PATH)
            .withBehaviours(Behaviours.file(testData.resolveFile(TYPE)))
            .serve("/" + REMOTE_DOWNLOAD_PATH)
            .withBehaviours(Behaviours.file(testData.resolveFile(ARCH)))
            .start();
    proxyRepo = repos.createTerraformProxy("terraform-test-proxy", server.getUrl().toExternalForm());
    proxyClient = terraformClient(proxyRepo);
  }

  @Test
  public void unresponsiveRemoteProduces404() throws Exception {
    assertThat(status(proxyClient.get(BAD_PATH)), is(HttpStatus.NOT_FOUND));
  }

  @Test
  public void retrieveDiscoveryJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(DISCOVERY_PATH)), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, DISCOVERY_PATH);
    assertThat(asset.name(), is(equalTo(DISCOVERY_PATH)));
    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_JSON)));
    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
  }

  @Test
  public void retrieveIndexJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_INDEX)), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, PROVIDER_INDEX);
    assertThat(asset.name(), is(equalTo(PROVIDER_INDEX)));
    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_JSON)));
    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    assertThat(asset.formatAttributes().get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(asset.formatAttributes().get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(asset.formatAttributes().get("type"), is(equalTo(TYPE)));
  }

  @Test
  public void retrieveVersionJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_VERSION)), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, PROVIDER_VERSION);
    assertThat(asset.name(), is(equalTo(PROVIDER_VERSION)));
    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_JSON)));
    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    assertThat(asset.formatAttributes().get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(asset.formatAttributes().get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(asset.formatAttributes().get("type"), is(equalTo(TYPE)));
    assertThat(asset.formatAttributes().get("version"), is(equalTo(VERSION)));
  }

  @Test
  public void retrieveProviderZipFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_ZIP)), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, PROVIDER_ZIP);
    assertThat(asset.name(), is(equalTo(PROVIDER_ZIP)));
    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_ZIP)));
    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    assertThat(asset.formatAttributes().get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(asset.formatAttributes().get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(asset.formatAttributes().get("type"), is(equalTo(TYPE)));
    assertThat(asset.formatAttributes().get("version"), is(equalTo(VERSION)));
    assertThat(asset.formatAttributes().get("os"), is(equalTo(OS)));
    assertThat(asset.formatAttributes().get("arch"), is(equalTo(ARCH)));
  }

  @After
  public void tearDown() throws Exception {
    server.stop();
  }
}
