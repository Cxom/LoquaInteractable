package net.punchtree.loquainteractable._unstable.incomplete.employ.barista;

public enum Phoneme {

	B("b", "bb"),
	D("d", "dd", "ed"),
	F("f", "ph", "ff", "gh", "lf", "ft"),
	G("g", "gg", "gh", "gu", "gue"),
	H("h", "wh"),
	DG("j", "g", "ge", "dge", "di", "gg"),
	K("c", "k", "ck", "ch", "cc", "que", "qu", "q", "lk"),
	L("l", "ll"),
	M("m", "mm", "mb", "mn", "lm"),
	N("n", "nn", "kn", "gn", "pn", "mn"),
	P("p", "pp"),
	R("r", "rr", "wr", "rh"),
	S("s", "se", "ss", "c", "ce", "sc", "ps", "st"),
	T("t", "tt", "ed", "th"),
	V("v", "ve", "f", "ph"),
	W("w", "wh", "u", "o"),
	JY("y", "i", "j"),
	Z("z", "zz", "ze", "s", "se", "x", "ss"),
	
	TH_UNVOICED("th"),
	TH_VOICED("th"),
	NG("ng", "n", "ngue"),
	SH("sh", "ss", "ch", "ti", "ci"),
	CH("ch", "tch", "tsch", "tu", "ti", "te"),
	ZH("ge", "s", "zh", "s", "si", "z"),
	WH("wh"),
	
	SHORT_A("a", "au", "aa", "ah", "ai"),
	SHORT_E("e", "ea", "eh"),
	SHORT_I("i", "y"),
	SHORT_O("o", "a", "au", "aw", "ough", "ah", "oh"),
	SHORT_U("u", "o", "uh", "a"),
	SHORT_OO("oo", "u", "oul", "eu"),
	
	LONG_A("a", "a_e", "ay", "ai", "ey", "ei", "eigh", "aigh"),
	LONG_E("e", "e_e", "ea", "ee", "ey", "ie", "y", "ee_e"),
	LONG_I("i", "i_e", "igh", "y", "ie"),
	LONG_O("o", "o_e", "oa", "ou", "ow"),
	LONG_OO("oo", "u", "u_e",  "ew"),
	
	OW("ow", "ou", "ou_e", "au", "aue"),
	OY("oi", "oy"),
	
	AR("ar", "arr"),
	AIR("air", "ear", "are", "ayr", "aire", "ayre", "eare"),
	EER("irr", "ere", "eer"),
	OR("or", "ore", "oor"),
	UR("ur", "ir", "er", "ear", "or", "ar"),
	
	SCHWA("a", "e", "i", "o", "u", "ah", "aw", "uh", "oo", "oul", "u", "eu");
	
	// TODO diphonemes
//	X(COMBO(K, S), "x"),
//	YU(COMBO(JY, LONG_OO), "u", "u_e", "ew", "yu"),
	
	
	private String[] spellings;
	
	private Phoneme(String... spellings) {
		this.spellings = spellings;
	}
	
//	private Phoneme(String... spellings) {
//		this.spellings = spellings;
//	}
	
	public enum Name {

		JAMES(DG, LONG_A, M, Z),
		ROBERT(R, SHORT_O, B, UR, T),
		JOHN(DG, SHORT_O, N),
		// Michael, William,
		WILLIAM(W, SHORT_I, L, LONG_E, SCHWA, M),
		DAVID(D, LONG_A, V, SHORT_I, D),
		RICHARD(R, SHORT_I, CH, AR, D),
		THOMAS(T, SHORT_O, M, SCHWA, S),
		ANTHONY(SHORT_A, N, TH_UNVOICED, SCHWA, N, LONG_E),
		
		NOAH(N, LONG_O, SHORT_A),
		LIAM(L, LONG_E, SCHWA, M),
		JACOB(DG, LONG_A, K, SCHWA, B),
		MASON(M, LONG_A, S, SCHWA, N),
		ETHAN(LONG_E, TH_UNVOICED, SCHWA, N),
		JADEN(DG, LONG_A, D, SCHWA, N),
		DANIEL(D, SHORT_A, N, JY, SCHWA, L),
		
		SOPHIE(S, LONG_O, F, LONG_E),
		EMMA(SHORT_E, M, SHORT_U),
		OLIVIA(SHORT_U, L, SHORT_I, V, LONG_E, SHORT_U),
		MIA(M, LONG_E, SHORT_U),
		MADISON(M, SHORT_A, D, SHORT_I, S, SCHWA, N),
		
//		ELIZABETH(SCHWA, L, SHORT_I, Z, SCHWA, B, COMBO(SHORT_E, SHORT_I), TH_UNVOICED)
		
		MARY(M, AIR, LONG_E),
		
//		CHARLOTTE(SH, AR, L, SCHWA, T, OPT(SCHWA))
		CHLOE(K, L, LONG_O, LONG_E),
		HARPER(H, AR, P, UR),
		NATALIE(N, SHORT_A, T, SCHWA, L, LONG_E),
		
		JACKSON(DG, SHORT_A, K, S, SCHWA, N),
		LOGAN(L, LONG_O, G, SCHWA, N),
//		ANDREW(SHORT_A, N, COMBO(D, DG), R, LONG_OO),
		LUCAS(L, LONG_OO, K, SCHWA, S),
		DYLAN(D, SHORT_I, L, SCHWA, N),
		HENRY(H, SHORT_E, N, R, LONG_E),
		WYATT(W, LONG_I, SCHWA, T),
		HUNTER(H, SHORT_U, N, T, UR),
		JULIAN(DG, LONG_OO, L, LONG_E, SCHWA, N),
		CAMILA(K, SCHWA, M, SHORT_I, L, SHORT_U),
		ALYSSA(SHORT_U, L, SHORT_I, S, SHORT_U);
		
		public Phoneme[] phonemes;
		
		private Name(Phoneme... phonemes) {
			this.phonemes = phonemes;
		}
		
	}
	
}
