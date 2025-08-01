package net.punchtree.loquainteractable._unstable.incomplete.employ.barista;

public enum Name {

	JAMES(Phoneme.DG, Phoneme.LONG_A, Phoneme.M, Phoneme.Z);
//	ROBERT(Phoneme.R, )
	
	private Phoneme[] phonemes;
	
	private Name(Phoneme... phonemes) {
		this.phonemes = phonemes;
	}
	
}
