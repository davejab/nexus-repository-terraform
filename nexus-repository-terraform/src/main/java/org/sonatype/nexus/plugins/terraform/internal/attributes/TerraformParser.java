package org.sonatype.nexus.plugins.terraform.internal.attributes;

import org.sonatype.nexus.plugins.terraform.internal.AssetKind;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

public class TerraformParser {

  public static TerraformAttributes parse(final AssetKind asset, final TokenMatcher.State matcherState) {
    switch (asset) {
      case PROVIDER_VERSIONS:
        return getProviderVersionsAttributes(matcherState);
      case PROVIDER_VERSION:
        return getProviderVersionAttributes(matcherState);
      case PROVIDER_ARCHIVE:
        return getProviderArchiveAttributes(matcherState);
      default:
        return new TerraformAttributes();
    }
  }

  private static TerraformAttributes getProviderVersionsAttributes(TokenMatcher.State state){
    TerraformAttributes attributes = new TerraformAttributes();
    attributes.setType(state.getTokens().get("type"));
    attributes.setHostname(state.getTokens().get("hostname"));
    attributes.setNamespace(state.getTokens().get("namespace"));
    return attributes;
  }

  private static TerraformAttributes getProviderVersionAttributes(TokenMatcher.State state){
    TerraformAttributes attributes = getProviderVersionsAttributes(state);
    attributes.setVersion(state.getTokens().get("version"));
    return attributes;
  }

  private static TerraformAttributes getProviderArchiveAttributes(TokenMatcher.State state){
    TerraformAttributes attributes = getProviderVersionAttributes(state);
    attributes.setProvider(state.getTokens().get("provider"));
    attributes.setOs(state.getTokens().get("os"));
    attributes.setArch(state.getTokens().get("arch"));
    return attributes;
  }

}
