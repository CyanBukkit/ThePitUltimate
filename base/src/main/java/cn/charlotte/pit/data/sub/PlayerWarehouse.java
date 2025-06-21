 package cn.charlotte.pit.data.sub;

import cn.charlotte.pit.data.deserializer.WarehouseDeserializer;
import cn.charlotte.pit.data.serializer.WarehouseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.mizukilab.pit.util.inventory.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 玩家仓库数据类
 * @Author: Araykal
 * @Date: 2025/6/21
 */
@JsonSerialize(using = WarehouseSerializer.class)
@JsonDeserialize(using = WarehouseDeserializer.class)
public class PlayerWarehouse {
    
    private Map<Integer, Inventory> warehouses;
    
    public PlayerWarehouse() {
        warehouses = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            warehouses.put(i, Bukkit.createInventory(null, 54, "寄存箱 #" + i));
        }
    }
    
    public static PlayerWarehouse deserialization(String string) {
        PlayerWarehouse warehouse = new PlayerWarehouse();
        
        if (string != null && !string.isEmpty()) {
            String[] warehouseData = string.split("\\|");
            for (int i = 0; i < warehouseData.length && i < 10; i++) {
                if (!warehouseData[i].isEmpty()) {
                    ItemStack[] items = InventoryUtil.stringToItems(warehouseData[i]);
                    warehouse.getWarehouse(i + 1).setContents(items);
                }
            }
        }
        
        return warehouse;
    }
    
    public Inventory getWarehouse(int warehouseId) {
        if (warehouseId < 1 || warehouseId > 10) {
            throw new IllegalArgumentException("寄存编号必须在1-10之间");
        }
        return warehouses.get(warehouseId);
    }
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            Inventory inv = warehouses.get(i);
            String warehouseData = InventoryUtil.itemsToString(inv.getContents());
            sb.append(warehouseData);
            if (i < 10) {
                sb.append("|");
            }
        }
        return sb.toString();
    }
    
    public int getItemCount(int warehouseId) {
        Inventory inv = getWarehouse(warehouseId);
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                count++;
            }
        }
        return count;
    }
    
    public boolean isEmpty(int warehouseId) {
        return getItemCount(warehouseId) == 0;
    }
    
    public Map<Integer, Inventory> getWarehouses() {
        return warehouses;
    }
    
    public void setWarehouses(Map<Integer, Inventory> warehouses) {
        this.warehouses = warehouses;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerWarehouse)) return false;
        PlayerWarehouse that = (PlayerWarehouse) o;
        return Objects.equals(warehouses, that.warehouses);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(warehouses);
    }
    
    @Override
    public String toString() {
        return "PlayerWarehouse{warehouses=" + warehouses + "}";
    }
} 