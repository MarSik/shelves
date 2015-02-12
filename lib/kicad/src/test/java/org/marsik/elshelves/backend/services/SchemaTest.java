package org.marsik.elshelves.backend.services;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import org.marsik.elshelves.kicad.SchemaGrammar;
import org.marsik.elshelves.kicad.SchemaTokens;

import java.io.IOException;


import static org.junit.Assert.*;

public class SchemaTest {

    private SchemaGrammar getSchemaGrammarParser(String resource) throws IOException {
        SchemaTokens l = new SchemaTokens(new ANTLRInputStream(getClass().getResourceAsStream(resource)));
        TokenStream tokenStream = new CommonTokenStream(l);
        SchemaGrammar p = new SchemaGrammar(tokenStream);
        p.removeErrorListeners();
        p.addErrorListener( new DiagnosticErrorListener() );
        return p;
    }

    @Test
    public void testEmpty() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/empty.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testWire() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/wire.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBus() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testLine() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/line.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testText() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/text.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testLabel() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testGlobalLabel() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/global_label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testHierarchicalLabel() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/hierarchical_label.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBitmap() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/bitmap.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testWireToBus() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/wire_to_bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testBusToBus() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/bus_to_bus.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testComponent() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/ic.sch");
        assertNotNull(p.schema());
    }

    @Test
    public void testTwoComponents() throws IOException {
        SchemaGrammar p = getSchemaGrammarParser("/ic_two_units.sch");
        assertNotNull(p.schema());
    }
}
