package org.marsik.elshelves.backend.services;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.IOException;
import org.marsik.elshelves.kicad.SchemaLexer;
import org.marsik.elshelves.kicad.SchemaParser;

import static org.junit.Assert.*;

public class SchemaTest {
    @Test
    public void testLoading() throws IOException {
        SchemaLexer l = new SchemaLexer(new ANTLRInputStream(getClass().getResourceAsStream("/empty.sch")));
        SchemaParser p = new SchemaParser(new CommonTokenStream(l));
        p.schema();
    }
}
