parser grammar SchemaParser;

options { tokenVocab=SchemaLexer; }

// Top level nonterminal
schema: header libs layer description item* ENDOFSCHEMA EOF;

// Other rules
header: EESCHEMA  SCHEMATIC  (FILE | SPINS)  VERSION  version=number_token ;
layer: EELAYER  number_token  number_token  EELAYER  END ;
libs: lib*;
lib: LIBS name=name_token ;

description: DESCR  sheetSize=name_token  sheetWidth=number_token  sheetHeight=number_token description_content* ENDDESCR ;
description_content: (description_key text_content) | (ENCODING name_token) | (SHEET number_token number_token) ;
description_key: COMMENTLINE | TITLE| DATE | REV | COMP ;
item: (no_conn | conn | line | wire | bus | label | hier_label | global_label | wire_bus | bus_bus | text | bitmap | component) ;

no_conn: NO_CONN  TILDE  number_token  number_token ;
conn: CONN  TILDE  number_token  number_token ;

wire_coords: xStart=number_token  yStart=number_token  xEnd=number_token  yEnd=number_token;
wire: wire_start wire_coords ;
bus: bus_start wire_coords ;
line: line_start wire_coords ;

text_coords: number_token  number_token  number_token  number_token ;
text_suffix:  TextFormat  number_token ;
text: text_start text_coords text_suffix text_content;
label: label_start text_coords text_suffix text_content;
global_label: global_label_start text_coords PinSymbol text_suffix text_content;
hier_label: hier_label_start text_coords PinSymbol text_suffix text_content;

bitmap: BITMAP_START  BITMAP_POSITION  number_token  number_token  BITMAP_SCALE  Float  DATA_START  HexByte*  DATA_END  BITMAP_END ;

bus_bus: ENTRY  BUS  BUS   wire_coords ;
wire_bus: ENTRY  WIRE  LINE   wire_coords ;

component: COMPONENT_START component_label component_unit component_position component_field* component_oldposition component_matrix COMPONENT_END;
component_label: COMPONENT_L value=name_token designator=name_token;
component_unit: COMPONENT_U unit=number_token mm=number_token timestamp=HexString;
component_position: COMPONENT_P x=number_token y=number_token;
component_field: COMPONENT_F id=number_token value=text_content orientation=name_token x=number_token y=number_token size=number_token number_token name_token name_token name=text_content?;
component_oldposition: unit=number_token x=number_token y=number_token;
component_matrix: a=number_token b=number_token c=number_token d=number_token;

// Helpers
bus_start: WIRE  BUS  LINE  ;
wire_start: WIRE  WIRE  LINE  ;
line_start: WIRE  NOTES  LINE  ;

text_start: TEXT  NOTES ;
label_start: TEXT  LABEL ;
global_label_start: TEXT  GLABEL ;
hier_label_start: TEXT  HLABEL ;

name_token: Name | HexString | number_token | Float | COMPONENT_L | COMPONENT_U | COMPONENT_P | COMPONENT_F;
number_token: Number;

// UnquotedString
text_content: StringBeginning content=String? StringEnd;

