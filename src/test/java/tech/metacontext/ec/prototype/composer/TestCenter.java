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
package tech.metacontext.ec.prototype.composer;

import java.util.logging.Level;
import java.util.logging.Logger;
import tech.metacontext.ec.prototype.composer.model.Composer;
import static tech.metacontext.ec.prototype.composer.Settings.*;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class TestCenter {

    public static final int PRESET_POPULATION_SIZE = 20;
    public static final int GOAL_CONSERVATORY_SIZE = 0;
    public static final int GOAL_GENERATION = 50;
    public static final double THRESHOLD = 0.5;
    public static final double CONSERVE_SCORE = 0.5;
    
    private static TestCenter instance;
    
    private Studio m;

    public static void main(String[] args) {

        var tc = TestCenter.getInstance();
        tc.getComposer().drawCombinedChart();
    }

    public static synchronized TestCenter getInstance() {

        if (instance == null) {
            instance = new TestCenter();
        }
        return instance;
    }

    private TestCenter() {

        try {
            this.m = new Studio(PRESET_POPULATION_SIZE, GOAL_CONSERVATORY_SIZE,
                    GOAL_GENERATION, THRESHOLD, CONSERVE_SCORE, LogState.DISABLED);
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
//        composer.draw(Composer.RENDERTYPE_AVERAGELINECHART);
//        composer.draw(Composer.RENDERTYPE_SCATTERPLOT);
    }

    public Composer getComposer() {

        return this.m.getComposer();
    }
}
