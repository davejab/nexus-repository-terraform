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

import java.net.URL;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.sonatype.goodies.httpfixture.server.fluent.Behaviours;
import org.sonatype.goodies.httpfixture.server.fluent.Server;
import org.sonatype.nexus.pax.exam.NexusPaxExamSupport;
import org.sonatype.nexus.plugins.terraform.internal.fixtures.RepositoryRuleTerraform;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.testsuite.testsupport.RepositoryITSupport;

import org.junit.Rule;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.DISCOVERY_PATH;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.PROVIDERS_PATH;

public class TerraformITSupport
    extends RepositoryITSupport
{
  protected static final String FORMAT_NAME = "terraform";

  protected static final String EXTENSION_JSON = ".json";

  protected static final String EXTENSION_ZIP = ".zip";

  protected static final String MIME_TYPE_JSON = "application/json";

  protected static final String MIME_TYPE_ZIP = "application/zip";

  protected static final String BAD_PATH = "this/path/is/bad";

  protected static final String HOSTNAME = "registry.terraform.io";

  protected static final String NAMESPACE = "hashicorp";

  protected static final String TYPE = "terraform";

  protected static final String VERSION = "0.1.0";

  protected static final String OS = "linux";

  protected static final String ARCH = "amd64";

  protected static final String PROVIDER_PATH = "/" + PROVIDERS_PATH + "/" + HOSTNAME + "/" + NAMESPACE + "/" + TYPE;

  protected static final String PROVIDER_INDEX = PROVIDER_PATH + "/index" + EXTENSION_JSON;

  protected static final String PROVIDER_VERSION = PROVIDER_PATH + "/" + VERSION + EXTENSION_JSON;

  protected static final String PROVIDER_ZIP = PROVIDER_PATH +
          "/terraform-provider-" + TYPE + "_" + VERSION + "_" + OS + "_" + ARCH + EXTENSION_ZIP;

  protected static final String REMOTE_PROVIDER_PATH = PROVIDERS_PATH + "/" + NAMESPACE + "/" + TYPE;

  protected static final String REMOTE_DOWNLOAD_PATH = REMOTE_PROVIDER_PATH + "/" + VERSION +
          "/download/" + OS + "/" + ARCH;

  protected TerraformClient proxyClient;

  protected Repository proxyRepo;

  protected Server server;

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

  @After
  public void tearDown() throws Exception {
    server.stop();
  }

  @Rule
  public RepositoryRuleTerraform repos = new RepositoryRuleTerraform(() -> repositoryManager);

  @Override
  protected RepositoryRuleTerraform createRepositoryRule() {
    return new RepositoryRuleTerraform(() -> repositoryManager);
  }

  public TerraformITSupport() {
    testData.addDirectory(NexusPaxExamSupport.resolveBaseFile("target/it-resources/terraform"));
  }

  @Nonnull
  protected TerraformClient terraformClient(final Repository repository) throws Exception {
    checkNotNull(repository);
    return terraformClient(repositoryBaseUrl(repository));
  }

  protected TerraformClient terraformClient(final URL repositoryUrl) throws Exception {
    return new TerraformClient(
            clientBuilder(repositoryUrl).build(),
            clientContext(),
            repositoryUrl.toURI()
    );
  }

}
