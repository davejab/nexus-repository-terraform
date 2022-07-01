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
package org.sonatype.nexus.plugins.terraform.internal.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.common.collect.AttributesMap;
import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.plugins.terraform.internal.util.TerraformDataAccess;
import org.sonatype.nexus.plugins.terraform.internal.util.TerraformDataUtils;
import org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.config.Configuration;
import org.sonatype.nexus.repository.proxy.ProxyFacet;
import org.sonatype.nexus.repository.proxy.ProxyFacetSupport;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.transaction.TransactionalStoreBlob;
import org.sonatype.nexus.repository.transaction.TransactionalTouchBlob;
import org.sonatype.nexus.repository.transaction.TransactionalTouchMetadata;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;
import org.sonatype.nexus.repository.view.payloads.TempBlob;
import org.sonatype.nexus.transaction.UnitOfWork;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.repository.storage.AssetEntityAdapter.P_ASSET_KIND;

/**
 * Terraform {@link ProxyFacet} implementation.
 *
 * @since 0.0.1
 */
@Named
public class TerraformProxyFacetImpl
    extends ProxyFacetSupport
    implements TerraformProxyFacet
{
  private TerraformPathUtils terraformPathUtils;

  private TerraformDataAccess terraformDataAccess;

  private TerraformDataUtils terraformDataUtils;

  @Inject
  public TerraformProxyFacetImpl(final TerraformPathUtils terraformPathUtils,
                             final TerraformDataAccess terraformDataAccess)
  {
    this.terraformPathUtils = checkNotNull(terraformPathUtils);
    this.terraformDataAccess = checkNotNull(terraformDataAccess);
    this.terraformDataUtils = new TerraformDataUtils();
  }

  // HACK: Workaround for known CGLIB issue, forces an Import-Package for org.sonatype.nexus.repository.config
  @Override
  protected void doValidate(final Configuration configuration) throws Exception {
    super.doValidate(configuration);
  }

  @Nullable
  @Override
  protected Content getCachedContent(final Context context) {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = terraformPathUtils.matcherState(context);
    switch (assetKind) {
      case DISCOVERY:
        return getAsset(terraformPathUtils.discoveryPath(matcherState));
      case MODULES:
        return getAsset(terraformPathUtils.modulesPath(matcherState));
      case MODULE_VERSIONS:
        return getAsset(terraformPathUtils.moduleVersionsPath(matcherState));
      case PROVIDERS:
        return getAsset(terraformPathUtils.providersPath(matcherState));
      case PROVIDER_VERSION:
        return getAsset(terraformPathUtils.providerVersionPath(matcherState));
      case PROVIDER_VERSIONS:
        return getAsset(terraformPathUtils.providerVersionsPath(matcherState));
      case PROVIDER_ARCHIVE:
        return getAsset(terraformPathUtils.providerArchivePath(matcherState));
      default:
        throw new IllegalStateException("Received an invalid AssetKind of type: " + assetKind.name());
    }
  }

  @TransactionalTouchBlob
  public Content getAsset(final String assetPath) {
    StorageTx tx = UnitOfWork.currentTx();

    Asset asset = terraformDataAccess.findAsset(tx, tx.findBucket(getRepository()), assetPath);
    if (asset == null) {
      return null;
    }
    return terraformDataAccess.toContent(asset, tx.requireBlob(asset.requireBlobRef()));
  }

  @Override
  protected Content store(final Context context, final Content content) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = terraformPathUtils.matcherState(context);
    switch (assetKind) {
      case DISCOVERY:
        return putTerraformPackage(content, assetKind, terraformPathUtils.discoveryPath(matcherState), matcherState);
      case MODULES:
        return putTerraformPackage(content, assetKind, terraformPathUtils.modulesPath(matcherState), matcherState);
      case MODULE_VERSIONS:
        return putTerraformPackage(content, assetKind, terraformPathUtils.moduleVersionsPath(matcherState), matcherState);
      case PROVIDERS:
        return putTerraformPackage(content, assetKind, terraformPathUtils.providersPath(matcherState), matcherState);
      case PROVIDER_VERSIONS:
        return putTerraformPackage(content, assetKind, terraformPathUtils.providerVersionsPath(matcherState), matcherState);
      case PROVIDER_VERSION:
        return putTerraformPackage(content, assetKind, terraformPathUtils.providerVersionPath(matcherState), matcherState);
      case PROVIDER_ARCHIVE:
        return putTerraformPackage(content, assetKind, terraformPathUtils.providerArchivePath(matcherState), matcherState);
      default:
        throw new IllegalStateException("Received an invalid AssetKind of type: " + assetKind.name());
    }
  }

  private Content putTerraformPackage(final Content content,
                                  final AssetKind assetKind,
                                  final String assetPath,
                                  final TokenMatcher.State matcherState)
      throws IOException
  {
    StorageFacet storageFacet = facet(StorageFacet.class);

    try (TempBlob tempBlob = storageFacet.createTempBlob(content.openInputStream(), TerraformDataAccess.HASH_ALGORITHMS)) {
      Component component = findOrCreateComponent(assetPath);

      return findOrCreateAsset(tempBlob, content, assetKind, assetPath, component, matcherState);
    }
  }

  @TransactionalStoreBlob
  protected Component findOrCreateComponent(final String assetPath) {
    StorageTx tx = UnitOfWork.currentTx();
    Bucket bucket = tx.findBucket(getRepository());

    Component component = terraformDataAccess.findComponent(tx,
        getRepository(),
        assetPath);

    if (component == null) {
      component = tx.createComponent(bucket, getRepository().getFormat())
          .name(assetPath);
    }
    tx.saveComponent(component);

    return component;
  }

//  private Content putMetadata(final Content content,
//                              final AssetKind assetKind,
//                              final String assetPath) throws IOException
//  {
//    StorageFacet storageFacet = facet(StorageFacet.class);
//
//    try (TempBlob tempBlob = storageFacet.createTempBlob(content.openInputStream(), TerraformDataAccess.HASH_ALGORITHMS)) {
//      return findOrCreateAsset(tempBlob, content, assetKind, assetPath, null);
//    }
//  }

  @TransactionalStoreBlob
  protected Content findOrCreateAsset(final TempBlob tempBlob,
                                      final Content content,
                                      final AssetKind assetKind,
                                      final String assetPath,
                                      final Component component,
                                      final TokenMatcher.State matcherState) throws IOException
  {
    StorageTx tx = UnitOfWork.currentTx();
    Bucket bucket = tx.findBucket(getRepository());

    Asset asset = terraformDataAccess.findAsset(tx, bucket, assetPath);
    // @todo
//    if (assetKind.equals(AssetKind.PROVIDER_ARCHIVE)) {
//      if (asset == null) {
//        asset = tx.createAsset(bucket, component);
//        asset.name(assetPath);
//        asset.formatAttributes().set(P_ASSET_KIND, assetKind.name());
//      }
//    } else {
      if (asset == null) {
        asset = tx.createAsset(bucket, getRepository().getFormat());
        asset.name(assetPath);
        for (String key : matcherState.getTokens().keySet()) {
          asset.formatAttributes().set(key, matcherState.getTokens().get(key));
        }
      }
//    }

    return terraformDataAccess.saveAsset(tx, asset, tempBlob, content);
  }

  @Override
  protected void indicateVerified(final Context context, final Content content, final CacheInfo cacheInfo)
      throws IOException
  {
    setCacheInfo(content, cacheInfo);
  }

  @TransactionalTouchMetadata
  public void setCacheInfo(final Content content, final CacheInfo cacheInfo) throws IOException {
    StorageTx tx = UnitOfWork.currentTx();
    Asset asset = Content.findAsset(tx, tx.findBucket(getRepository()), content);
    if (asset == null) {
      log.debug(
          "Attempting to set cache info for non-existent Terraform asset {}", content.getAttributes().require(Asset.class)
      );
      return;
    }
    log.debug("Updating cacheInfo of {} to {}", asset, cacheInfo);
    CacheInfo.applyToAsset(asset, cacheInfo);
    tx.saveAsset(asset);
  }

  @Override
  protected Content fetch(String url, Context context, Content stale) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = terraformPathUtils.matcherState(context);
    Content response;
    switch (assetKind) {
      case PROVIDER_VERSION:
        ArrayList<String> downloads = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        for(Entry os : terraformDataUtils.getPlatformMap().entrySet()) {
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
            attributesMap = response.getAttributes();
            response.close();
          }   
        }
        Content content = terraformDataUtils.providerVersionJson(downloads);
        for (String key : attributesMap.keys()) {
          content.getAttributes().set(key, attributesMap.get(key));
        }
        return content;
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
  protected String getUrl(@Nonnull final Context context) {
    return context.getRequest().getPath().substring(1);
  }
}
