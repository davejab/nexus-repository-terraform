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
package org.sonatype.nexus.plugins.terraform.internal.Attributes;

import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

/**
 * @since 0.0.6
 */
public class TerraformAttributes
{

  public static TerraformAttributes parse(final AssetKind asset, final TokenMatcher.State matcherState) {

    switch (asset) {
      case PROVIDER_VERSIONS:
        TerraformIndexAttributes indexAttributes = new TerraformIndexAttributes();
        indexAttributes.setType(matcherState.getTokens().get("type"));
        indexAttributes.setHostname(matcherState.getTokens().get("hostname"));
        indexAttributes.setNamespace(matcherState.getTokens().get("namespace"));
        return indexAttributes;
      case PROVIDER_VERSION:
        TerraformVersionAttributes versionAttributes = new TerraformVersionAttributes();
        versionAttributes.setType(matcherState.getTokens().get("type"));
        versionAttributes.setHostname(matcherState.getTokens().get("hostname"));
        versionAttributes.setNamespace(matcherState.getTokens().get("namespace"));
        versionAttributes.setVersion(matcherState.getTokens().get("version"));
        return versionAttributes;
      case PROVIDER_ARCHIVE:
        TerraformArchiveAttributes archiveAttributes = new TerraformArchiveAttributes();
        archiveAttributes.setType(matcherState.getTokens().get("type"));
        archiveAttributes.setHostname(matcherState.getTokens().get("hostname"));
        archiveAttributes.setNamespace(matcherState.getTokens().get("namespace"));
        archiveAttributes.setVersion(matcherState.getTokens().get("version"));
        archiveAttributes.setProvider(matcherState.getTokens().get("provider"));
        archiveAttributes.setOs(matcherState.getTokens().get("os"));
        archiveAttributes.setArch(matcherState.getTokens().get("arch"));
        return archiveAttributes;
    }
    return new TerraformAttributes();
  }

}
