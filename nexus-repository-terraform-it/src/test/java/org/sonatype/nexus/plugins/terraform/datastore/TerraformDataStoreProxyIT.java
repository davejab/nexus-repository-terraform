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
package org.sonatype.nexus.plugins.terraform.datastore;

import java.util.LinkedHashMap;

import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.content.Asset;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.DISCOVERY_PATH;
import static org.sonatype.nexus.testsuite.testsupport.FormatClientSupport.status;

public class TerraformDataStoreProxyIT
    extends TerraformDatastoreITSupport
{

  @Test
  public void unresponsiveRemoteProduces404() throws Exception {
    assertThat(status(proxyClient.get(BAD_PATH)), is(HttpStatus.NOT_FOUND));
  }

  @Test
  public void retrieveDiscoveryJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(DISCOVERY_PATH)), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, "/" + DISCOVERY_PATH).get();

    assertThat(asset.path(), is(equalTo("/" + DISCOVERY_PATH)));
//    assertThat(asset.attributes()., is(equalTo(MIME_TYPE_JSON)));
//    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
  }

  @Test
  public void retrieveIndexJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_INDEX.replaceFirst("/", ""))), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, PROVIDER_INDEX).get();
    assertThat(asset.path(), is(equalTo(PROVIDER_INDEX)));
//    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_JSON)));
//    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    LinkedHashMap attributes = (LinkedHashMap) asset.attributes().get("terraform");
    assertThat(attributes.get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(attributes.get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(attributes.get("type"), is(equalTo(TYPE)));
  }

  @Test
  public void retrieveVersionJSONFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_VERSION.replaceFirst("/", ""))), is(HttpStatus.OK));
    final Asset asset = findAsset(proxyRepo, PROVIDER_VERSION).get();
    assertThat(asset.path(), is(equalTo(PROVIDER_VERSION)));
//    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_JSON)));
//    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    LinkedHashMap attributes = (LinkedHashMap) asset.attributes().get("terraform");
    assertThat(attributes.get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(attributes.get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(attributes.get("type"), is(equalTo(TYPE)));
    assertThat(attributes.get("version"), is(equalTo(VERSION)));
  }

  @Test
  public void retrieveProviderZipFromProxyWhenRemoteOnline() throws Exception {
    assertThat(status(proxyClient.get(PROVIDER_ZIP.replaceFirst("/", ""))), is(HttpStatus.OK));

    final Asset asset = findAsset(proxyRepo, PROVIDER_ZIP).get();
    assertThat(asset.path(), is(equalTo(PROVIDER_ZIP)));
//    assertThat(asset.contentType(), is(equalTo(MIME_TYPE_ZIP)));
//    assertThat(asset.format(), is(equalTo(FORMAT_NAME)));
    LinkedHashMap attributes = (LinkedHashMap) asset.attributes().get("terraform");
    assertThat(attributes.get("hostname"), is(equalTo(HOSTNAME)));
    assertThat(attributes.get("namespace"), is(equalTo(NAMESPACE)));
    assertThat(attributes.get("type"), is(equalTo(TYPE)));
    assertThat(attributes.get("version"), is(equalTo(VERSION)));
    assertThat(attributes.get("os"), is(equalTo(OS)));
    assertThat(attributes.get("arch"), is(equalTo(ARCH)));
  }


}
