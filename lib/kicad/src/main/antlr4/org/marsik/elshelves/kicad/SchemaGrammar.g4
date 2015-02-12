parser grammar SchemaGrammar;

options { tokenVocab=SchemaTokens; }

// Top level nonterminal
schema: header libs layer description item* ENDOFSCHEMA EOF;

// Other rules
header: EESCHEMA  SCHEMATIC  (FILE | SPINS)  VERSION  version=Number ;
layer: EELAYER  Number  Number  EELAYER  END ;
libs: lib*;
lib: LIBS name=Name ;

description: DESCR  sheetSize=Name  sheetWidth=Number  sheetHeight=Number description_content* ENDDESCR ;
description_content: (description_key text_content) | (ENCODING Name) | (SHEET Number Number) ;
description_key: COMMENTLINE | TITLE| DATE | REV | COMP ;
item: (no_conn | conn | line | wire | bus | label | hier_label | global_label | wire_bus | bus_bus | text | bitmap | component) ;

no_conn: NO_CONN  TILDE  Number  Number ;
conn: CONN  TILDE  Number  Number ;

wire_coords: xStart=Number  yStart=Number  xEnd=Number  yEnd=Number;
wire: wire_start wire_coords ;
bus: bus_start wire_coords ;
line: line_start wire_coords ;

text_coords: Number  Number  Number  Number ;
text_suffix:  TextFormat  Number ;
text: text_start text_coords text_suffix text_content;
label: label_start text_coords text_suffix text_content;
global_label: global_label_start text_coords PinSymbol text_suffix text_content;
hier_label: hier_label_start text_coords PinSymbol text_suffix text_content;

bitmap: BITMAP_START  BITMAP_POSITION  Number  Number  BITMAP_SCALE  Float  DATA_START  HexByte*  DATA_END  BITMAP_END ;

bus_bus: ENTRY  BUS  BUS   wire_coords ;
wire_bus: ENTRY  WIRE  LINE   wire_coords ;

component: COMPONENT_START component_label component_unit component_position component_field* component_oldposition component_matrix COMPONENT_END;
component_label: COMPONENT_L value=Name designator=Name;
component_unit: COMPONENT_U unit=Number mm=Number timestamp=HexString;
component_position: COMPONENT_P x=Number y=Number;
component_field: COMPONENT_F id=Number value=String orientation=Name x=Number y=Number size=Number Number Name Name;
component_oldposition: COMPONENT_1 x=Number y=Number;
component_matrix: a=Number b=Number c=Number d=Number;

// Helpers
bus_start: WIRE  BUS  LINE  ;
wire_start: WIRE  WIRE  LINE  ;
line_start: WIRE  NOTES  LINE  ;

text_start: TEXT  NOTES ;
label_start: TEXT  LABEL ;
global_label_start: TEXT  GLABEL ;
hier_label_start: TEXT  HLABEL ;

// UnquotedString
text_content: StringBeginning String? StringEnd;

