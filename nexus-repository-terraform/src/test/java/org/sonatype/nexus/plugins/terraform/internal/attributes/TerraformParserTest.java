package org.sonatype.nexus.plugins.terraform.internal.attributes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.plugins.terraform.internal.TerraformTestHelper;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import static org.sonatype.nexus.plugins.terraform.internal.AssetKind.*;
import static org.sonatype.nexus.plugins.terraform.internal.attributes.TerraformParser.parse;

public class TerraformParserTest
        extends TestSupport
{

  @Mock
  TokenMatcher.State state;
  private TerraformTestHelper testHelper;
  private TerraformAttributes expected;


  @Before
  public void setUp() {
    testHelper = new TerraformTestHelper();
    when(state.getTokens()).thenReturn(testHelper.getMatcherTokens());

    expected = new TerraformAttributes();
    expected.setType(testHelper.type);
    expected.setHostname(testHelper.hostname);
    expected.setNamespace(testHelper.namespace);
    expected.setVersion(testHelper.version);
    expected.setProvider(testHelper.provider);
    expected.setOs(testHelper.os);
    expected.setArch(testHelper.arch);
  }

  @Test
  public void parseProviderVersions() {
    TerraformAttributes actual = parse(PROVIDER_VERSIONS, state);
    assertThat(actual.getType(), is(equalTo(expected.getType())));
    assertThat(actual.getHostname(), is(equalTo(expected.getHostname())));
    assertThat(actual.getNamespace(), is(equalTo(expected.getNamespace())));
  }

  @Test
  public void parseProviderVersion() {
    TerraformAttributes actual = parse(PROVIDER_VERSION, state);
    assertThat(actual.getType(), is(equalTo(expected.getType())));
    assertThat(actual.getHostname(), is(equalTo(expected.getHostname())));
    assertThat(actual.getNamespace(), is(equalTo(expected.getNamespace())));
    assertThat(actual.getVersion(), is(equalTo(expected.getVersion())));
  }

  @Test
  public void parseProviderArchive() {
    TerraformAttributes actual = parse(PROVIDER_ARCHIVE, state);
    assertThat(actual.getType(), is(equalTo(expected.getType())));
    assertThat(actual.getHostname(), is(equalTo(expected.getHostname())));
    assertThat(actual.getNamespace(), is(equalTo(expected.getNamespace())));
    assertThat(actual.getVersion(), is(equalTo(expected.getVersion())));
    assertThat(actual.getProvider(), is(equalTo(expected.getProvider())));
    assertThat(actual.getOs(), is(equalTo(expected.getOs())));
    assertThat(actual.getArch(), is(equalTo(expected.getArch())));
  }

}
