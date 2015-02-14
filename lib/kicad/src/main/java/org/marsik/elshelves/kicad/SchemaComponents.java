package org.marsik.elshelves.kicad;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SchemaComponents extends Schema {

    static public class Component {
        public String id;
        public String type;
        public String value;

        @Override
        public String toString() {
            return "Component{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    static private class ComponentVisitor extends SchemaParserBaseVisitor<String> {
        private List<Component> components = new ArrayList<>();

        @Override
        public String visitComponent(@NotNull SchemaParser.ComponentContext ctx) {
            String designation = ctx.component_label().designator.getText();

            // Ignore power symbols
            if (designation.startsWith("#")) {
                return null;
            }


            String type = ctx.component_label().value.getText();

            String baseUnit = ctx.component_unit().unit.getText();
            String unit = String.valueOf((char)(Integer.valueOf(baseUnit) + (int)'A' - 1));

            Component c = new Component();
            c.id = designation + unit;
            c.type = type;

            for (SchemaParser.Component_fieldContext fieldContext: ctx.component_field()) {
                String value = visit(fieldContext);

                if (value == null) {
                    continue;
                }

                if (!value.equals(type)) {
                    c.value = value;
                }

                break;
            }

            components.add(c);

            return null;
        }

        @Override
        public String visitComponent_field(@NotNull SchemaParser.Component_fieldContext ctx) {
            // Field 1 is the value
            if (ctx.id.getText().equals("1")) {
                for (SchemaParser.Text_contentContext t: ctx.text_content()) {
                    if (t.content != null && t.content.getText() != null) {
                        return t.content.getText();
                    }
                }
            }

            return super.visitComponent_field(ctx);
        }

        public List<Component> getComponents() {
            return components;
        }
    }

    public Collection<Component> fetchComponents(InputStream schema) throws IOException {
        SchemaParser p = getSchemaParser(schema);
        ParseTree tree = p.schema();

        ComponentVisitor visitor = new ComponentVisitor();
        visitor.visit(tree);

        return visitor.getComponents();
    }
}
