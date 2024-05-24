package cn.charlotte.pit.events.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.TradeData;
import cn.charlotte.pit.data.mail.Mail;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.item.type.MythicRepairKit;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.medal.impl.challenge.FirstBidMedal;
import cn.charlotte.pit.medal.impl.challenge.HighestBidMedal;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.ChatComponentBuilder;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.rank.RankUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Creator Misoryan
 * @Date 2021/5/25 16:30
 */
public class AuctionEvent implements IEvent, INormalEvent, Listener {

    private static final String prefix = "&6&l竞拍! &7";
    private static final double rate = 1.15;
    public static boolean isCustom = false;
    private static LotsData lots;
    private static List<BidHistory> bidHistories;
    private static List<UUID> allowedParticipants;
    private static BukkitRunnable runnable;
    private static Cooldown timer;
    private static boolean startByAdmin = false;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static LotsData getRandomLots() {
        try {
            List<LotsData> possibleLots = new ArrayList<>();
            //generate random lots start
            int randomItemAmount = RandomUtil.random.nextInt(2) + 1;
            possibleLots.add(
                    new LotsData(
                            new ItemStack[]{
                                    new ItemBuilder(Material.FEATHER)
                                            .name("&3时髦的羽毛")
                                            .lore("&e特殊物品",
                                                    "&7放于物品栏时,可以保护",
                                                    "&7背包内的神话物品不会在死亡后扣除生命.",
                                                    "&7&o此物品会在死亡后消耗")
                                            .internalName("funky_feather")
                                            .canTrade(true)
                                            .canSaveToEnderChest(true)
                                            .amount(randomItemAmount)
                                            .build()},
                            randomItemAmount * 2000,
                            0
                    ));
            possibleLots.add(
                    new LotsData(
                            new ItemStack[]{
                                    new ItemBuilder(Material.CACTUS)
                                            .name("&a哲学仙人掌")
                                            .lore("&e特殊物品",
                                                    "&7手持并右键可以从九件未附魔的",
                                                    "&7随机 &a神&c话&e之&6甲 &7选择其一.",
                                                    " ",
                                                    "&7(部分特殊颜色不可选择)")
                                            .internalName("cactus")
                                            .canTrade(true)
                                            .canSaveToEnderChest(true)
                                            .amount(randomItemAmount)
                                            .build()},
                            randomItemAmount * 2000,
                            0
                    ));
            int randomRenownAmount = RandomUtil.random.nextInt(2) + 2;
            possibleLots.add(
                    new LotsData(
                            null,
                            randomRenownAmount * 1000,
                            randomRenownAmount,
                            new ItemBuilder(Material.GOLD_BLOCK)
                                    .name("&e声望")
                                    .amount(randomRenownAmount)
                                    .build()
                    )
            );

            MythicLeggingsItem abstractPitItem = new MythicLeggingsItem();
            abstractPitItem.setLive(RandomUtil.random.nextInt(3) + 3);
            abstractPitItem.setMaxLive(abstractPitItem.getLive());
            Map<AbstractEnchantment, Integer> enchantments = new HashMap<>();
            List<AbstractEnchantment> list = ThePit.getInstance()
                    .getEnchantmentFactor()
                    .getEnchantments()
                    .stream()
                    .filter(abstractEnchantment -> abstractEnchantment.canApply(abstractPitItem.toItemStack()))
                    .collect(Collectors.toList());
            List<AbstractEnchantment> results = list.stream().filter(abstractEnchantment -> abstractEnchantment.getRarity() == EnchantmentRarity.AUCTION_LIMITED).collect(Collectors.toList());
            if (RandomUtil.hasSuccessfullyByChance(0.02)) {
                results = list.stream().filter(abstractEnchantment -> abstractEnchantment.getRarity() == EnchantmentRarity.AUCTION_LIMITED_RARE).collect(Collectors.toList());
            }
            enchantments.put((AbstractEnchantment) RandomUtil.helpMeToChooseOne(results.toArray()), 1);
            abstractPitItem.setEnchantments(enchantments);
            abstractPitItem.setTier(1);
            possibleLots.add(
                    new LotsData(
                            new ItemStack[]{abstractPitItem.toItemStack()},
                            4000,
                            0,
                            abstractPitItem.toItemStack()
                    )
            );

            possibleLots.add(new LotsData(
                    new ItemStack[]{
                            new ItemBuilder(Material.DIAMOND_HELMET).lore("&6从拍卖中获得").canTrade(true).deathDrop(true).canSaveToEnderChest(true).internalName("shopItem").buildWithUnbreakable(),
                            new ItemBuilder(Material.DIAMOND_CHESTPLATE).lore("&6从拍卖中获得").canTrade(true).deathDrop(true).canSaveToEnderChest(true).internalName("shopItem").buildWithUnbreakable(),
                            new ItemBuilder(Material.DIAMOND_LEGGINGS).lore("&6从拍卖中获得").canTrade(true).deathDrop(true).canSaveToEnderChest(true).internalName("shopItem").buildWithUnbreakable(),
                            new ItemBuilder(Material.DIAMOND_BOOTS).lore("&6从拍卖中获得").canTrade(true).deathDrop(true).canSaveToEnderChest(true).internalName("shopItem").buildWithUnbreakable(),
                    },
                    2000,
                    0,
                    new ItemBuilder(Material.DIAMOND_HELMET).name("&b钻石盔甲套装").lore("&7&o包含了完整一套的钻石盔甲.").build()
            ));
            //generate random lots end
            if (RandomUtil.hasSuccessfullyByChance(0.01)) {
                return new LotsData(
                        new ItemStack[]{MythicRepairKit.toItemStack0()},
                        10000,
                        0,
                        MythicRepairKit.toItemStack0()
                );
            }
            return possibleLots.get(RandomUtil.random.nextInt(possibleLots.size()));
        } catch (Throwable e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to init Enchant Leggings Auction Lots. (Not a problem if you saw it during server startup stage!)");
        }

        return new LotsData(
                new ItemStack[]{
                        new ItemBuilder(Material.FEATHER)
                                .name("&3时髦的羽毛")
                                .lore("&e特殊物品",
                                        "&7放于物品栏时,可以保护",
                                        "&7背包内的神话物品不会在死亡后扣除生命.",
                                        "&7&o此物品会在死亡后消耗")
                                .internalName("funky_feather")
                                .canTrade(true)
                                .canSaveToEnderChest(true)
                                .amount(1)
                                .build()},
                2000,
                0
        );
    }

    public static void sendMail(UUID uuid, Mail mail) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(uuid);
            profile.getMailData().sendMail(mail);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerProfile profile = PlayerProfile.getOrLoadPlayerProfileByUuid(uuid);
                    profile.getMailData().sendMail(mail);
                }
            }.runTaskAsynchronously(ThePit.getInstance());
        }
    }

    public double getRate() {
        return AuctionEvent.rate;
    }

    public LotsData getLots() {
        return AuctionEvent.lots;
    }

    public void setLots(LotsData lotsData) {
        AuctionEvent.lots = lotsData;
    }

    public List<BidHistory> getBidHistories() {
        return AuctionEvent.bidHistories;
    }

    public Cooldown getTimer() {
        return AuctionEvent.timer;
    }

    public void setTimer(Cooldown cooldown) {
        AuctionEvent.timer = cooldown;
        EventFactory eventFactory = ThePit.getInstance().getEventFactory();
        eventFactory.setNormalEnd(cooldown);
    }

    public void setStartByAdmin(boolean startByAdmin) {
        AuctionEvent.startByAdmin = startByAdmin;
    }

    @Override
    public String getEventInternalName() {
        return "auction";
    }

    @Override
    public String getEventName() {
        return "拍卖";
    }

    @Override
    public int requireOnline() {
        return NewConfiguration.INSTANCE.getEventOnlineRequired().get(getEventInternalName());
    }

    @Override
    public void onActive() {
        if (!isCustom || lots == null) {
            lots = getRandomLots();
        }

        //event settings init
        try {
            setTimer(new Cooldown(60, TimeUnit.SECONDS));
            bidHistories = new ArrayList<>();
            allowedParticipants = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> {
                allowedParticipants.add(player.getUniqueId());
                player.spigot()
                        .sendMessage(new ChatComponentBuilder(CC.translate(prefix + "当前正在竞拍: " + (lots.getIcon().getAmount() > 1 ? "&f" + lots.getIcon().getAmount() + "x " : "") + (lots.getIcon().getItemMeta().getDisplayName() == null ? "&f" + lots.getIcon().getType().toString() : lots.getIcon().getItemMeta().getDisplayName()) + " &8(&6" + (int) (getHighestBidHistory() == null ? getLots().getStartPrice() : getHighestBidHistory().getCoins()) + " 硬币&8) ")
                                ).append(new ChatComponentBuilder(CC.translate("&e&l点击查看"))
                                                .setCurrentHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.translate("&f点击访问拍卖行")).create()))
                                                .setCurrentClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/AuctionGui"))
                                                .create())
                                        .create()
                        );
            });
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getTimer().hasExpired()) {
                        ThePit.getInstance()
                                .getEventFactory()
                                .inactiveEvent(AuctionEvent.this);
                        return;
                    }
                    if (Math.floorDiv(getTimer().getRemaining(), 1000L) % 10 == 0 && getTimer().getRemaining() > 5 * 1000L) {
                        Bukkit.getOnlinePlayers().forEach(player ->
                                player.spigot()
                                        .sendMessage(new ChatComponentBuilder(CC.translate(prefix + "竞拍: " + (lots.getIcon().getAmount() > 1 ? "&f" + lots.getIcon().getAmount() + "x " : "") + (lots.getIcon().getItemMeta().getDisplayName() == null ? "&f" + lots.getIcon().getType().toString() : lots.getIcon().getItemMeta().getDisplayName()) + " &8(&6" + (int) (getHighestBidHistory() == null ? getLots().getStartPrice() : getHighestBidHistory().getCoins()) + " 硬币&8) &7将在 &e" + TimeUtil.millisToRoundedTime(getTimer().getRemaining()) + " &7后结束! "))
                                                .append(new ChatComponentBuilder(CC.translate("&e&l点击查看"))
                                                        .setCurrentHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.translate("&f点击访问拍卖行")).create()))
                                                        .setCurrentClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/AuctionGui"))
                                                        .create())
                                                .create()
                                        ));
                    }
                }
            };
            runnable.runTaskTimer(ThePit.getInstance(), 20, 20);
        } catch (Exception e) {
            Bukkit.getOnlinePlayers().forEach(player -> CC.printError(player, e));
        }
    }

    @Override
    public void onInactive() {
        isCustom = false;
        try {
            runnable.cancel();
        } catch (Exception ignored) {
        }
        if (!startByAdmin && ThePit.getInstance().getPitConfig().isGenesisEnable() && bidHistories.size() == 0) {
            return;
        }
        if (bidHistories.size() == 0) {
            CC.boardCast(prefix + "流拍! 无人参与竞拍.");
            return;
        }
        this.saveAuctionLogs();
        if (!getTimer().hasExpired()) {
            CC.boardCast(prefix + "拍卖被取消,竞拍硬币已全部退还.");
            getParticipants().forEach(uuid -> {
                        if (getHighestBidHistory(uuid) != null) {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && player.isOnline()) {
                                //if player is online,give back coins to the profile
                                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(uuid);
                                profile.setCoins(profile.getCoins() + (int) getHighestBidHistory(uuid).getCoins());
                                player.sendMessage(CC.translate(prefix + "你参与竞拍的 &6" + (int) getHighestBidHistory(player.getUniqueId()).getCoins() + " 硬币 &7已退还至账户中."));
                            } else {
                                //send mail to give back coins if player is offline
                                Mail mail = new Mail();
                                mail.setExpireTime(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
                                mail.setSendTime(System.currentTimeMillis());
                                mail.setCoins((int) getHighestBidHistory(uuid).getCoins());
                                mail.setTitle("&e【拍卖行】 竞拍硬币退还");
                                mail.setContent("&f你在 " + dateFormat.format(System.currentTimeMillis()) + " 参与的拍卖被取消, \\n&f于当时投入的硬币现已退还.");
                                sendMail(uuid, mail);
                            }
                        }
                    }
            );
            this.saveAuctionLogs();
            return;
        }
        if (getHighestBidHistory() != null) {
            BidHistory highestBid = getHighestBidHistory();
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(highestBid.getUuid());
            new HighestBidMedal().addProgress(profile, 1);
            CC.boardCast(prefix + "恭喜 " + profile.getFormattedName() + " &7以 &6" + (int) highestBid.getCoins() + " 硬币 &7的价格买下了 " + (lots.getIcon().getAmount() > 1 ? "&f" + lots.getIcon().getAmount() + "x " : "") + (lots.getIcon().getItemMeta().getDisplayName() == null ? "&f" + lots.getIcon().getType().toString() : lots.getIcon().getItemMeta().getDisplayName()) + " &7!");
            getParticipants().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        Mail mail = new Mail();
                        mail.setExpireTime(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
                        mail.setSendTime(System.currentTimeMillis());
                        if (highestBid.getUuid().equals(uuid)) {
                            PlayerInv contents = new PlayerInv();
                            contents.setContents(lots.getContents());
                            mail.setItem(contents);
                            mail.setRenown(lots.getRenown());
                            mail.setTitle("&e【拍卖行】 竞拍物品发放");
                            mail.setContent("&f你在 " + dateFormat.format(System.currentTimeMillis()) + " 参与的拍卖中竞拍成功.\\n&f请注意及时收取竞拍物品.");
                            sendMail(uuid, mail);
                            if (player != null && player.isOnline()) {
                                player.sendMessage(CC.translate(prefix + "你赢得了竞拍! 物品已发放至你的邮箱中,请及时收取."));
                            }
                        } else if (player != null && getHighestBidHistory(player.getUniqueId()) != null) {
                            if (player.isOnline()) {
                                PlayerProfile participantProfile = PlayerProfile.getPlayerProfileByUuid(uuid);
                                participantProfile.setCoins(participantProfile.getCoins() + (int) getHighestBidHistory(uuid).getCoins());
                                player.sendMessage(CC.translate(prefix + "参与竞拍的 &6" + (int) getHighestBidHistory(player.getUniqueId()).getCoins() + " 硬币 &7已直接退还到您的账户当中."));
                            } else {
                                mail.setCoins(getHighestBidHistory(player.getUniqueId()).getCoins());
                                mail.setTitle("&e【拍卖行】 竞拍硬币退还");
                                mail.setContent("&f你在 " + dateFormat.format(System.currentTimeMillis()) + " 参与的拍卖中未能拍下物品. \\n&f于当时投入的硬币现已退还.");
                                sendMail(uuid, mail);
                            }
                        }
                    }
            );
        }
    }

    public boolean playerBid(PlayerProfile profile, double paidCoins) {
        //check if player's profile is loaded
        try {
            if (!profile.isLoaded()) {
                if (profile.isLogin() && Bukkit.getPlayer(profile.getPlayerUuid()) != null && Bukkit.getPlayer(profile.getPlayerUuid()).isOnline()) {
                    Bukkit.getPlayer(profile.getPlayerUuid()).sendMessage(CC.translate("&c你当前无法进行此操作!"));
                }
                return false;
            }
            if (getTimer().hasExpired()) {
                return false;
            }
            Player player = Bukkit.getPlayer(profile.getPlayerUuid());
            BidHistory highestBid = getHighestBidHistory();
            double price = getParticipants().size() > 0 ? rate * highestBid.getCoins() : lots.getStartPrice();
            //check if player is holding highest bid
            if (highestBid != null) {
                if (System.currentTimeMillis() - highestBid.getTime() < 1000) {
                    player.sendMessage(CC.translate("&c拍卖出价刚刚发生了变动! 请重试!"));
                    return false;
                }
                if (highestBid.getUuid().equals(profile.getPlayerUuid())) {
                    return false;
                }
            }
            if (paidCoins > 0 && paidCoins < price) {
                player.sendMessage(CC.translate("&c你的出价过低!"));
                return false;
            }
            //if player hasn't bid before
            if (getHighestBidHistory(profile.getPlayerUuid()) == null) {
                //if player has enough coins
                //if player pay not enough coins with no-fast-bid way , cancel it
                if (profile.getCoins() >= Math.max(paidCoins, price)) {
                    profile.setCoins(profile.getCoins() - Math.max(paidCoins, price));
                    bidHistories.add(new BidHistory(profile.getPlayerUuid(), System.currentTimeMillis(), Math.max(paidCoins, price)));
                    StringBuilder alert = new StringBuilder(prefix);
                    alert.append("玩家 ")
                            .append(profile.getFormattedName())
                            .append(" &7以 &6")
                            .append((int) Math.max(paidCoins, price))
                            .append(" 硬币 &7的价格出价!");
                    if (getTimer().getRemaining() < 20 * 1000L) {
                        this.setTimer(new Cooldown(getTimer().getRemaining() + 10 * 1000L));
                        alert.append(" &a+10秒!");
                    }
                    CC.boardCast(alert.toString());
                    new FirstBidMedal().addProgress(profile, 1);
                    return true;
                } else {
                    return false;
                }
            } else { //if player has bid before
                BidHistory playerHighestBid = getHighestBidHistory(profile.getPlayerUuid());
                if (profile.getCoins() + playerHighestBid.getCoins() >= Math.max(paidCoins, price)) {
                    profile.setCoins(profile.getCoins() - (Math.max(paidCoins, price) - playerHighestBid.getCoins()));
                    bidHistories.add(new BidHistory(profile.getPlayerUuid(), System.currentTimeMillis(), Math.max(paidCoins, price)));
                    StringBuilder alert = new StringBuilder(prefix);
                    alert.append("玩家 ")
                            .append(profile.getFormattedName())
                            .append(" &7以 &6")
                            .append((int) Math.max(paidCoins, price))
                            .append(" 硬币 &7的价格加价!");
                    if (getTimer().getRemaining() < 20 * 1000L) {
                        this.setTimer(new Cooldown(getTimer().getRemaining() + 10 * 1000L));
                        alert.append(" &a+10秒!");
                    }
                    CC.boardCast(alert.toString());
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Bukkit.getPlayer(profile.getPlayerUuid()) != null && Bukkit.getPlayer(profile.getPlayerUuid()).isOnline()) {
                CC.printError(Bukkit.getPlayer(profile.getPlayerUuid()), e);
                Bukkit.getPlayer(profile.getPlayerUuid()).sendMessage(CC.translate("&c在竞价时出现了一个错误,请向管理员反馈!"));
            }
        }
        return false;
    }

    //fast bid
    public void playerBid(PlayerProfile profile) {
        playerBid(profile, 0);
    }

    public BidHistory getHighestBidHistory(UUID uuid) {
        final List<BidHistory> finalBidHistories = new ArrayList<>();
        for (BidHistory bidHistory : bidHistories) {
            if (bidHistory.getUuid().equals(uuid)) {
                finalBidHistories.add(bidHistory);
            }
        }
        if (finalBidHistories.size() == 0) {
            return null;
        }
        List<Double> bidCoins = new ArrayList<>();
        for (BidHistory bidHistory : finalBidHistories) {
            bidCoins.add(bidHistory.getCoins());
        }
        double max = Collections.max(bidCoins);
        for (BidHistory bidHistory : finalBidHistories) {
            if (bidHistory.getCoins() == max) {
                return bidHistory;
            }
        }
        return null;
    }

    public boolean isAllowedToParticipate(UUID uuid) {
        return allowedParticipants.contains(uuid);
    }

    public List<UUID> getParticipants() {
        List<UUID> participants = new ArrayList<>();
        for (BidHistory bidHistory : bidHistories) {
            if (!participants.contains(bidHistory.getUuid())) {
                participants.add(bidHistory.getUuid());
            }
        }
        return participants;
    }

    public BidHistory getHighestBidHistory() {
        if (bidHistories.size() == 0) {
            return null;
        }
        List<Double> bidCoins = new ArrayList<>();
        for (BidHistory bidHistory : bidHistories) {
            bidCoins.add(bidHistory.getCoins());
        }
        double max = Collections.max(bidCoins);
        for (BidHistory bidHistory : bidHistories) {
            if (bidHistory.getCoins() == max) {
                return bidHistory;
            }
        }
        return null;
    }

    public List<BidHistory> getBidHistories(UUID uuid) {
        List<BidHistory> playerBidHistories = new ArrayList<>(bidHistories);
        playerBidHistories.removeIf(bidHistory -> !bidHistory.getUuid().equals(uuid));
        return playerBidHistories;
    }

    public void saveAuctionLogs() {
        new BukkitRunnable() {
            @Override
            public void run() {
                BidHistory highestBid = getHighestBidHistory();
                getParticipants().forEach(uuid -> {
                    TradeData tradeData = new TradeData("auction", uuid.toString(), "[SYS] Auction", CC.stripColor(RankUtil.getPlayerRealColoredName(uuid)));
                    BidHistory playerHighestBid = getHighestBidHistory(uuid);
                    tradeData.setCompleteTime(System.currentTimeMillis());
                    tradeData.setBPaidCoin(((long) playerHighestBid.getCoins()));
                    PlayerInv bInv = new PlayerInv();
                    bInv.setContents(new ItemStack[36]);
                    tradeData.setBPaidItem(bInv);
                    //if player held highest bid
                    List<ItemStack> itemStacks = new ArrayList<>();
                    itemStacks.add(getLots().getIcon());
                    List<String> lines = new ArrayList<>();
                    lines.add("&7此玩家参与竞价次数: &e" + getBidHistories(uuid).size() + " 次");
                    lines.add("&7此玩家最高出价: &6" + (int) playerHighestBid.getCoins() + " 硬币");
                    lines.add("");
                    lines.add("&7本次拍卖竞价次数: &e" + getBidHistories().size() + " 次");
                    lines.add("&7全场最高出价: &6" + (int) highestBid.getCoins() + " 硬币");
                    lines.add("&7拍下物品者: " + RankUtil.getPlayerRealColoredName(uuid));
                    lines.add("");
                    lines.add("&7服务器: &a" + ThePit.getInstance().getServer().getName());
                    lines.add("&7时间: &f" + dateFormat.format(System.currentTimeMillis()));
                    itemStacks.add(new ItemBuilder(Material.MAP).name("&e拍卖信息").lore(lines).build());
                    if (!highestBid.getUuid().equals(uuid)) {
                        tradeData.setAPaidCoin(((long) playerHighestBid.getCoins()));
                        lines.add("");
                        lines.add("&8此玩家未能成功拍下物品,");
                        lines.add("&8交易记录中出现的物品仅为拍卖内容展示.");
                    }
                    PlayerInv aInv = new PlayerInv();
                    aInv.setContents(itemStacks.toArray(new ItemStack[36]));
                    tradeData.setAPaidItem(aInv);
                    tradeData.save();
                });
            }
        }.runTaskAsynchronously(ThePit.getInstance());
    }

    @Data
    @AllArgsConstructor
    public static class BidHistory {
        private UUID uuid;
        private long time;
        private double coins;
    }

    @Data
    public static class LotsData {
        private ItemStack[] contents;
        private double startPrice;
        private int renown;
        private ItemStack icon;

        public LotsData(ItemStack[] contents, double startPrice, int renown, ItemStack icon) {
            this.contents = contents;
            this.startPrice = startPrice;
            this.renown = renown;
            this.icon = icon;
        }

        public LotsData(ItemStack[] contents, double startPrice, int renown) {
            this.contents = contents;
            this.startPrice = startPrice;
            this.renown = renown;
            this.icon = contents[0].clone();
        }
    }
}
