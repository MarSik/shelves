package org.marsik.elshelves.kicad;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.InputStream;

public class Schema {
    SchemaParser getSchemaParser(InputStream input) throws IOException {
        SchemaLexer l = new SchemaLexer(new ANTLRInputStream(input));
        TokenStream tokenStream = new CommonTokenStream(l);
        SchemaParser p = new SchemaParser(tokenStream);
        return p;
    }
}
