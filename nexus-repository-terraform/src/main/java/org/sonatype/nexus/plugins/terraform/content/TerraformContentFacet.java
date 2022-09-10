/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.terraform.content;

import java.io.IOException;
import java.util.Optional;

import org.sonatype.nexus.common.hash.HashAlgorithm;
import org.sonatype.nexus.plugins.terraform.internal.Attributes.TerraformAttributes;
import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.content.facet.ContentFacet;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;

import com.google.common.collect.ImmutableList;

import static org.sonatype.nexus.common.hash.HashAlgorithm.MD5;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA1;

/**
 * Provides persistent content for the 'terraform' format.
 *
 * @since 0.0.6
 */
@Facet.Exposed
public interface TerraformContentFacet
        extends ContentFacet
{
  ImmutableList<HashAlgorithm> HASHING = ImmutableList.of(MD5, SHA1);

  Optional<Content> get(String path) throws IOException;

  FluentAsset getOrCreateAsset(Repository repository, String componentName, String componentGroup,
                               String assetName, TerraformAttributes attributes);

  Content put(String path, Payload content, TerraformAttributes attributes) throws IOException;

  boolean delete(String path) throws IOException;
}
