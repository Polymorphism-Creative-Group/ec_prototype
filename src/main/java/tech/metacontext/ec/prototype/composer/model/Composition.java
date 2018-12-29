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
package tech.metacontext.ec.prototype.composer.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tech.metacontext.ec.prototype.abs.Individual;
import tech.metacontext.ec.prototype.abs.Wrapper;
import tech.metacontext.ec.prototype.composer.Main;
import tech.metacontext.ec.prototype.composer.Settings;
import tech.metacontext.ec.prototype.composer.connectors.Connector;
import tech.metacontext.ec.prototype.composer.enums.ComposerAim;
import tech.metacontext.ec.prototype.composer.factory.CompositionFactory;
import tech.metacontext.ec.prototype.composer.factory.ConnectorFactory;
import tech.metacontext.ec.prototype.composer.styles.GoldenSectionClimax;
import tech.metacontext.ec.prototype.composer.styles.Style;
import tech.metacontext.ec.prototype.composer.styles.UnaccompaniedCello;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class Composition extends Individual<CompositionEval> {

    private Logger _logger;

    private static ConnectorFactory connectorFactory;
    private LinkedList<Connector> connectors;
    private LinkedList<SketchNode> rendered;
    private SketchNode seed;

    public Composition(String composer_id, String id, Collection<? extends Style> styles) {

        super(id);
        setup(composer_id, styles);
    }

    public Composition(String composer_id, Collection<? extends Style> styles) {

        setup(composer_id, styles);
    }

    public void setup(String composer_id, Collection<? extends Style> styles) {

        this._logger = Logger.getLogger(composer_id);
        this.rendered = new LinkedList<>();
        this.connectors = new LinkedList<>();
        this.setEval(new CompositionEval(styles));
        connectorFactory = ConnectorFactory.getInstance();
    }

    public Composition elongation(Predicate<SketchNode> styleChecker) {

        this.addConnector(connectorFactory.newConnector(styleChecker));
        return this;
    }

    public void addConnector(Connector connector) {

        this.connectors.add(connector);
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main(100, 1, Settings.TEST);
        Composer composer = main.getComposer();
        Composition p0, p1;
        do {
            p0 = composer.randomSelect(Composer.SELECT_ONLY_COMPLETED);
            p1 = composer.randomSelect(Composer.SELECT_ONLY_COMPLETED);
        } while (Objects.equals(p0, p1));
        Composition child, dupe;
        int counter = 0;
        CompositionFactory cf = CompositionFactory.getInstance(composer.getId());
        do {
            System.out.print(".");
            child = composer.crossover(p0, p1);
            child.getRenderedChecked();
            dupe = cf.forArchiving(child);
            if (counter++ % 50 == 0) {
                System.out.println();
            }
        } while (!dupe.ifReRenderRequired());
    }

    public List<SketchNode> render() {

        rendered.clear();
        Wrapper<SketchNode> previous = new Wrapper<>(seed);
        rendered.add(seed);
        rendered.addAll(this.getConnectors().stream()
                .map(conn -> {
                    /*
                    1. conn.setPrevious(previous.get())
                    2. previous.set(conn.transform())
                    3. return conn.getNext()
                     */
                    conn.setPrevious(previous.get());
                    return previous.set(conn.transform());
                })
                .collect(Collectors.toList()));
//        System.out.println(this);
        return rendered;
    }

    public List<SketchNode> getRenderedChecked() {

        if (this.ifReRenderRequired()) {
            this.render();
            updateEval();
        }
        return this.rendered;
    }

    public boolean ifReRenderRequired() {

        if (this.rendered.isEmpty()) {
            _logger.log(Level.INFO,
                    "Not rendered yet, rendering required for Composition {0}.",
                    this.getId_prefix());
            return true;
        }
        if (!Objects.equals(this.connectors.getFirst().getPrevious(), this.seed)) {
            _logger.log(Level.INFO,
                    "Seed mismatched, rerendering required for Composition {0}.",
                    this.getId_prefix());
            return true;
        }
        if (this.rendered.size() != this.getSize()) {
            _logger.log(Level.INFO,
                    "Size mismatched: {0} to {1}, rerendering required for Composition {2}.", new Object[]{
                        this.rendered.size(),
                        this.getSize(),
                        this.getId_prefix()});
            return true;
        }
        if (this.connectors.stream().anyMatch(conn
                -> Objects.isNull(conn.getPrevious()) || Objects.isNull(conn.getNext()))) {
            _logger.log(Level.INFO,
                    "Connector without connected SketchNode found, rerendering required for Composition {0}.",
                    this.getId_prefix());
            return true;
        }
        if (IntStream.range(1, this.getSize()).anyMatch(i
                -> !Objects.equals(this.connectors.get(i - 1).getNext(), this.rendered.get(i)))) {
            _logger.log(Level.INFO,
                    "Mismatched SketchNodes, rerendering required for Composition {0}.",
                    this.getId_prefix());
            return true;
        }
        _logger.log(Level.FINE,
                "Rendered list remained consistant, no rerendering required for {0}.",
                this.getId_prefix());
        return false;
    }

    public void updateEval() {

        this.getEval().getStyles().stream()
                .forEach(this::updateScore);
    }

    public void updateScore(Style style) {

        this.getEval().getScores()
                .put(style, style.rateComposition(this));
    }

    public Double getScore(Style style) {

        return this.getEval().getScores().get(style);
    }

    public int getSize() {

        return this.getConnectors().size() + 1;
    }

    public Path persistent() {

        Path destination = new File("src/main/resources/composition", this.getId() + ".txt").toPath();
        try (BufferedWriter out = Files.newBufferedWriter(
                destination, StandardCharsets.UTF_8)) {
            out.write(this.toString());
            out.flush();
        } catch (IOException ex) {
            _logger.log(Level.SEVERE, "Failed to persist {0}. {1}", new Object[]{
                this.getId_prefix(), ex.getMessage()});
        }
        _logger.log(Level.INFO, "{0} has been persisted to {1}", new Object[]{
            this.getId_prefix(),
            destination.getFileName()});
        return destination;
    }

    public void resetSeed(SketchNode seed) {

        this.seed = seed;
        this.connectors.getFirst().setPrevious(seed);
        if (!this.rendered.contains(seed)) {
            if (this.rendered.size() < this.getSize()) {
                this.rendered.add(0, seed);
            } else {
                _logger.warning("Rendered size mismatch of missing seed, rerendering.");
                this.render();
            }
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Composition other = (Composition) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        String result
                = String.format("%s(size = %d, Composer = [%s])\n",
                        super.toString(), this.getSize(), _logger.getName())
                + String.format("%s\n", Composer.output(this))
                + String.format("Seed: %s\n", this.getSeed())
                + this.getConnectors().stream()
                        .peek(c -> {
                            if (Objects.isNull(c.getPrevious())) {
                                _logger.log(Level.WARNING,
                                        "Null SketchNode found in {0}.getPrevious().", c.getId_prefix());
                            }
                            if (Objects.isNull(c.getNext())) {
                                _logger.log(Level.WARNING,
                                        "Null SketchNode found in {0}.getNext().", c.getId_prefix());
                            }
                        })
                        .filter(c -> Objects.nonNull(c.getNext()))
                        .map(Connector::toStringNext)
                        .collect(Collectors.joining("\n"));
        return result;
    }

    /*
     * Default setters and getters
     */
    public LinkedList<Connector> getConnectors() {
        return connectors;
    }

    public SketchNode getSeed() {
        return seed;
    }

    public void setSeed(SketchNode seed) {
        this.seed = seed;
    }

    public List<SketchNode> getRendered() {
        return this.rendered;
    }

}