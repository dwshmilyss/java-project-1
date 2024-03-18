// Generated from /Users/edz/sourceCode/java-project-1/java-base/src/main/resources/Dsl.g4 by ANTLR 4.13.1
        //一种action,定义生成的词法语法解析文件的头，当使用java的时候，生成的类需要包名，可以在这里统一定义
 package antlr;
 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DslLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, AS=3, LOAD=4, SELECT=5, STRING=6, IDENTIFIER=7, BACKQUOTED_IDENTIFIER=8, 
		SIMPLE_COMMENT=9, BRACKETED_EMPTY_COMMENT=10, BRACKETED_COMMENT=11, WS=12, 
		UNRECOGNIZED=13;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "AS", "LOAD", "SELECT", "DIGIT", "LETTER", "STRING", 
			"IDENTIFIER", "BACKQUOTED_IDENTIFIER", "SIMPLE_COMMENT", "BRACKETED_EMPTY_COMMENT", 
			"BRACKETED_COMMENT", "WS", "UNRECOGNIZED"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'.'", null, null, null, null, null, null, null, "'/**/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "AS", "LOAD", "SELECT", "STRING", "IDENTIFIER", "BACKQUOTED_IDENTIFIER", 
			"SIMPLE_COMMENT", "BRACKETED_EMPTY_COMMENT", "BRACKETED_COMMENT", "WS", 
			"UNRECOGNIZED"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public DslLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Dsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\r\u008e\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0005\u0007;\b\u0007\n\u0007\f\u0007>\t"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007E\b\u0007\n\u0007\f\u0007H\t\u0007\u0001\u0007\u0003\u0007K\b\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0004\bP\b\b\u000b\b\f\bQ\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0005\tX\b\t\n\t\f\t[\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0005\nc\b\n\n\n\f\nf\t\n\u0001\n\u0003\ni\b\n\u0001\n\u0003"+
		"\nl\b\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0005\f|\b\f\n\f\f\f\u007f\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0004\r\u0087\b\r\u000b\r\f\r\u0088\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001}\u0000\u000f\u0001\u0001\u0003\u0002\u0005\u0003\u0007"+
		"\u0004\t\u0005\u000b\u0000\r\u0000\u000f\u0006\u0011\u0007\u0013\b\u0015"+
		"\t\u0017\n\u0019\u000b\u001b\f\u001d\r\u0001\u0000\u0010\u0002\u0000A"+
		"Aaa\u0002\u0000SSss\u0002\u0000LLll\u0002\u0000OOoo\u0002\u0000DDdd\u0002"+
		"\u0000EEee\u0002\u0000CCcc\u0002\u0000TTtt\u0001\u000009\u0002\u0000A"+
		"Zaz\u0002\u0000\'\'\\\\\u0002\u0000\"\"\\\\\u0001\u0000``\u0002\u0000"+
		"\n\n\r\r\u0001\u0000++\u0003\u0000\t\n\r\r  \u009a\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000"+
		"\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000"+
		"\u0000\u0001\u001f\u0001\u0000\u0000\u0000\u0003!\u0001\u0000\u0000\u0000"+
		"\u0005#\u0001\u0000\u0000\u0000\u0007&\u0001\u0000\u0000\u0000\t+\u0001"+
		"\u0000\u0000\u0000\u000b2\u0001\u0000\u0000\u0000\r4\u0001\u0000\u0000"+
		"\u0000\u000fJ\u0001\u0000\u0000\u0000\u0011O\u0001\u0000\u0000\u0000\u0013"+
		"S\u0001\u0000\u0000\u0000\u0015^\u0001\u0000\u0000\u0000\u0017o\u0001"+
		"\u0000\u0000\u0000\u0019v\u0001\u0000\u0000\u0000\u001b\u0086\u0001\u0000"+
		"\u0000\u0000\u001d\u008c\u0001\u0000\u0000\u0000\u001f \u0005;\u0000\u0000"+
		" \u0002\u0001\u0000\u0000\u0000!\"\u0005.\u0000\u0000\"\u0004\u0001\u0000"+
		"\u0000\u0000#$\u0007\u0000\u0000\u0000$%\u0007\u0001\u0000\u0000%\u0006"+
		"\u0001\u0000\u0000\u0000&\'\u0007\u0002\u0000\u0000\'(\u0007\u0003\u0000"+
		"\u0000()\u0007\u0000\u0000\u0000)*\u0007\u0004\u0000\u0000*\b\u0001\u0000"+
		"\u0000\u0000+,\u0007\u0001\u0000\u0000,-\u0007\u0005\u0000\u0000-.\u0007"+
		"\u0002\u0000\u0000./\u0007\u0005\u0000\u0000/0\u0007\u0006\u0000\u0000"+
		"01\u0007\u0007\u0000\u00001\n\u0001\u0000\u0000\u000023\u0007\b\u0000"+
		"\u00003\f\u0001\u0000\u0000\u000045\u0007\t\u0000\u00005\u000e\u0001\u0000"+
		"\u0000\u00006<\u0005\'\u0000\u00007;\b\n\u0000\u000089\u0005\\\u0000\u0000"+
		"9;\t\u0000\u0000\u0000:7\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000"+
		";>\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000"+
		"\u0000=?\u0001\u0000\u0000\u0000><\u0001\u0000\u0000\u0000?K\u0005\'\u0000"+
		"\u0000@F\u0005\"\u0000\u0000AE\b\u000b\u0000\u0000BC\u0005\\\u0000\u0000"+
		"CE\t\u0000\u0000\u0000DA\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000"+
		"EH\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000"+
		"\u0000GI\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IK\u0005\"\u0000"+
		"\u0000J6\u0001\u0000\u0000\u0000J@\u0001\u0000\u0000\u0000K\u0010\u0001"+
		"\u0000\u0000\u0000LP\u0003\r\u0006\u0000MP\u0003\u000b\u0005\u0000NP\u0005"+
		"_\u0000\u0000OL\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000ON\u0001"+
		"\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000\u0000"+
		"QR\u0001\u0000\u0000\u0000R\u0012\u0001\u0000\u0000\u0000SY\u0005`\u0000"+
		"\u0000TX\b\f\u0000\u0000UV\u0005`\u0000\u0000VX\u0005`\u0000\u0000WT\u0001"+
		"\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000"+
		"YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\\\u0001\u0000\u0000"+
		"\u0000[Y\u0001\u0000\u0000\u0000\\]\u0005`\u0000\u0000]\u0014\u0001\u0000"+
		"\u0000\u0000^_\u0005-\u0000\u0000_`\u0005-\u0000\u0000`d\u0001\u0000\u0000"+
		"\u0000ac\b\r\u0000\u0000ba\u0001\u0000\u0000\u0000cf\u0001\u0000\u0000"+
		"\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000eh\u0001\u0000"+
		"\u0000\u0000fd\u0001\u0000\u0000\u0000gi\u0005\r\u0000\u0000hg\u0001\u0000"+
		"\u0000\u0000hi\u0001\u0000\u0000\u0000ik\u0001\u0000\u0000\u0000jl\u0005"+
		"\n\u0000\u0000kj\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lm\u0001"+
		"\u0000\u0000\u0000mn\u0006\n\u0000\u0000n\u0016\u0001\u0000\u0000\u0000"+
		"op\u0005/\u0000\u0000pq\u0005*\u0000\u0000qr\u0005*\u0000\u0000rs\u0005"+
		"/\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0006\u000b\u0000\u0000u\u0018"+
		"\u0001\u0000\u0000\u0000vw\u0005/\u0000\u0000wx\u0005*\u0000\u0000xy\u0001"+
		"\u0000\u0000\u0000y}\b\u000e\u0000\u0000z|\t\u0000\u0000\u0000{z\u0001"+
		"\u0000\u0000\u0000|\u007f\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000"+
		"\u0000}{\u0001\u0000\u0000\u0000~\u0080\u0001\u0000\u0000\u0000\u007f"+
		"}\u0001\u0000\u0000\u0000\u0080\u0081\u0005*\u0000\u0000\u0081\u0082\u0005"+
		"/\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0006\f"+
		"\u0000\u0000\u0084\u001a\u0001\u0000\u0000\u0000\u0085\u0087\u0007\u000f"+
		"\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000"+
		"\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008b\u0006\r\u0000"+
		"\u0000\u008b\u001c\u0001\u0000\u0000\u0000\u008c\u008d\t\u0000\u0000\u0000"+
		"\u008d\u001e\u0001\u0000\u0000\u0000\u000f\u0000:<DFJOQWYdhk}\u0088\u0001"+
		"\u0000\u0001\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}