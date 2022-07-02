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
package org.sonatype.nexus.plugins.terraform.internal

import javax.inject.Inject
import javax.inject.Provider

import org.sonatype.nexus.plugins.terraform.internal.security.TerraformSecurityFacet
import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.RecipeSupport
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.attributes.AttributesFacet
import org.sonatype.nexus.repository.cache.NegativeCacheFacet
import org.sonatype.nexus.repository.cache.NegativeCacheHandler
import org.sonatype.nexus.repository.http.PartialFetchHandler
import org.sonatype.nexus.repository.httpclient.HttpClientFacet
import org.sonatype.nexus.repository.purge.PurgeUnusedFacet
import org.sonatype.nexus.repository.search.ElasticSearchFacet
import org.sonatype.nexus.repository.security.SecurityHandler
import org.sonatype.nexus.repository.storage.DefaultComponentMaintenanceImpl
import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.repository.storage.UnitOfWorkHandler
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Context
import org.sonatype.nexus.repository.view.Matcher
import org.sonatype.nexus.repository.view.handlers.ConditionalRequestHandler
import org.sonatype.nexus.repository.view.handlers.ContentHeadersHandler
import org.sonatype.nexus.repository.view.handlers.ExceptionHandler
import org.sonatype.nexus.repository.view.handlers.HandlerContributor
import org.sonatype.nexus.repository.view.handlers.TimingHandler
import org.sonatype.nexus.repository.view.matchers.ActionMatcher
import org.sonatype.nexus.repository.view.matchers.logic.LogicMatchers
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher

import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.DISCOVERY_PATH;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.MODULES_PATH;
import static org.sonatype.nexus.plugins.terraform.internal.util.TerraformPathUtils.PROVIDERS_PATH;
import static org.sonatype.nexus.repository.http.HttpMethods.GET
import static org.sonatype.nexus.repository.http.HttpMethods.HEAD

/**
 * Support for Terraform recipes.
 */
abstract class TerraformRecipeSupport
    extends RecipeSupport
{
  @Inject
  Provider<TerraformSecurityFacet> securityFacet

  @Inject
  Provider<ConfigurableViewFacet> viewFacet

  @Inject
  Provider<StorageFacet> storageFacet

  @Inject
  Provider<ElasticSearchFacet> searchFacet

  @Inject
  Provider<AttributesFacet> attributesFacet

  @Inject
  ExceptionHandler exceptionHandler

  @Inject
  TimingHandler timingHandler

  @Inject
  SecurityHandler securityHandler

  @Inject
  PartialFetchHandler partialFetchHandler

  @Inject
  ConditionalRequestHandler conditionalRequestHandler

  @Inject
  ContentHeadersHandler contentHeadersHandler

  @Inject
  UnitOfWorkHandler unitOfWorkHandler

  @Inject
  HandlerContributor handlerContributor

  @Inject
  Provider<DefaultComponentMaintenanceImpl> componentMaintenanceFacet

  @Inject
  Provider<HttpClientFacet> httpClientFacet

  @Inject
  Provider<PurgeUnusedFacet> purgeUnusedFacet

  @Inject
  Provider<NegativeCacheFacet> negativeCacheFacet

  @Inject
  NegativeCacheHandler negativeCacheHandler

  protected TerraformRecipeSupport(final Type type, final Format format) {
    super(type, format)
  }

  static Matcher discoveryMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${DISCOVERY_PATH}",
            AssetKind.DISCOVERY, GET, HEAD);
  }

  static Matcher modulesMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${MODULES_PATH}",
            AssetKind.MODULES, GET, HEAD);
  }

  static Matcher moduleVersionsMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${MODULES_PATH}/{namespace}/{name}/{provider}/index.json",
            AssetKind.MODULE_VERSIONS, GET, HEAD);
  }

  static Matcher providersMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${PROVIDERS_PATH}",
            AssetKind.PROVIDERS, GET, HEAD);
  }

  static Matcher providerVersionsMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${PROVIDERS_PATH}/{hostname}/{namespace}/{type}/index.json",
            AssetKind.PROVIDER_VERSIONS, GET, HEAD);
  }

  static Matcher providerVersionMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${PROVIDERS_PATH}/{hostname}/{namespace}/{type}/{version}.json",
            AssetKind.PROVIDER_VERSION, GET, HEAD);
  }

  static Matcher providerArchiveMatcher() {
    buildTokenMatcherForPatternAndAssetKind(
            "/${PROVIDERS_PATH}/{hostname}/{namespace}/{type}/{provider}-{type}_{version}_{os}_{arch}.zip",
            AssetKind.PROVIDER_ARCHIVE, GET, HEAD);
  }

  static Matcher buildTokenMatcherForPatternAndAssetKind(final String pattern,
                                                         final AssetKind assetKind,
                                                         final String... actions) {
    LogicMatchers.and(
            new ActionMatcher(actions),
            new TokenMatcher(pattern),
            new Matcher() {
              @Override
              boolean matches(final Context context) {
                context.attributes.set(AssetKind.class, assetKind)
                return true
              }
            }
    )
  }

}
