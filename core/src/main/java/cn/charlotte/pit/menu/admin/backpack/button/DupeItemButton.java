package cn.charlotte.pit.menu.admin.backpack.button;

import cn.charlotte.pit.util.menu.buttons.DisplayButton;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;



public class DupeItemButton extends DisplayButton {

	public DupeItemButton() {
        super(new ItemStack(Material.ACACIA_DOOR_ITEM),true);
    }


	public DumpType isDupe() {
		return DumpType.SUCCESSFULLY;
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
