grammar Schema;

LibraryCharacter: [a-zA-Z0-9$_.];
Digit: [0-9];
SheetClass: [A-E];
SheetSize: [0-4];
EscapeSequence: '\\' [btnfr"'\\];
StringCharacter: ~["\\] | EscapeSequence;

WS: (' ' | '\t')+;
NL:  '\r'? '\n';

HEADER: 'EESchema Schematic Spins Version 1';
LIBS: 'LIBS:';

schema: HEADER libs layer description item* EOF;

libs: LIBS library_name (',' library_name)*;
library_name: LibraryCharacter LibraryCharacter*;

EELAYER: 'EELAYER';
END: 'END';
layer: EELAYER number number EELAYER END;

number: Digit Digit*;

sheet_size: SheetClass SheetSize;

DESCR: '$Descr';
ENDDESCR: '$EndDescr';

string: '"' StringCharacter* '"';

description: DESCR sheet_size number number description_content* ENDDESCR;
description_content: string | number | library_name;

item: component;

COMPONENT_START: '$Comp';
COMPONENT_END: '$EndComp';
component: COMPONENT_START COMPONENT_END;