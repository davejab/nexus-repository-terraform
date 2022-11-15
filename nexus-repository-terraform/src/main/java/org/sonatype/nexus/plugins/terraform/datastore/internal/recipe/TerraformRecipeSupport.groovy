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
package org.sonatype.nexus.plugins.terraform.datastore.internal.recipe

import javax.inject.Inject
import javax.inject.Provider

import org.sonatype.nexus.plugins.terraform.internal.matcher.DiscoveryMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderArchiveMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderVersionMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderVersionsMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProvidersMatcher
import org.sonatype.nexus.plugins.terraform.internal.security.TerraformSecurityFacet
import org.sonatype.nexus.plugins.terraform.datastore.TerraformContentFacet
import org.sonatype.nexus.plugins.terraform.datastore.internal.TerraformIndexHtmlForwardHandler
import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.RecipeSupport
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.content.browse.BrowseFacet
import org.sonatype.nexus.repository.content.maintenance.SingleAssetMaintenanceFacet
import org.sonatype.nexus.repository.content.search.SearchFacet
import org.sonatype.nexus.repository.http.PartialFetchHandler
import org.sonatype.nexus.repository.security.SecurityHandler
import org.sonatype.nexus.repository.view.Route
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.handlers.ConditionalRequestHandler
import org.sonatype.nexus.repository.view.handlers.ContentHeadersHandler
import org.sonatype.nexus.repository.view.handlers.ExceptionHandler
import org.sonatype.nexus.repository.view.handlers.HandlerContributor
import org.sonatype.nexus.repository.view.handlers.LastDownloadedHandler
import org.sonatype.nexus.repository.view.handlers.TimingHandler

/**
 * @since 0.0.6
 */
abstract class TerraformRecipeSupport
    extends RecipeSupport
{
  @Inject
  Provider<TerraformSecurityFacet> securityFacet

  @Inject
  Provider<ConfigurableViewFacet> viewFacet

  @Inject
  Provider<TerraformContentFacet> contentFacet

  @Inject
  Provider<SingleAssetMaintenanceFacet> maintenanceFacet

  @Inject
  Provider<SearchFacet> searchFacet

  @Inject
  Provider<BrowseFacet> browseFacet

//  @Inject
//  Provider<TerraformReplicationFacet> replicationFacet

  @Inject
  ExceptionHandler exceptionHandler

  @Inject
  TimingHandler timingHandler

  @Inject
  TerraformIndexHtmlForwardHandler indexHtmlForwardHandler

  @Inject
  SecurityHandler securityHandler

  @Inject
  PartialFetchHandler partialFetchHandler

//  @Inject
//  TerraformContentHandler contentHandler

  @Inject
  ConditionalRequestHandler conditionalRequestHandler

  @Inject
  ContentHeadersHandler contentHeadersHandler

  @Inject
  LastDownloadedHandler lastDownloadedHandler

  @Inject
  HandlerContributor handlerContributor

//  @Inject
//  ContentDispositionHandler contentDispositionHandler

  protected TerraformRecipeSupport(final Type type, final Format format)
  {
    super(type, format)
  }

  Route.Builder newDiscoveryRouteBuilder() {
    return new Route.Builder().matcher(new DiscoveryMatcher().matcher());
  }

  Route.Builder newProvidersRouteBuilder() {
    return new Route.Builder().matcher(new ProvidersMatcher().matcher());
  }

  Route.Builder newProviderVersionsRouteBuilder() {
    return new Route.Builder().matcher(new ProviderVersionsMatcher().matcher());
  }

  Route.Builder newProviderVersionRouteBuilder() {
    return new Route.Builder().matcher(new ProviderVersionMatcher().matcher());
  }

  Route.Builder newProviderArchiveRouteBuilder() {
    return new Route.Builder().matcher(new ProviderArchiveMatcher().matcher());
  }

}
