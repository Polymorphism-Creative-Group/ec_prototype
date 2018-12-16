/*
 * Copyright 2018 Jonathan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.metacontext.ec.prototype.composer.connectors;

import tech.metacontext.ec.prototype.composer.SketchNode;
import tech.metacontext.ec.prototype.composer.styles.FreeStyle;

/**
 *
 * @author Jonathan
 */
public class ConnectorFactory {

    private static ConnectorFactory instance;

    private ConnectorFactory() {

    }

    public static ConnectorFactory getInstance() {

        if (instance == null) {
            instance = new ConnectorFactory();
        }
        return instance;
    }

    public Connector getConnector(SketchNode previous) {

        Connector conn = new Connector(previous, FreeStyle::checker,
                ConnectorType.getRandom());
        switch (ConnectorType.getRandom()) {
            case Total:
            case ByItem:
        }

        return conn;

    }

}
