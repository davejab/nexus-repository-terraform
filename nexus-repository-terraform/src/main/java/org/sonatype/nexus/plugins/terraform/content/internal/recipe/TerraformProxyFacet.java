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
package org.sonatype.nexus.plugins.terraform.content.internal.recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.plugins.terraform.content.TerraformContentFacet;
import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.plugins.terraform.internal.Attributes.TerraformAttributes;
import org.sonatype.nexus.plugins.terraform.internal.util.TerraformDataUtils;
import org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils;
import org.sonatype.nexus.repository.content.facet.ContentProxyFacetSupport;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

/**
 * Terraform proxy facet.
 *
 * @since 0.0.6
 */
@Named
public class TerraformProxyFacet
    extends ContentProxyFacetSupport
{
  private TerraformPathUtils terraformPathUtils;

  private TerraformDataUtils terraformDataUtils;

  @Inject
  public TerraformProxyFacet()
  {
    this.terraformPathUtils = new TerraformPathUtils();
    this.terraformDataUtils = new TerraformDataUtils();
  }

  @Override
  protected Content getCachedContent(final Context context) throws IOException {
    return content().get(terraformPathUtils.getAssetPath(context)).orElse(null);
  }

  @Override
  protected Content store(final Context context, final Content payload) throws IOException {
    TokenMatcher.State matcherState = terraformPathUtils.matcherState(context);
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TerraformAttributes attributes = TerraformAttributes.parse(assetKind, matcherState);
    return content().put(terraformPathUtils.getAssetPath(context), payload, attributes);
  }

  @Override
  protected Content fetch(String url, Context context, Content stale) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = terraformPathUtils.matcherState(context);
    Content response;
    switch (assetKind) {
      case PROVIDER_VERSION:
        ArrayList<String> downloads = new ArrayList<>();
        for(Map.Entry os : terraformDataUtils.getPlatformMap().entrySet()) {
          for (String arch: (String[])os.getValue()) {
            String downloadUrl = terraformPathUtils
                    .toProviderVersionDownloadPath(url, (String)os.getKey(), arch, matcherState);
            log.debug("Fetching filename for {} on {} from {}", os.getKey(), arch, downloadUrl);
            response = super.fetch(downloadUrl, context, null);
            if (response == null){
              log.debug("Filename for {} on {} not found", os.getKey(), arch);
              continue;
            }
            downloads.add(terraformDataUtils.contentToString(response));
            response.close();
          }
        }
        return terraformDataUtils.providerVersionJson(downloads);
      case PROVIDER_VERSIONS:
        url = terraformPathUtils.toProviderVersionsPath(url, matcherState);
        log.debug("Fetching versions from {}", url);
        response = super.fetch(url, context, stale);
        Content versions = terraformDataUtils.providerVersionsJson(response);
        response.close();
        return versions;
      case PROVIDER_ARCHIVE:
        String downloadInfoUrl = terraformPathUtils.toProviderArchiveDownloadPath(url, matcherState);
        log.debug("Fetching download link from {}", downloadInfoUrl);
        response = super.fetch(downloadInfoUrl, context, stale);
        String downloadUrl = terraformDataUtils.getDownloadUrl(response);
        response.close();
        log.debug("Fetching archive from {}", downloadUrl);
        return super.fetch(downloadUrl, context, stale);
      default:
        return super.fetch(url, context, stale);
    }
  }

  @Override
  protected String getUrl(final Context context) {
    String url = context.getRequest().getPath();
    if(url != null && url.startsWith("/")) {
      return url.replaceFirst("/", "");
    }
    return url;
  }

  private TerraformContentFacet content() {
    return getRepository().facet(TerraformContentFacet.class);
  }

}
