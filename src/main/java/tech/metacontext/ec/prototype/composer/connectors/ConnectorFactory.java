/*
 * Copyright 2018 Jonathan Chang, Chun-yien <ccy@musicapoetica.org>.
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

import java.util.ArrayList;
import java.util.List;
import tech.metacontext.ec.prototype.abs.Individual;
import tech.metacontext.ec.prototype.composer.nodes.SketchNode;
import tech.metacontext.ec.prototype.composer.abs.Calculator;
import tech.metacontext.ec.prototype.composer.abs.Factory;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class ConnectorFactory extends Factory<Connector> {

   private static ConnectorFactory instance;
   private final List<Calculator> template;

   private ConnectorFactory() {
      this.template = new ArrayList<>();
   }

   public void addCalculator(Calculator cal) {
      this.template.add(cal);
   }

   public void removeCalculator(Calculator cal) {
      this.template.remove(cal);
   }

   public static Factory<Connector> getInstance() {
      if (instance == null) {
         instance = new ConnectorFactory();
      }
      return instance;
   }

   @Override
   public Connector create() {
      return new Connector();
   }

   public Connector create(ConnectorType type, Individual... params) {
      Connector conn = create().setType(type);
      switch (type) {
         case MirrorNextNodeConnector:
            conn.setNext((SketchNode) params[1]);
         case Default:
            conn.setPrevious((SketchNode) params[0]);
            break;
         case MirrorConnector:
            ((MirrorConnector) conn).setMirror((Connector) params[0]);
            break;
      }
      return conn;
   }

   public static void main(String[] args) {
      System.out.println(ConnectorFactory.getInstance().getClass().getSimpleName());
   }
}
