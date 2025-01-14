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
package org.sonatype.nexus.repository.pypi.internal

import org.sonatype.nexus.repository.pypi.PyPiPathUtils

import spock.lang.Specification
import spock.lang.Unroll

/**
 * {@link PyPiPathUtils} unit tests.
 */
class PyPiPathUtilsTest
    extends Specification
{
  @Unroll
  def 'Normalize name #initialName following the PEP 503 standard'() {
    expect:
      PyPiPathUtils.normalizeName(initialName) == normalizedName
    where:
      initialName  | normalizedName
      'some-name'  | 'some-name'
      'Some_Name'  | 'some-name'
      'some_name'  | 'some-name'
      'some.name'  | 'some-name'
      'some--name' | 'some-name'
      'some__name' | 'some-name'
      'some..name' | 'some-name'
  }
}
