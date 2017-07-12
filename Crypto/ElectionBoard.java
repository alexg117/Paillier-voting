package Crypto;
import de.henku.jpaillier.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;


public class ElectionBoard {
	private BigInteger[] votes;
	private final PaillierKeyPair keys;
	private final PaillierPublicKey publicKey;
	private RSA RSAkeys;
    private BulletinBoard bb;
    private Counter c;
    ArrayList<String> voters;
    ArrayList<String> candidates;
	
    private ArrayList<String> readIn(String filename) throws IOException {
    	ArrayList<String> dest=new ArrayList<String>();
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	while (br.ready()) {
    		dest.add(br.readLine());
    	}
    	br.close();
		return dest;
    }
	public ElectionBoard(KeyPairBuilder keygen,BulletinBoard bb,String candidateFile,String voterFile,Counter c) {
		this.bb=bb;
		this.c=c;
		try {
			candidates=readIn(candidateFile);
			voters=readIn(voterFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	keys = keygen.generateKeyPair();
	   	publicKey=keys.getPublicKey();
	   	RSAkeys=new RSA();
	}
	public PaillierPublicKey getPublicPaillierKey() {
		return publicKey;
	}
	public RSAPublicKey getPublicRSAKey() {
		return RSAkeys.pk;
	}
	void sendVotes(BigInteger[] votes) {
		this.votes=votes;
	}
	public BigInteger sign(String name,BigInteger b1) {
		if (!(voters.contains(name))) {
			//Invalid request
			return BigInteger.ZERO;
		}
		return RSAkeys.rk.decrypt(b1);
	}
	public PaillierPublicKey newVoter(String name) {
		bb.addVoter(name);
		return this.publicKey;
	}
	public int getNumCandidates() {
		return candidates.size();
	}
	public boolean voterExists(String voterName) {
		if (voters.contains(voterName)) return true;
		return false;
	}
	public String getCandidateList() {
		String candidateList="";
		for (int x=0;x<candidates.size();x++) {
			candidateList+=(x+1)+": "+candidates.get(x)+'\n';
		}
		return candidateList;
	}
	public String getCandidate(int x) {return candidates.get(x);}
	//Call this to compose a string containing the results for the GUI
	public String getVotes() {
		int[] votes=c.requestVotes(bb);
		String result="Results:\n";
		for (int x=0;x<candidates.size();x++) result+=candidates.get(x)+": "+votes[x]+'\n';
		return result;
	}
}
