/*
 * Copyright 2025 dScope.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dscope.camel.sample;

import org.apache.camel.main.Main;

/**
 * Sample application demonstrating Apache Camel YAML route configuration
 * with RosettaNet PIP 3A4 message processing.
 */
public class YamlRouteApplication {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        
        // Configure Camel to load routes from YAML files
        main.configure().withRoutesIncludePattern("classpath:routes.yaml");
        
        // Run the application
        main.run(args);
    }
}
