/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.repository.pypi.internal;

import javax.inject.Named;

import org.sonatype.nexus.repository.browse.node.BrowseNode;
import org.sonatype.nexus.repository.browse.node.BrowseNodeIdentity;
import org.sonatype.nexus.repository.pypi.PyPiFormat;

/**
 * PyPi custom {@link BrowseNodeIdentity}
 *
 * @since 3.22
 */
@Named(PyPiFormat.NAME)
public class PyPiBrowseNodeIdentity
    implements BrowseNodeIdentity
{
  /**
   * Changes default behavior to be distinct and ignore case
   */
  @Override
  public String identity(final BrowseNode browseNode) {
    return browseNode.getName().toLowerCase();
  }
}
