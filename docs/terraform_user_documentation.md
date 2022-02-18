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


[Terraform](https://add_URL_To_Format_Info_Here/) provides Add_Format_Description_Here.


Full documentation on installing `terraform` can be found on [the Terraform project website](https://add_Format_Install_Info_URL/).


You can create a proxy repository in Nexus Repository Manager (NXRM) that will cache packages from a remote Terraform repository, like
[Add_Format_Repo_Name](https://add_format_repo_url/). Then, you can make the `terraform` client use your Nexus Repository Proxy 
instead of the remote repository.
 
To proxy a Terraform repository, you simply create a new 'terraform (proxy)' as documented in 
[Repository Management](https://help.sonatype.com/repomanager3/configuration/repository-management) in
detail. Minimal configuration steps are:

- Define 'Name' - e.g. `terraform-proxy`
- Define URL for 'Remote storage' - e.g. [https://add_format_repo_url/](https://add_format_repo_url/)
- Select a `Blob store` for `Storage`

Using the `terraform` client, you can now download packages from your NXRM Terraform proxy like so:

    $ add client command line example here
    
The command above tells terraform to fetch (and install) packages from your NXRM Terraform proxy. The NXRM Terraform proxy will 
download any missing packages from the remote Terraform repository, and cache the packages on the NXRM Terraform proxy.
The next time any client requests the same package from your NXRM Terraform proxy, the already cached package will
be returned to the client.
