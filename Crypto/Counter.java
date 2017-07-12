package Crypto;
import java.math.BigInteger;
import java.util.ArrayList;

import de.henku.jpaillier.PaillierKeyPair;
import de.henku.jpaillier.KeyPairBuilder;
import de.henku.jpaillier.PaillierPublicKey;

public class Counter {
	private ElectionBoard eb;
	private final PaillierKeyPair keys;
	private final PaillierPublicKey publicKey;
	private BigInteger[] votes;
	public Counter(KeyPairBuilder keygen) {
	   	keys = keygen.generateKeyPair();
	   	publicKey=keys.getPublicKey();
	}
	public int[] decryptSums(ArrayList<BigInteger[]> cText) {
		votes=new BigInteger[cText.get(0).length];
		for (int x=0;x<cText.get(0).length;x++) {
			votes[x]=BigInteger.ONE;
			for (int y=0;y<cText.size();y++) {
				votes[x]=votes[x].multiply(cText.get(y)[x]);
			}
			votes[x]=keys.decrypt(votes[x]);
		}
		int[] intVotes=new int[cText.size()];
		for (int x=0;x<cText.get(0).length;x++) {
			intVotes[x]=votes[x].intValue();
		}
		return intVotes;
	}
	public PaillierPublicKey getPublicKey() {
		return publicKey;
	}
	//Call this to get the results
	public int[] requestVotes(BulletinBoard bb) {
		ArrayList<BigInteger[]> cText=bb.requestVotes();
		return decryptSums(cText);
	}
}
