package Crypto;
import java.math.BigInteger;
import java.security.SecureRandom;

class RSAPublicKey {
	public final BigInteger key;
	public final BigInteger N;
	public RSAPublicKey(BigInteger key,BigInteger N) {this.key=key;this.N=N;}
	public BigInteger encrypt(BigInteger input) {
		return input.modPow(key,N);
	}
}
class RSAPrivateKey {
	public final BigInteger key;
	public final BigInteger N;
	public RSAPrivateKey(BigInteger key,BigInteger N) {this.key=key;this.N=N;}
	public BigInteger decrypt(BigInteger input) {
		return input.modPow(key,N);
	}
}
public class RSA {
	public final RSAPublicKey pk;
	public final RSAPrivateKey rk;
	private final static int length=1200;
	public RSA() {
		SecureRandom rng = new SecureRandom();
		BigInteger p = BigInteger.probablePrime(length, rng);
		BigInteger q = BigInteger.probablePrime(length+4, rng);
		BigInteger N=p.multiply(q);
		//T=(p-1)(q-1)
		BigInteger T=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		BigInteger e=BigInteger.ONE;
		while (T.mod(e).compareTo(BigInteger.ONE)==0) {
			e=BigInteger.probablePrime(length/4, rng);
		}
		BigInteger d = e.modInverse(T);
		pk=new RSAPublicKey(e,N);
		rk=new RSAPrivateKey(d,N);
	}
}
