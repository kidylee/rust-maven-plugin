/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.jar.jni;

import java.util.Locale;

/**
 * Information about the current operating system.
 */
public abstract class OsInfo {

    /**
     * The platform name, e.g. "linux-aarch64", "mac_os_x-x86_64" or "windows-amd64"
     */
    public static final String PLATFORM;

    /**
     * The prefix for native libraries, e.g. "lib", or "" on Windows.
     */
    public static final String LIB_PREFIX;

    /**
     * The suffix for native libraries, e.g. ".so", ".dylib" or ".dll".
     */
    public static final String LIB_SUFFIX;

    /**
     * The suffix for executables, e.g. ".exe" or "" on Unix.
     */
    public static final String EXE_SUFFIX;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            osName = "windows";  // Too many flavours, binaries are compatible.
        }
        final String osArch = System.getProperty("os.arch").toLowerCase();
        PLATFORM = (Os.getCurrent().universalName() + "-" + osArch).replace(' ', '_');

        LIB_PREFIX = osName.startsWith("windows") ? "" : "lib";

        LIB_SUFFIX = osName.startsWith("windows") ? ".dll"
                : osName.contains("mac") ? ".dylib"
                : ".so";

        EXE_SUFFIX = osName.startsWith("windows")
                ? ".exe" : "";
    }

    private enum Os {
        MAC_OS("mac", "darwin") {
            @Override
            public String universalName() {
                return "darwin";
            }
        },
        WINDOWS("win") {
            @Override
            public String universalName() {
                if (currentIs64Bit()) {
                    return "win32-x86-64";
                } else {
                    return "win32-x86";
                }
            }
        },
        GNU_SLASH_LINUX("nux") {
            @Override
            public String universalName() {
                if (currentIs64Bit()) {
                    return "linux-x86-64";
                } else {
                    return "linux-x86";
                }
            }
        },
        UNKNOWN() {
            @Override
            public String universalName()  {
                throw new RuntimeException("Unknown platform. Can't tell what platform we're running on!");
            }
        };
        private final String[] substrings;

        private Os(String... substrings) {
            this.substrings = substrings;
        }

        public abstract String universalName();

        public static Os getCurrent() {
            for (Os os : values()) {
                if (os.isCurrent()) {
                    return os;
                }
            }
            return UNKNOWN;
        }

        public boolean isCurrent() {
            for (String substring : substrings) {
                if (currentOsString().contains(substring)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean currentIs64Bit() {
            return System.getProperty("os.arch").contains("64");
        }

        private static String currentOsString() {
            return System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        }
    }

}

