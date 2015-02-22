package org.marsik.elshelves.kicad;

import gnu.trove.map.hash.THashMap;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SchemaComponents extends Schema {

    static public class Component {
        public String id;
        public String type;
        public String value;
        public Integer unit;
        public String footprint;

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
        private Map<String, List<Component>> components = new THashMap<>();

        @Override
        public String visitComponent(@NotNull SchemaParser.ComponentContext ctx) {
            String designation = ctx.component_label().designator.getText();

            // Ignore power symbols
            if (designation.startsWith("#")) {
                return null;
            }


            String type = ctx.component_label().value.getText();

            String baseUnit = ctx.component_unit() != null ? ctx.component_unit().unit.getText() : "1";
            Integer unit = Integer.valueOf(baseUnit);

            Component c = new Component();
            c.id = designation;
            c.type = type;
            c.unit = unit;

            for (SchemaParser.Component_fieldContext fieldContext: ctx.component_field()) {
                // Field 1 is the value
                if (fieldContext.id.getText().equals("1")) {
                    for (SchemaParser.Text_contentContext t: fieldContext.text_content()) {
                        if (t.content != null && t.content.getText() != null) {
                            if (!t.content.getText().equals(type)) {
                                c.value = t.content.getText();
                            }
                        }
                    }
                }

                // Field 2 is the footprint
                if (fieldContext.id.getText().equals("2")) {
                    for (SchemaParser.Text_contentContext t: fieldContext.text_content()) {
                        if (t.content != null
                                && t.content.getText() != null
                                && !t.content.getText().isEmpty()) {
                            c.footprint = t.content.getText();
                        }
                    }
                }
            }

            if (!components.containsKey(designation)) {
                components.put(designation, new ArrayList<Component>());
            }
            components.get(designation).add(c);

            return null;
        }

        public Map<String, List<Component>> getComponents() {
            return components;
        }
    }

    public Map<String, List<Component>> fetchComponents(InputStream schema) throws IOException {
        SchemaParser p = getSchemaParser(schema);
        ParseTree tree = p.schema();

        ComponentVisitor visitor = new ComponentVisitor();
        visitor.visit(tree);

        return visitor.getComponents();
    }
}
