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
package org.sonatype.nexus.plugins.terraform.internal.matcher;

import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.repository.view.Matcher;
import org.sonatype.nexus.repository.view.matchers.ActionMatcher;
import org.sonatype.nexus.repository.view.matchers.logic.LogicMatchers;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

import static org.sonatype.nexus.repository.http.HttpMethods.GET;
import static org.sonatype.nexus.repository.http.HttpMethods.HEAD;

/**
 * Abstract terraform path matcher
 *
 * @since 0.0.6
 */
public abstract class TerraformMatcher
{

  private String pattern;
  private AssetKind asset;

  protected TerraformMatcher(String pattern, AssetKind asset) {
    this.pattern = pattern;
    this.asset = asset;
  }

  public Matcher matcher() {
    return buildTokenMatcherForPatternAndAssetKind(pattern, asset, GET, HEAD);
  }

  private Matcher buildTokenMatcherForPatternAndAssetKind(final String pattern,
                                                         final AssetKind assetKind,
                                                         final String... actions) {
    return LogicMatchers.and(
            new ActionMatcher(actions),
            new TokenMatcher(pattern),
            context -> {
              context.getAttributes().set(AssetKind.class, assetKind);
              return true;
            }
    );
  }
}
