/**
 * Copyright 2012 Twitter, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.logstash.uaparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Device parser using ua-parser regexes. Extracts device information from user agent strings.
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class DeviceParser {
    List<DeviceParser.DevicePattern> patterns;

    public DeviceParser(List<DeviceParser.DevicePattern> patterns) {
        this.patterns = patterns;
    }

    public Device parse(String agentString) {
        if (agentString == null) {
            return null;
        }
        String device = null;
        for (DeviceParser.DevicePattern p : patterns) {
            if ((device = p.match(agentString)) != null) {
                break;
            }
        }
        if (device == null) device = "Other";
        return new Device(device);
    }

    public static DeviceParser fromList(List<Map<String, String>> configList) {
        List<DeviceParser.DevicePattern> configPatterns = new ArrayList<>();
        for (Map<String, String> configMap : configList) {
            configPatterns.add(DeviceParser.patternFromMap(configMap));
        }
        return new DeviceParser(configPatterns);
    }

    protected static DeviceParser.DevicePattern patternFromMap(Map<String, String> configMap) {
        String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("Device is missing regex");
        }
        Pattern pattern = "i".equals(configMap.get("regex_flag")) // no ohter flags used (by now)
            ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
        return new DeviceParser.DevicePattern(pattern, configMap.get("device_replacement"));
    }

    protected static class DevicePattern {
        private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");
        private final Pattern pattern;
        private final String deviceReplacement;

        public DevicePattern(Pattern pattern, String deviceReplacement) {
            this.pattern = pattern;
            this.deviceReplacement = deviceReplacement;
        }

        public String match(String agentString) {
            Matcher matcher = this.pattern.matcher(agentString);
            if (!matcher.find()) {
                return null;
            }
            String device = null;
            if (this.deviceReplacement != null) {
                if (this.deviceReplacement.contains("$")) {
                    device = this.deviceReplacement;
                    for (String substitution : getSubstitutions(this.deviceReplacement)) {
                        int i = Integer.valueOf(substitution.substring(1));
                        String replacement = matcher.groupCount() >= i && matcher.group(i) != null
                            ? Matcher.quoteReplacement(matcher.group(i)) : "";
                        device = device.replaceFirst("\\" + substitution, replacement);
                    }
                    device = device.trim();
                } else {
                    device = this.deviceReplacement;
                }
            } else if (matcher.groupCount() >= 1) {
                device = matcher.group(1);
            }
            return device;
        }

        private List<String> getSubstitutions(String deviceReplacement) {
            Matcher matcher =
                DeviceParser.DevicePattern.SUBSTITUTIONS_PATTERN.matcher(deviceReplacement);
            List<String> substitutions = new ArrayList<>();
            while (matcher.find()) {
                substitutions.add(matcher.group());
            }
            return substitutions;
        }

    }

}
