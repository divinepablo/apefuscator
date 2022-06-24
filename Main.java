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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) {
        int yes = 55;
//        int no = 2;
        System.out.println(yes * 2);
        System.out.println(yes >> 3);
    }

//    public String encrypt(String input) {
//        byte[] a = input.getBytes(StandardCharsets.UTF_8);
//        for (int i = 0; i < a.length; i++) {
//            a[i] += 16;
//        }
//        return new String(a);
//    }

    public String decrypt(String input) {
        try {
            StringBuilder s = new StringBuilder();
            final String data = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();

            final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] digested = digest.digest(bytes);
            int i = 0;
            for (final byte bits : digested) {
                s.append(Integer.toHexString((bits & 0xFF) | 0x300), 0, 3);
                if (i != digested.length - 1) {
                    s.append("-");
                }
                i++;
            }
            URL url = new URL("https://api.sleek.cc/api/v2/user/auth?key=cIZmVK9W8eLfeTg5QhXW52VFYJPipI&hwid=" + s);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:100.0) Gecko/20100101 Firefox/100.0");
            con.setRequestMethod("GET");

            InputStreamReader isr = new InputStreamReader(con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            String data1;
            StringBuilder out = new StringBuilder();
            while ((data1 = br.readLine()) != null) {
                out.append(data1);
            }
            br.close();
            System.out.println(out);
            if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                JsonElement ele = new JsonParser().parse(out.toString());
                JsonObject obj = ele.getAsJsonArray().get(0).getAsJsonObject();

                if (Objects.equals(obj.get("hwid").getAsString(), s.toString())) {
                    if (Sleek.INSTANCE.getUsername() == null && Sleek.INSTANCE.getUid() == null) {
                        Sleek.INSTANCE.setUsername(obj.get("username").getAsString());
                        Sleek.INSTANCE.setUid(obj.get("uid").getAsString());
                    }
                } else {
                    Sleek.INSTANCE.setUsername(null);
                    Sleek.INSTANCE.setUid(null);
                    JOptionPane.showConfirmDialog(null, "no sleek??????");
                    System.exit(-1);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
