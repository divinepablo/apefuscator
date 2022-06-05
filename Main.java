import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        int yes = 55;
//        int no = 2;
        System.out.println(yes * 2);
        System.out.println(yes >> 3);
    }

    public String encrypt(String input) {
        byte[] a = input.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < a.length; i++) {
            a[i] += 16;
        }
        return new String(a);
    }

    public String decrypt(String input) {

        byte[] a = input.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < a.length; i++) {
            a[i] -= 16;
        }
        return new String(a);
    }
}
