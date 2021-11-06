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

import java.time.Instant;
import java.time.LocalTime;

import org.junit.Test;

import freemarker.template.TemplateException;
import freemarker.test.TemplateTest;

public class TemporalErrorMessagesTest extends TemplateTest {

    @Test
    public void testExplicitFormatString() throws TemplateException {
        addToDataModel("t", LocalTime.now());
        assertErrorContains("${t?string('yyyy-HH')}", "Failed to format temporal value", "yyyy-HH", "YearOfEra");
    }

    @Test
    public void testDefaultFormatStringBadFormatString() throws TemplateException {
        getConfiguration().setSetting("local_time_format", "ABCDEF");
        addToDataModel("t", LocalTime.now());
        assertErrorContains("${t}", "local_time_format", "ABCDEF");
        assertErrorContains("${t?string}", "local_time_format", "ABCDEF");
    }

    @Test
    public void testDefaultFormatStringIncompatibleFormatString() throws TemplateException {
        getConfiguration().setSetting("local_time_format", "yyyy-HH");
        addToDataModel("t", LocalTime.now());
        // TODO [FREEMARKER-35] Should contain "local_time_format" too
        assertErrorContains("${t}", "Failed to format temporal value", "yyyy-HH", "YearOfEra");
        assertErrorContains("${t?string}", "Failed to format temporal value", "yyyy-HH", "YearOfEra");
    }

}