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

import java.io.IOException;
import java.util.ArrayList;

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.repository.view.Content;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.nexus.repository.view.payloads.StringPayload;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TerraformDataUtilsTest
        extends TestSupport
{
  private TerraformDataUtils underTest;

  @Before
  public void setUp() {
    underTest = new TerraformDataUtils();
  }

  @Test
  public void providerVersionsJson() throws IOException {
    String testJson = "{\"versions\":[\"0.1.0\",\"0.1.1\",\"0.1.2\"]}";
    String expectedJson = "{\"versions\":{\"0.1.0\":{},\"0.1.1\":{},\"0.1.2\":{}}}";

    Content expectedContent = mockContent(expectedJson);
    Content result = underTest.providerVersionsJson(mockContent(testJson));

    assertThat(result.toString(), is(equalTo(expectedContent.toString())));
  }

  @Test
  public void providerVersionJson() throws IOException {
    String[] testJsons = new String[]{
            "{\"os\":\"linux\",\"arch\":\"amd64\",\"filename\":\"test_linux_amd64.zip\"}",
            "{\"os\":\"darwin\",\"arch\":\"amd64\",\"filename\":\"test_darwin_amd64.zip\"}"
    };
    ArrayList<Content> testContents = new ArrayList<>();
    for (String testJson : testJsons) {
      testContents.add(mockContent(testJson));
    }
    Content result = underTest.providerVersionJson(testContents);
    String expectedJson = "{\"archives\":{\"darwin_amd64\":{\"url\":\"test_darwin_amd64.zip\"}," +
            "\"linux_amd64\":{\"url\":\"test_linux_amd64.zip\"}}}";
    Content expectedContent = mockContent(expectedJson);
    assertThat(result.toString(), is(equalTo(expectedContent.toString())));
  }

  @Test
  public void getDownloadUrl() throws IOException {
    String testJson = "{\"download_url\":\"https://example.com/test_linux_amd64.zip\"}";
    String expected = "https://example.com/test_linux_amd64.zip";

    Content testContent = mockContent(testJson);
    String result = underTest.getDownloadUrl(testContent);

    assertThat(result, is(equalTo(expected)));
  }

  private Content mockContent(String json){
    return new Content(new StringPayload(json, "text/plain"));
  }

}
