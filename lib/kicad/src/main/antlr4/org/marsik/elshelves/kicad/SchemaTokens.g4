lexer grammar SchemaTokens;

SPACE: [ \t]+ -> skip;
NL: '\r'? '\n' -> skip;

TILDE: '~';

EESCHEMA: 'EESchema';
SCHEMATIC: 'Schematic';
FILE: 'File';
SPINS: 'Spins';
VERSION: 'Version';

ENDOFSCHEMA: '$EndSCHEMATC';
LIBS: 'LIBS:';
EELAYER: 'EELAYER';
END: 'END';
DESCR: '$Descr' -> pushMode(SheetDescription);

COMPONENT_START: '$Comp';
COMPONENT_END: '$EndComp';

WIRE: 'Wire';
LINE: 'Line';
BUS: 'Bus';

BITMAP_START: '$Bitmap' -> pushMode(Bitmap);
TEXT: 'Text' -> pushMode(UnquotedStrings);

NO_CONN: 'NoConn';
CONN: 'Connection';
ENTRY: 'Entry';

// Hidden lexer elements (no token is generated)
fragment Digit: [0-9];
fragment HexDigit: [0-9A-Fa-f];
fragment NameCharacter: [a-zA-Z0-9$_.-];
fragment EscapeSequence: '\\' [btnfr"'\\];
fragment UnquotedStringCharacter: ~[\\\n\r] | EscapeSequence;
fragment StringCharacter: ~["\\] | EscapeSequence;
fragment FractionSeparator: ',' | '.';

// Lexer elements
Number: '-'? Digit Digit*;
Float: Digit* FractionSeparator Digit Digit*;
Name: NameCharacter NameCharacter*;
StringBeginning: '"' -> pushMode(QuotedStrings);

mode QuotedStrings;

StringEnd: '"' -> popMode;
String: StringCharacter+;

mode UnquotedStrings;

LABEL: 'Label';
GLABEL: 'GLabel';
HLABEL: 'HLabel';
NOTES: 'Notes';

PinSymbol: 'BiDi' | 'Input' | 'Output' | 'Totem';
TextFormat: '~' | 'Italic' | 'Bold';
USNumber: Number -> type(Number);
USWS: SPACE -> skip;
UnquotedStringContent: NL -> mode(UnquotedStringsContent), type(StringBeginning);

mode UnquotedStringsContent;

UnquotedString: (UnquotedStringCharacter UnquotedStringCharacter*) -> type(String);
UnquotedStringEnd: NL -> popMode, type(StringEnd);

mode SheetDescription;

COMMENTLINE: 'Comment' [1-9];
SHEET: 'Sheet';
TITLE: 'Title';
DATE: 'Date';
REV: 'Rev';
COMP: 'Comp';
ENCODING: 'encoding';
ENDDESCR: '$EndDescr' -> popMode;

EENumber: Number -> type(Number);
EEName: Name -> type(Name);
EEValue: '"' -> pushMode(QuotedStrings), type(StringBeginning);
EEWS: (NL | SPACE) -> skip;

mode Bitmap;

BITMAP_END: '$EndBitmap' -> popMode;
DATA_START: 'Data';
DATA_END: 'EndData';
BITMAP_POSITION: 'Pos';
BITMAP_SCALE: 'Scale';

HexByte: HexDigit HexDigit;

BitmapWS: (NL | SPACE) -> skip;
BitmapNumber: Number -> type(Number);
BitmapFloat: Float -> type(Float);


