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
package tech.metacontext.ec.prototype.composer.nodes;

import java.util.ArrayList;
import java.util.List;
import tech.metacontext.ec.prototype.composer.abs.Factory;
import tech.metacontext.ec.prototype.composer.materials.MusicMaterial;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class SketchNodeFactory extends Factory<SketchNode> {

   private static SketchNodeFactory instance;
   List<? extends MusicMaterial> template;

   private SketchNodeFactory() {
      template = new ArrayList<>();
   }

   public static SketchNodeFactory getInstance() {
      if (instance == null) {
         instance = new SketchNodeFactory();
      }
      return instance;
   }

   @Override
   public SketchNode create() {
      SketchNode node = new SketchNode();
      template.forEach(node::addMaterial);
      return node;
   }
}
