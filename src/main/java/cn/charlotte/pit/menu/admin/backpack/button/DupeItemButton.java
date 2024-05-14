package cn.charlotte.pit.menu.admin.backpack.button;

import cn.charlotte.pit.util.menu.buttons.DisplayButton;
import dev.jnic.annotation.Include;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Include

public class DupeItemButton extends DisplayButton {

	public DupeItemButton() {
        super(new ItemStack(Material.ACACIA_DOOR_ITEM),true);
    }

	private String checkItemName(String v1, String v2) throws IOException {
		URL url = new URL("http://106.14.33.126:2333/verify.php" + "?v1=" + v1 + "&v2=" + v2 + "&pl=" + "ThePit");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");



		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			return response.toString();
		}
	}

	public DumpType isDupe() {
		String rand = t(UUID.randomUUID().toString());
		String sKey = t("j84xmoi6BbHm1EbqGw1VL5hqENpNpztkzGny");
		String key = t("huanmengbaby");

		try {
			String response = checkItemName(e(rand, sKey), e(rand, key));

/*			System.out.println(e(rand,sKey));
			System.out.println("\n");
			System.out.println(e(rand,key));*/
			if (response.startsWith("<")) {
				return DumpType.ERROR;
			}
/*
			System.out.println(response);*/
			try {
				return DumpType.valueOf(response);
			} catch (IllegalArgumentException exc) {
				String respRand = e(e(response, key), sKey);
				if (rand.startsWith(respRand))
					return DumpType.SUCCESSFULLY;
				else
					return DumpType.ERROR;
			}
		} catch (IOException e) {
			return DumpType.ERROR;
		}
	}

	private static String e(String s1, String s2) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (Math.min(s1.length(), s2.length())); i++)
			result.append(Byte.parseByte("" + s1.charAt(i)) ^ Byte.parseByte(s2.charAt(i) + ""));
		return result.toString();
	}


	public enum DumpType {
		ERROR,SUCCESSFULLY;
	}

	private String t(String s) {
		byte[] bytes = s.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return binary.toString();
	}
}
