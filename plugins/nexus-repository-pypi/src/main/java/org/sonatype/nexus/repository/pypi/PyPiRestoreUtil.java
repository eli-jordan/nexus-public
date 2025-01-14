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
package org.sonatype.nexus.repository.pypi;

import org.sonatype.nexus.repository.pypi.datastore.internal.ContentPypiPathUtils;
import org.sonatype.nexus.repository.pypi.internal.PyPiFileUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple util class to provide limited exposure to specific utility
 * methods within the internal package.
 *
 * @since 3.16
 */
public class PyPiRestoreUtil
{
  public static String extractVersionFromPath(final String path) {
    checkNotNull(path);
    String fileName = PyPiFileUtils.extractFilenameFromPath(path);
    if (fileName.length() > 0) {
      return PyPiFileUtils.extractVersionFromFilename(fileName);
    }
    return null;
  }

  public static boolean isIndex(final String path) {
    checkNotNull(path);
    return PyPiPathUtils.isIndexPath(path) || PyPiPathUtils.isRootIndexPath(path) || ContentPypiPathUtils.isIndex(path);
  }

  public static boolean isRootIndex(final String path) {
    checkNotNull(path);
    return PyPiPathUtils.isRootIndexPath(path) || ContentPypiPathUtils.isRootIndexPath(path);
  }

  private PyPiRestoreUtil() {
    // no op
  }
}
