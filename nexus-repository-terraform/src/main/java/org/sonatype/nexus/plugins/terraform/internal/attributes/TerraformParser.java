package org.sonatype.nexus.plugins.terraform.internal.attributes;

import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

public class TerraformParser {

  public static TerraformAttributes parse(final AssetKind asset, final TokenMatcher.State matcherState) {

    TerraformAttributes attributes = new TerraformAttributes();

    switch (asset) {
      case PROVIDER_VERSIONS:
        attributes.setType(matcherState.getTokens().get("type"));
        attributes.setHostname(matcherState.getTokens().get("hostname"));
        attributes.setNamespace(matcherState.getTokens().get("namespace"));
      case PROVIDER_VERSION:
        attributes.setType(matcherState.getTokens().get("type"));
        attributes.setHostname(matcherState.getTokens().get("hostname"));
        attributes.setNamespace(matcherState.getTokens().get("namespace"));
        attributes.setVersion(matcherState.getTokens().get("version"));
      case PROVIDER_ARCHIVE:
        attributes.setType(matcherState.getTokens().get("type"));
        attributes.setHostname(matcherState.getTokens().get("hostname"));
        attributes.setNamespace(matcherState.getTokens().get("namespace"));
        attributes.setVersion(matcherState.getTokens().get("version"));
        attributes.setProvider(matcherState.getTokens().get("provider"));
        attributes.setOs(matcherState.getTokens().get("os"));
        attributes.setArch(matcherState.getTokens().get("arch"));
    }
    return attributes;
  }

}
