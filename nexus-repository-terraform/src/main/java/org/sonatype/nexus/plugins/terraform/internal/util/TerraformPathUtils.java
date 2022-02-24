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

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher.State;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 0.0.1
 */
@Named
@Singleton
public class TerraformPathUtils
{
  public static final String DISCOVERY_PATH = ".well-known/terraform.json";
  public static final String API_PATH = "v1";
  public static final String MODULES_PATH = API_PATH + "/modules";
  public static final String PROVIDERS_PATH = API_PATH + "/providers";

  private String token(final TokenMatcher.State state, final String token) {
    return match(state, token);
  }

  public TokenMatcher.State matcherState(final Context context) {
    return context.getAttributes().require(TokenMatcher.State.class);
  }

  private String match(final TokenMatcher.State state, final String name) {
    checkNotNull(state);
    String result = state.getTokens().get(name);
    checkNotNull(result);
    return result;
  }

  public String discoveryPath(final State matcherState) {
    return String.format("%s", DISCOVERY_PATH);
  }

  public String modulesPath(final State matcherState) {
    return String.format("%s/index.json", MODULES_PATH);
  }

  public String moduleVersionsPath(final State matcherState) {
    String namespace = token(matcherState, "namespace");
    String name = token(matcherState, "name");
    String provider = token(matcherState, "provider");
    return String.format("%s/%s/%s/%s/index.json", MODULES_PATH, namespace, name, provider);
  }

  public String providersPath(final State matcherState) {
    return String.format("%s/index.json", PROVIDERS_PATH);
  }

  private String providerPath(final State matcherState) {
    String hostname = token(matcherState, "hostname");
    String namespace = token(matcherState, "namespace");
    String type = token(matcherState, "type");
    return String.format("%s/%s/%s/%s", PROVIDERS_PATH, hostname, namespace, type);
  }

  public String providerVersionsPath(final State matcherState) {
    return String.format("%s/index.json", providerPath(matcherState));
  }

  public String providerVersionPath(final State matcherState) {
    String version = token(matcherState, "version");
    return String.format("%s/%s.json", providerPath(matcherState), version);
  }

  public String providerArchivePath(final State matcherState) {
    String provider = token(matcherState, "provider");
    String type = token(matcherState, "type");
    String version = token(matcherState, "version");
    String os = token(matcherState, "os");
    String arch = token(matcherState, "arch");
    return String.format("%s/%s-%s_%s_%s_%s.zip", providerPath(matcherState), provider, type, version, os, arch);
  }

  private String removeFromPath(String url, String pattern) {
    return url.replaceAll(pattern, "");
  }

  private String replaceHostname(final String url, final State matcherState) {
    return removeFromPath(url, "/" + token(matcherState, "hostname"));
  }

  private String removeLastPath(final String url) {
    return removeFromPath(url, "/[^/]*$");
  }

  public String toProviderVersionsPath(final String url, final State matcherState) {
    return removeLastPath(replaceHostname(url, matcherState));
  }

  public String toProviderVersionDownloadPath(final String url, final String os,
                                              final String arch, final State matcherState) {
    return toProviderArchiveDownloadPath(url, os, arch, matcherState);
  }

  public String toProviderArchiveDownloadPath(final String url, final State matcherState) {
    String os = token(matcherState, "os");
    String arch = token(matcherState, "arch");
    return toProviderArchiveDownloadPath(url, os, arch, matcherState);
  }

  private String toProviderArchiveDownloadPath(final String url, final String os,
                                              final String arch, final State matcherState) {
    String version = token(matcherState, "version");
    return String.format("%s/%s/download/%s/%s", removeLastPath(replaceHostname(url, matcherState)), version, os, arch);
  }

}
