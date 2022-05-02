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

public class Main {
    public static void main(String[] args) {
        int yes = 0;
        int no = 0;
        while (true) {
            yes++;
            no *= yes >> 1;
            print("Number +%s", yes);
            System.out.println("Number -" + no);
            if (yes == 1000000) {
                break;
            }
            try {
                System.out.println("Throw exception");
                throw new Exception("Exception");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void print(String message, Object... args) {
        System.out.println(format(message, args));
    }

    public static String format(String message, Object... args) {
        return String.format(message, args);
    }

    public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
