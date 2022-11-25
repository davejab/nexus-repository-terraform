<!--

    Sonatype Nexus (TM) Open Source Version
    Copyright (c) 2022-present Sonatype, Inc.
    All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.

    This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
    which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.

    Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
    of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
    Eclipse Foundation. All other trademarks are the property of their respective owners.

-->
<!-- generated with nexus-format-archetype version 1.0.56 on 18-Feb-2022 -->
# Nexus Repository Terraform Format

[![Maven Central](https://img.shields.io/maven-central/v/org.sonatype.nexus.plugins/nexus-repository-terraform.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.sonatype.nexus.plugins%22%20AND%20a:%22nexus-repository-terraform%22) [![CircleCI](https://circleci.com/gh/davejab/nexus-repository-terraform.svg?style=shield)](https://circleci.com/gh/davejab/nexus-repository-terraform) [![Join the chat at https://gitter.im/sonatype/nexus-developers](https://badges.gitter.im/sonatype/nexus-developers.svg)](https://gitter.im/sonatype/nexus-developers?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Table Of Contents

* [Developing](#developing)
   * [Requirements](#requirements)
   * [Download](#download)
   * [Building](#building)
* [Using Terraform with Nexus Repository Manager 3](#using-terraform-with-nexus-repository-manager-3)
* [Compatibility with Nexus Repository Manager 3 Versions](#compatibility-with-nexus-repository-manager-3-versions)
* [Features Implemented In This Plugin](#features-implemented-in-this-plugin)
   * [Supported Terraform Commands](#supported-terraform-commands)
* [Installing the plugin](#installing-the-plugin)
   * [Easiest Install](#easiest-install)
   * [Temporary Install](#temporary-install)
   * [(more) Permanent Install](#more-permanent-install)
   * [(most) Permament Install](#most-permanent-install)
* [The Fine Print](#the-fine-print)
* [Getting Help](#getting-help)
* [Integration Tests](#integration-tests)

## Developing

### Requirements

* [Apache Maven 3.3.3+](https://maven.apache.org/install.html)
* [Java 8](https://adoptopenjdk.net)
* Network access to https://repository.sonatype.org/content/groups/sonatype-public-grid

Also, there is a good amount of information available at [Bundle Development](https://help.sonatype.com/display/NXRM3/Bundle+Development).

You may also find it helpful to configure your IDE to use the [Sonatype Code style](https://github.com/sonatype/codestyle).

### Download

 Find pre-compiled files [here](https://github.com/davejab/nexus-repository-terraform/releases).

### Building

To build the project and generate the bundle use Maven

    mvn clean package -PbuildKar

If everything checks out, the bundle for terraform should be available in the `target` folder

#### Build with Docker

    docker build -t nexus-repository-terraform .

#### Run as a Docker container

    docker run -d -p 8081:8081 --name nexus-repository-terraform nexus-repository-terraform 

For further information like how to persist volumes check out [the GitHub repo for our official image](https://github.com/sonatype/docker-nexus3).

After allowing some time to spin up, the application will be available from your browser at http://localhost:8081.

To read the generated admin password for your first login to the web UI, you can use the command below against the running docker container:

    docker exec -it nexus-repository-terraform cat /nexus-data/admin.password && echo

For simplicity, you should check `Enable anonymous access` in the prompts following your first login.   

## Using Terraform With Nexus Repository Manager 3

[We have detailed instructions on how to get started here!](docs/terraform_user_documentation.md)

## Compatibility with Nexus Repository Manager 3 Versions

The table below outlines what version of Nexus Repository the plugin was built against

| Plugin Version | Nexus Repository Version |
|----------------|--------------------------|
| v0.0.1-1       | 3.31.0-01                |
| v0.0.6         | 3.38.0-01                |

If a new version of Nexus Repository is released and the plugin needs changes, a new release will be made, and this
table will be updated to indicate which version of Nexus Repository it will function against. This is done on a time 
available basis, as this is community supported. If you see a new version of Nexus Repository, go ahead and update the
plugin and send us a PR after testing it out!

All released versions can be found [here](https://github.com/davejab/nexus-repository-terraform/releases).

## Features Implemented In This Plugin 

| Feature | Implemented          |
|---------|----------------------|
| Proxy   | :heavy_check_mark:   |
| Hosted  |                      |
| Group   |                      |

### Supported Terraform Commands

#### Proxy

| Command          | Implemented |
|------------------|--------------------------|
| `terraform init` | :heavy_check_mark:       |

## Installing the plugin

There are a range of options for installing the terraform plugin. You'll need to build it first, and
then install the plugin with the options shown below:

### Easiest Install

Thanks to some upstream work in Nexus Repository, it's become a LOT easier to install a plugin. To install the `terraform` plugin, follow these steps:

* Build the plugin with `mvn clean package -PbuildKar`
* Copy the `nexus-repository-terraform-x.y.z-bundle.kar` file from your `target` folder to the `deploy` folder for your Nexus Repository installation.

Once you've done this, go ahead and either restart Nexus Repo, or go ahead and start it if it wasn't running to begin with.

You should see `terraform (proxy)` in the available Repository Recipes to use, if all has gone according to plan :)

### Temporary Install

Installations done via the Karaf console will be wiped out with every restart of Nexus Repository. This is a
good installation path if you are just testing or doing development on the plugin.

* Enable Nexus Repo console: edit `<nexus_dir>/bin/nexus.vmoptions` and change `karaf.startLocalConsole`  to `true`.

  More details here: [Bundle Development](https://help.sonatype.com/display/NXRM3/Bundle+Development+Overview)

* Run Nexus Repo console:
  ```
  # sudo su - nexus
  $ cd <nexus_dir>/bin
  $ ./nexus run
  > bundle:install file:///tmp/nexus-repository-terraform-x.y.z.jar
  > bundle:list
  ```
  (look for org.sonatype.nexus.plugins:nexus-repository-terraform ID, should be the last one)
  ```
  > bundle:start <org.sonatype.nexus.plugins:nexus-repository-terraform ID>
  ```

### (more) Permanent Install

For more permanent installs of the nexus-repository-terraform plugin, follow these instructions:

* Copy the bundle (nexus-repository-terraform-x.y.z.jar) into <nexus_dir>/deploy

This will cause the plugin to be loaded with each restart of Nexus Repository. As well, this folder is monitored
by Nexus Repository and the plugin should load within 60 seconds of being copied there if Nexus Repository
is running. You will still need to start the bundle using the karaf commands mentioned in the temporary install.

### (most) Permanent Install

If you are trying to use the terraform plugin permanently, it likely makes more sense to do the following:

* Copy the bundle into `<nexus_dir>/system/org/sonatype/nexus/plugins/nexus-repository-terraform/x.y.z/nexus-repository-terraform-x.y.z.jar`
* Make the following additions marked with + to `<nexus_dir>/system/org/sonatype/nexus/assemblies/nexus-core-feature/3.x.y/nexus-core-feature-3.x.y-features.xml`

   ```
         <feature prerequisite="false" dependency="false">wrap</feature>
   +     <feature prerequisite="false" dependency="false">nexus-repository-terraform</feature>
   ```
   to the `<feature name="nexus-core-feature" description="org.sonatype.nexus.assemblies:nexus-core-feature" version="3.x.y.xy">` section below the last (above is an example, the exact last one may vary).
   
   And
   ```
   + <feature name="nexus-repository-terraform" description="org.sonatype.nexus.plugins:nexus-repository-terraform" version="x.y.z">
   +     <details>org.sonatype.nexus.plugins:nexus-repository-terraform</details>
   +     <bundle>mvn:org.sonatype.nexus.plugins/nexus-repository-terraform/x.y.z</bundle>
   + </feature>
    </features>
   ```
   as the last feature.
   
This will cause the plugin to be loaded and started with each startup of Nexus Repository.

## The Fine Print

It is worth noting that this is **NOT SUPPORTED** by Sonatype, and is a contribution of ours
to the open source community (read: you!)

Don't worry, using this community item does not "void your warranty". In a worst case scenario, you may be asked 
by the Sonatype Support team to remove the community item in order to determine the root cause of any issues.

Remember:

* Use this contribution at the risk tolerance that you have
* Do NOT file Sonatype support tickets related to Terraform support in regard to this plugin
* DO file issues here on GitHub, so that the community can pitch in

Phew, that was easier than I thought. Last but not least of all:

Have fun creating and using this plugin and the Nexus platform, we are glad to have you here!

## Getting help

Looking to contribute to our code but need some help? There's a few ways to get information:

* Chat with us on [Gitter](https://gitter.im/sonatype/nexus-developers)
* Check out the [Nexus3](http://stackoverflow.com/questions/tagged/nexus3) tag on Stack Overflow
* Check out the [Nexus Repository User List](https://groups.google.com/a/glists.sonatype.com/forum/?hl=en#!forum/nexus-users)

## Integration Tests

There a still some rough edges around writing integration tests, which are noted below.
Please report any problems you find. 

The project has a “format” module, and an “IT” module. 
   This allows the “format” module to be bundled up and used by the IT framework classes in the “it” module.
   In this project, the sub module: [nexus-repository-terraform](nexus-repository-terraform) is the "format" module.
   The sub module: [nexus-repository-terraform-it](nexus-repository-terraform-it) is the "it" module.
      
#### Debugging ITs

  You can connect a remote debugger to port 5005 to debug Integration Tests. Just add the `-Dit.debug=true` argument 
  when running ITs. For example:
  
    mvn clean verify -Dit.debug=true

  After the IT starts (you would see the following in a terminal:
  
      ...
      [INFO] --- maven-failsafe-plugin:2.18.1:integration-test (default) @ nexus-repository-...-it ---
      ...
      -------------------------------------------------------
       T E S T S
      -------------------------------------------------------
      Running org.sonatype.nexus.plugins...

   ), you can attach a remote debugger to port 5005. Keep trying to attach the remote debugger until
   the connection succeeds.

   After each IT runs, you have to reconnect the remote debugger.
   
   You can run a single IT by adding the `-Dit.test=MyIntegrationTestToRunIT` property. The example below also skips
    running the unit tests.
    
      mvn clean verify -Dit.debug=true -Dtest=skip -Dit.test=MyIntegrationTestToRunIT
      
  When running ITs, the Nexus Repository Manager will write log output to the following file:
  
      nexus-repository-terraform/nexus-repository-terraform-it/target/it-data/1/nexus3/log/nexus.log
      
   With multiple ITs, the `1` in the path above will be incremented for each IT.
