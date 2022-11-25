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

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.plugins.terraform.internal.TerraformTestHelper;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

import org.mockito.Mock;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class TerraformPathUtilsTest
    extends TestSupport
{
  private TerraformPathUtils underTest;

  private TerraformTestHelper testHelper;

  @Mock
  TokenMatcher.State state;

  @Before
  public void setUp() {
    underTest = new TerraformPathUtils();
    testHelper = new TerraformTestHelper();
    when(state.getTokens()).thenReturn(testHelper.getMatcherTokens());
  }

  @Test
  public void discoveryPath() {
    String result = underTest.discoveryPath(state);
    assertThat(result, is(equalTo(testHelper.discovery)));
  }
  @Test
  public void modulesPath() {
    String expect = String.format("/%s/index.json", testHelper.modulesPath);
    String result = underTest.modulesPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void moduleVersionsPath() {
    String expect = String.format("/%s/%s/%s/%s/index.json", testHelper.modulesPath, testHelper.namespace, testHelper.name, testHelper.provider);
    String result = underTest.moduleVersionsPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void providersPath() {
    String expect = String.format("/%s/index.json", testHelper.providerPath);
    String result = underTest.providersPath(state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void providerVersionsPath() {
    String result = underTest.providerVersionsPath(state);
    assertThat(result, is(equalTo(testHelper.providerVersionsPath)));
  }

  @Test
  public void providerVersionPath() {
    String result = underTest.providerVersionPath(state);
    assertThat(result, is(equalTo(testHelper.providerVersionPath)));
  }

  @Test
  public void providerArchivePath() {
    String result = underTest.providerArchivePath(state);
    assertThat(result, is(equalTo(testHelper.providerArchivePath)));
  }

  @Test
  public void toProviderVersionsPath() {
    String expect = String.format("/%s/%s/%s", testHelper.providerPath, testHelper.namespace, testHelper.type);
    String result = underTest.toProviderVersionsPath(testHelper.providerVersionsPath, state);
    assertThat(result, is(equalTo(expect)));
  }

  @Test
  public void toProviderVersionDownloadPath() {
    String result = underTest.toProviderVersionDownloadPath(testHelper.providerVersionPath, testHelper.os, testHelper.arch, state);
    assertThat(result, is(equalTo(testHelper.providerVersionDownloadPath)));
  }

  @Test
  public void toProviderArchiveDownloadPath() {
    String result = underTest.toProviderArchiveDownloadPath(testHelper.providerArchivePath, state);
    assertThat(result, is(equalTo(testHelper.providerVersionDownloadPath)));
  }

}
