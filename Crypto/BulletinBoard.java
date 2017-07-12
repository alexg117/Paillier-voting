package Crypto;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import de.henku.jpaillier.KeyPairBuilder;
import de.henku.jpaillier.PaillierPublicKey;

public class BulletinBoard {
	private ArrayList<String> voters;
	ArrayList<String> candidates;
	private ArrayList<BigInteger[]> voteRecord;
	private int numCandidates;
	private PaillierPublicKey key;
	private RSAPublicKey pk;
	private int[] trials;
	private BigInteger lastBallot;
	private String lastVoter;
	private int lastCandidate;
	private BigInteger lastu;

    private ArrayList<String> readIn(String filename) throws IOException {
    	ArrayList<String> dest=new ArrayList<String>();
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	while (br.ready()) {
    		dest.add(br.readLine());
    	}
    	br.close();
		return dest;
    }
	public BulletinBoard(PaillierPublicKey publicKey,String candidateFile,String voterFile) {
		try {
			voters=readIn(voterFile);
			candidates=readIn(candidateFile);
			numCandidates=candidates.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		voteRecord=new ArrayList<BigInteger[]>();
		this.key=publicKey;
		for (int a=0;a<voters.size();a++) {
			BigInteger[] bi=new BigInteger[numCandidates];
			for (int x=0;x<numCandidates;x++) {
				//Initially assume new voter does not vote for any candidate
				bi[x]=key.encrypt(BigInteger.ZERO)[0];
			}
			voteRecord.add(bi);
		}
	}

	public void addVoter(String name) {
	}

	public void setRSAKey(RSAPublicKey pk) {
		this.pk=pk;
	}

	public int[] authenticateBallot(BigInteger ballot, BigInteger signed, String voterName, int candidateNum,BigInteger u) {
		BigInteger decrypted=pk.encrypt(signed);
		if (decrypted.compareTo(ballot)!=0) {
			JOptionPane.showMessageDialog(null,"Bulletin Board here. Your signed ballot is invalid. Try again?");
		}
		else {
			Random r=new Random();
			//Signed ballot authenticated, send tests
			int numTrials=5;
			trials=new int[numTrials];
			for (int x=0;x<numTrials;x++) {
				trials[x]=r.nextInt(1000);
			}
			lastBallot=ballot;
			lastVoter=voterName;
			lastCandidate=candidateNum;
			lastu=u;
				
			return trials;
		}
		return null;
	}
	public void solveTrials(BigInteger[] solutions) {
		for (int x=0;x<trials.length;x++) {
			//check g^v *c^e *w^N ?= u
			BigInteger N2=key.getN().pow(2);
			BigInteger u=BigInteger.ONE;
			u=solutions[2*x+1].modPow(key.getN(),N2);
			u=u.multiply(lastBallot.pow(trials[x]));
			u=u.multiply(key.getG().modPow(solutions[2*x],N2));
			u=u.mod(N2);
			if (u.compareTo(lastu)!=0) {
				JOptionPane.showMessageDialog(null,"Bulletin Board here. You failed to complete the ZKP.\nThis message may be shown once per candidate.\nSorry for the inconvenience, but please don't try to hack the election.");
				return;

				//System.out.println("Trial failed" );
			}
			else {
				//System.out.println("Trial passed" );
			}
		}
		int votr=voters.indexOf(lastVoter);
		voteRecord.get(votr)[lastCandidate]=lastBallot;
	}
	//Composes a string containing a crude representation of the bulletin board.
	public String getBulletinBoard() {
		StringBuffer bb=new StringBuffer("This is the board. Only the int reduction of each vote is shown.\n");
		int maxNameLength=11;
		int maxVoterNameLength=0;
		for (int x=0;x<voters.size();x++) {
			if (voters.get(x).length()>maxVoterNameLength) maxVoterNameLength=voters.get(x).length();
		}
		for (int y=0;y<candidates.size();y++) {
			if (candidates.get(y).length()>maxNameLength) maxNameLength=candidates.get(y).length();
		}

		for (int z=0;z<maxVoterNameLength+1;z++) {
			bb.append(" ");
		}
		for (int z=0;z<maxVoterNameLength+1;z++) {
			bb.append(" ");
		}
		for (int y=0;y<candidates.size();y++) {
			bb.append(candidates.get(y));
			for (int z=0;z<(maxNameLength+1-candidates.get(y).length());z++) {
				bb.append(" ");
				
			}
			for (int z=0;z<(maxNameLength+1-candidates.get(y).length());z++) {
				bb.append(" ");
				
			}
		}
		bb.append("\n");
		for (int x=0;x<voters.size();x++) {
			bb.append(voters.get(x));
			for (int z=0;z<maxVoterNameLength+1-voters.get(x).length();z++) {
				bb.append(" ");
			}
			for (int z=0;z<maxVoterNameLength+1-voters.get(x).length();z++) {
				bb.append(" ");
			}
			for (int y=0;y<candidates.size();y++) {
				String temp=""+voteRecord.get(x)[y].intValue();
				bb.append(temp);
				for (int z=0;z<(maxNameLength+1-temp.length());z++) {
					bb.append(" ");
				}
			}
			bb.append("\n");
		}
		return bb.toString();
	}
	public ArrayList<BigInteger[]> requestVotes() {
		return voteRecord;
	}
}
