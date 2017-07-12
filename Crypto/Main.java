package Crypto;


import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.swing.JOptionPane;

import de.henku.jpaillier.*;
public class Main {
	public static void main(String[] args) {
		String candidates="candidates.txt";
		String voters="voters.txt";
		SecureRandom rng = new SecureRandom();
		Random rand=new Random();
		KeyPairBuilder keygen = new KeyPairBuilder();
		//First make the election board, bulletin board, and CA
		Counter ca=new Counter(keygen);
		//Everything is ready, start asking the user.
		BulletinBoard bb=new BulletinBoard(ca.getPublicKey(),candidates,voters);
		ElectionBoard eb=new ElectionBoard(keygen,bb,candidates,voters,ca);
		int numCandidates=eb.getNumCandidates();
		bb.setRSAKey(eb.getPublicRSAKey());
		int electioneering=rand.nextInt(numCandidates);
		while (true) {
			String input=null;
			while (input==null||input.equals("")) 
				input=JOptionPane.showInputDialog("Enter v to vote, b to see the bulletin board, or r to see the results.\nEntering r will show the results and end the election.");
			if (input.charAt(0)=='v') {
				input=JOptionPane.showInputDialog("Hello, voter! Please enter your name.");
				while (input.length()==0) {
					input=JOptionPane.showInputDialog("Your NAME. I need your NAME.");
				}
				String voterName=input;
				if (eb.voterExists(voterName)) {
					String inputMessage="Thank you for voting, "+input+"! Which candidate would you like to vote for? \nTrue patriots would vote for "+eb.getCandidate(electioneering)+", but you can vote for any of the "+numCandidates+" candidates!\nTo vote, enter a number from 1 to "+numCandidates+".";
					inputMessage+='\n'+eb.getCandidateList();
					input=JOptionPane.showInputDialog(inputMessage);
					int candidate=Integer.parseInt(input);
					candidate--;
					for (int x=0;x<numCandidates;x++) {
						int vote=0;
						if (candidate==x) vote=1;
						byte[] me=new byte[20];
						rand.nextBytes(me);
						BigInteger rndm=(new BigInteger(me)).abs();
						//We must first get the ballot blind signed
						//This is the ballot itself
						BigInteger[] results=ca.getPublicKey().encrypt(BigInteger.valueOf(vote));
						BigInteger ballot=results[0];
						//This value was used to encrypt the ballot and will be necessary for the ZKP later
						BigInteger x0=results[1];
						BigInteger g=ca.getPublicKey().getG();
						BigInteger n=ca.getPublicKey().getN();
						BigInteger n2=ca.getPublicKey().getN().pow(2);
						BigInteger blindBallot=eb.getPublicRSAKey().encrypt(rndm).multiply(ballot);
						BigInteger signed=eb.sign(voterName,blindBallot).multiply(rndm.modInverse(eb.getPublicRSAKey().N));
						rand.nextBytes(me);
						BigInteger r=BigInteger.valueOf(3);//mm(new BigInteger(me)).abs();
						BigInteger s=BigInteger.valueOf(3);//BigInteger.probablePrime(25, rng);
						r=r.mod(n);
						//u=(g^r)*(s^n) mod (n^2)
						BigInteger u=(g.modPow(r,n2).multiply(s.modPow(n,n2))).mod(n2);
						int[] trials=bb.authenticateBallot(ballot,signed,voterName,x,u);
						BigInteger[] solutions=new BigInteger[2*trials.length];
						//We got out ballot authenticated, now we must solve the Trials for the ZKP
						for (int y=0;y<trials.length;y++) {
							//Solve for v
							solutions[2*y]=r.subtract(BigInteger.valueOf(trials[y]).multiply(BigInteger.valueOf(vote)));
							//Solve for w
							solutions[2*y+1]=s;
							for (int q=0;q<trials[y];q++) {
								solutions[2*y+1]=solutions[2*y+1].multiply(x0.modInverse(n2)).mod(n2);
							}
								
						}
						bb.solveTrials(solutions);
					}
				}
				else {
					//invalid voter
					JOptionPane.showMessageDialog(null,"I'm sorry, our records indicate that you are not registered to vote.");
				}
			}
			else if (input.charAt(0)=='b') {
				JOptionPane.showMessageDialog(null,bb.getBulletinBoard());
			}
			else if (input.charAt(0)=='r') {
				String results=eb.getVotes();
				JOptionPane.showMessageDialog(null,results);
				break;//This means the election's over
			}
		}
	}
}
