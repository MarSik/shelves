grammar Schema;

// Lexer Constants
WS: (' ' | '\t')+ -> skip;
NL:  '\r'? '\n' -> skip;

HEADER: 'EESchema Schematic ' ('File' | 'Spins') ' Version ' ('1' | '2');
ENDOFSCHEMA: '$EndSCHEMATC';
LIBS: 'LIBS:';
EELAYER: 'EELAYER';
END: 'END';
DESCR: '$Descr';
ENDDESCR: '$EndDescr';
COMPONENT_START: '$Comp';
COMPONENT_END: '$EndComp';

// Hidden lexer elements (no token is generated)
fragment NameCharacter: [a-zA-Z0-9$_.-];
fragment Digit: [0-9];
fragment SheetClass: [A-E];
fragment SheetSize: [0-4];
fragment EscapeSequence: '\\' [btnfr"'\\];
fragment StringCharacter: ~["\\] | EscapeSequence;

// Lexer elements
String: '"' StringCharacter* '"';
Number: Digit Digit*;
Name: NameCharacter NameCharacter*;

// Top level nonterminal
schema: HEADER libs layer description item* ENDOFSCHEMA;

// Other rules
layer: EELAYER Number Number EELAYER END;
libs: lib*;
lib: LIBS name=Name;

description: DESCR sheetSize=Name sheetWidth=Number sheetHeight=Number description_content* ENDDESCR;
description_content: String | Number | Name;
item: component;
component: COMPONENT_START COMPONENT_END;
