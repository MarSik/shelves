package org.marsik.elshelves.kicad;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import org.marsik.elshelves.kicad.SchemaLexer;
import org.marsik.elshelves.kicad.SchemaParser;

import java.io.IOException;
import java.util.BitSet;


import static org.junit.Assert.*;

public class SchemaTest {

    private SchemaParser getSchemaGrammarParser(String resource) throws IOException {
        SchemaLexer l = new SchemaLexer(new ANTLRInputStream(getClass().getResourceAsStream(resource)));
        TokenStream tokenStream = new CommonTokenStream(l);
        SchemaParser p = new SchemaParser(tokenStream);
        p.setErrorHandler(new BailErrorStrategy());
        return p;
    }

    @Test
    public void testEmpty() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/empty.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testMeta() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/meta.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testWire() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/wire.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBus() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testLine() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/line.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testText() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/text.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testLabel() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testGlobalLabel() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/global_label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testHierarchicalLabel() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/hierarchical_label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBitmap() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/bitmap.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testWireToBus() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/wire_to_bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBusToBus() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/bus_to_bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testComponent() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/ic.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testTwoComponents() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/ic_two_units.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testComplicatedBoard() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/psu.sch");
        assertNotNull(p.schema());
    }

    /**
     * This serves as a check of false negatives
     * @throws IOException
     */
    @Test(expected = ParseCancellationException.class)
    public void testFailingSchema() throws IOException {
        SchemaParser p = getSchemaGrammarParser("/broken.sch");
        assertNotNull(p.schema());
    }
}
