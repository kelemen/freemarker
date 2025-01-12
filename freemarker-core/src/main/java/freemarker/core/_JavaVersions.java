/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package freemarker.core;

import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.SecurityUtilities;

/**
 * Used internally only, might change without notice!
 */
public final class _JavaVersions {
    
    private _JavaVersions() {
        // Not meant to be instantiated
    }

    private static final boolean IS_AT_LEAST_16 = isAtLeast(16, "java.net.UnixDomainSocketAddress");

    /**
     * {@code null} if Java 8 is not available, otherwise the object through with the Java 8 operations are available.
     */
    static public final _Java16 JAVA_16;
    static {
        _Java16 java16;
        if (IS_AT_LEAST_16) {
            try {
                java16 = (_Java16) Class.forName("freemarker.core._Java16Impl").getField("INSTANCE").get(null);
            } catch (Exception e) {
                try {
                    Logger.getLogger("freemarker.runtime").error("Failed to access Java 16 functionality", e);
                } catch (Exception e2) {
                    // Suppressed
                }
                java16 = null;
            }
        } else {
            java16 = null;
        }
        JAVA_16 = java16;
    }

    private static boolean isAtLeast(int minimumMinorVersion, String proofClassPresence) {
        boolean result = false;
        String vStr = SecurityUtilities.getSystemProperty("java.version", null);
        if (vStr != null) {
            try {
                Version v = new Version(vStr);
                result = v.getMajor() == 1 && v.getMinor() >= minimumMinorVersion || v.getMajor() > 1;
            } catch (Exception e) {
                // Ignore
            }
        } else {
            try {
                Class.forName(proofClassPresence);
                result = true;
            } catch (Exception e) {
                // Ignore
            }
        }
        return result;
    }

}
