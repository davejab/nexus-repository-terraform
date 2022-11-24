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
package org.sonatype.nexus.plugins.terraform.internal.attributes;

/**
 * Terraform content attributes
 *
 * @since 0.0.6
 */
public class TerraformAttributes
{
  private String type;
  private String hostname;
  private String namespace;
  private String provider;
  private String os;
  private String arch;
  private String version;

  public String getType() {
    return type;
  }

  public TerraformAttributes setType(String type) {
    this.type = type;
    return this;
  }

  public String getHostname() {
    return hostname;
  }

  public TerraformAttributes setHostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

  public String getNamespace() {
    return namespace;
  }

  public TerraformAttributes setNamespace(String namespace) {
    this.namespace = namespace;
    return this;
  }
  public String getProvider() {
    return provider;
  }

  public TerraformAttributes setProvider(String provider) {
    this.provider = provider;
    return this;
  }

  public String getOs() {
    return os;
  }

  public TerraformAttributes setOs(String os) {
    this.os = os;
    return this;
  }

  public String getArch() {
    return arch;
  }

  public TerraformAttributes setArch(String arch) {
    this.arch = arch;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public TerraformAttributes setVersion(String version) {
    this.version = version;
    return this;
  }

}
