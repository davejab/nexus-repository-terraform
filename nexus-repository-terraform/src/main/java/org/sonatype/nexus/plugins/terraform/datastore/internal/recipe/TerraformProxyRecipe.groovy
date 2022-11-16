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

import org.sonatype.nexus.plugins.terraform.datastore.TerraformContentFacet

import org.sonatype.nexus.plugins.terraform.internal.matcher.DiscoveryMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderArchiveMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderVersionMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProviderVersionsMatcher
import org.sonatype.nexus.plugins.terraform.internal.matcher.ProvidersMatcher
import org.sonatype.nexus.plugins.terraform.internal.security.TerraformSecurityFacet
import org.sonatype.nexus.repository.RecipeSupport
import org.sonatype.nexus.repository.content.browse.BrowseFacet
import org.sonatype.nexus.repository.content.maintenance.SingleAssetMaintenanceFacet
import org.sonatype.nexus.repository.content.search.SearchFacet
import org.sonatype.nexus.repository.security.SecurityHandler
import org.sonatype.nexus.repository.view.handlers.ContentHeadersHandler
import org.sonatype.nexus.repository.view.handlers.ExceptionHandler
import org.sonatype.nexus.repository.view.handlers.TimingHandler

import javax.annotation.Nonnull
import javax.annotation.Priority
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.cache.NegativeCacheFacet
import org.sonatype.nexus.repository.cache.NegativeCacheHandler
import org.sonatype.nexus.repository.http.HttpMethods
import org.sonatype.nexus.repository.http.PartialFetchHandler
import org.sonatype.nexus.repository.httpclient.HttpClientFacet
import org.sonatype.nexus.repository.proxy.ProxyHandler
import org.sonatype.nexus.repository.purge.PurgeUnusedFacet
import org.sonatype.nexus.plugins.terraform.internal.TerraformFormat
import org.sonatype.nexus.repository.types.ProxyType
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Route
import org.sonatype.nexus.repository.view.Router
import org.sonatype.nexus.repository.view.ViewFacet
import org.sonatype.nexus.repository.view.handlers.ConditionalRequestHandler
import org.sonatype.nexus.repository.view.handlers.HandlerContributor
import org.sonatype.nexus.repository.view.matchers.ActionMatcher
import org.sonatype.nexus.repository.view.matchers.SuffixMatcher

import static org.sonatype.nexus.repository.http.HttpHandlers.notFound
import static org.sonatype.nexus.repository.view.matchers.logic.LogicMatchers.and

/**
 * Terraform proxy repository recipe.
 *
 * @since 0.0.6
 */
@Named(TerraformProxyRecipe.NAME)
@Priority(Integer.MAX_VALUE)
@Singleton
class TerraformProxyRecipe
    extends RecipeSupport
{
  public static final String NAME = 'terraform-proxy'

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
  ProxyHandler proxyHandler

  @Inject
  NegativeCacheHandler negativeCacheHandler

  @Inject
  HandlerContributor handlerContributor

  @Inject
  Provider<TerraformContentFacet> contentFacet

  @Inject
  Provider<TerraformProxyFacet> proxyFacet

  @Inject
  Provider<TerraformSecurityFacet> securityFacet

  @Inject
  Provider<ConfigurableViewFacet> viewFacet

  @Inject
  Provider<SearchFacet> searchFacet

  @Inject
  Provider<SingleAssetMaintenanceFacet> maintenanceFacet

  @Inject
  Provider<HttpClientFacet> httpClientFacet

  @Inject
  Provider<NegativeCacheFacet> negativeCacheFacet

  @Inject
  Provider<PurgeUnusedFacet> purgeUnusedFacet

  @Inject
  Provider<BrowseFacet> browseFacet

  @Inject
  TerraformProxyRecipe(@Named(ProxyType.NAME) final Type type,
                 @Named(TerraformFormat.NAME) final Format format
  )
  {
    super(type, format)
  }

  @Override
  void apply(final @Nonnull Repository repository) throws Exception {
    repository.attach(securityFacet.get())
    repository.attach(configure(viewFacet.get()))
    repository.attach(contentFacet.get())
    repository.attach(httpClientFacet.get())
    repository.attach(negativeCacheFacet.get())
    repository.attach(maintenanceFacet.get())
    repository.attach(proxyFacet.get())
    repository.attach(searchFacet.get())
    repository.attach(browseFacet.get())
    repository.attach(purgeUnusedFacet.get())
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

  /**
   * Configure {@link ViewFacet}.
   */
  private ViewFacet configure(final ConfigurableViewFacet facet) {
    Router.Builder builder = new Router.Builder()

    // Additional handlers, such as the lastDownloadHandler, are intentionally
    // not included on this route because this route forwards to the route below.
    // This route specifically handles GET / and forwards to /index.html.
    builder.route(new Route.Builder()
        .matcher(and(new ActionMatcher(HttpMethods.GET), new SuffixMatcher('/')))
        .handler(timingHandler)
        .create()
    )

    [
            newDiscoveryRouteBuilder(),
            newProvidersRouteBuilder(),
            newProviderVersionsRouteBuilder(),
            newProviderVersionRouteBuilder(),
            newProviderArchiveRouteBuilder()
    ].each { routeBuilder ->
      builder.route(routeBuilder
              .handler(timingHandler)
              .handler(securityHandler)
              .handler(exceptionHandler)
              .handler(handlerContributor)
              .handler(negativeCacheHandler)
              .handler(partialFetchHandler)
              .handler(contentHeadersHandler)
              .handler(conditionalRequestHandler)
              .handler(proxyHandler)
              .create())
    }

    builder.defaultHandlers(notFound())

    facet.configure(builder.create())

    return facet
  }
}
